
#import <IOBluetooth/objc/IOBluetoothDevice.h>
#import <IOBluetooth/objc/IOBluetoothSDPUUID.h>
#import <IOBluetooth/objc/IOBluetoothRFCOMMChannel.h>
#import <IOBluetoothUI/objc/IOBluetoothDeviceSelectorController.h>

#import "DynamicServerAppDelegate.h"
#import "HTTPServer.h"
#import "MyHTTPConnection.h"
#import "DDLog.h"
#import "DDTTYLogger.h"
#import "NSString+Hex.h"

// Log levels: off, error, warn, info, verbose
static const int ddLogLevel = LOG_LEVEL_VERBOSE;

@interface DynamicServerAppDelegate () {
    IOBluetoothRFCOMMChannel *mRFCOMMChannel;
    IOBluetoothDevice *device;
}

@end

@implementation DynamicServerAppDelegate

@synthesize window;

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
	// Configure our logging framework.
	// To keep things simple and fast, we're just going to log to the Xcode console.
	[DDLog addLogger:[DDTTYLogger sharedInstance]];
	
	// Initalize our http server
	httpServer = [[HTTPServer alloc] init];
	
	// Tell server to use our custom MyHTTPConnection class.
	[httpServer setConnectionClass:[MyHTTPConnection class]];
	
	// Tell the server to broadcast its presence via Bonjour.
	// This allows browsers such as Safari to automatically discover our service.
//	[httpServer setType:@"_http._tcp."];
	
	// Normally there's no need to run our server on any specific port.
	// Technologies like Bonjour allow clients to dynamically discover the server's port at runtime.
	// However, for easy testing you may want force a certain port so you can just hit the refresh button.
	[httpServer setPort:12345];
	
	// Serve files from our embedded Web folder
	NSString *webPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"Web"];
	DDLogVerbose(@"Setting document root: %@", webPath);
	
	[httpServer setDocumentRoot:webPath];
	
	// Start the server (and check for problems)
	
	NSError *error;
	BOOL success = [httpServer start:&error];
	
	if(!success)
	{
		DDLogError(@"Error starting HTTP Server: %@", error);
	}
}

#pragma mark - Bluetooth shit

- (IBAction) discover:(id)sender
{
    IOBluetoothDeviceSelectorController  *deviceSelector;
    IOBluetoothSDPUUID                                      *sppServiceUUID;
    NSArray                                                         *deviceArray;
    IOBluetoothRFCOMMChannel *chan;
    
    [self log: @"Attempting to connect\n" ];
    
    // The device selector will provide UI to the end user to find a remote device
    deviceSelector = [IOBluetoothDeviceSelectorController deviceSelector];
    
    if ( deviceSelector == nil ) {
        [self log: @"Error - unable to allocate IOBluetoothDeviceSelectorController.\n" ];
        return;
    }
    
    sppServiceUUID = [IOBluetoothSDPUUID uuidWithData:[@"d749856c514348fe8b8635e4494bd073" hexToBytes]];
    [deviceSelector addAllowedUUID:sppServiceUUID];
    if ( [deviceSelector runModal] != kIOBluetoothUISuccess ) {
        NSLog( @"User has cancelled the device selection.\n" );
        return;
    }
    deviceArray = [deviceSelector getResults];
    if ( ( deviceArray == nil ) || ( [deviceArray count] == 0 ) ) {
        [self log: @"Error - no selected device.  ***This should never happen.***\n" ];
        return;
    }
    device = [deviceArray objectAtIndex:0];
    IOBluetoothSDPServiceRecord     *sppServiceRecord = [device getServiceRecordForUUID:sppServiceUUID];
    if ( sppServiceRecord == nil ) {
        [self log: @"Error - no spp service in selected device.  ***This should never happen since the selector forces the user to select only devices with spp.***\n" ];
        return;
    }
    // To connect we need a device to connect and an RFCOMM channel ID to open on the device:
    UInt8   rfcommChannelID;
    if ( [sppServiceRecord getRFCOMMChannelID:&rfcommChannelID] != kIOReturnSuccess ) {
        [self log: @"Error - no spp service in selected device.  ***This should never happen an spp service must have an rfcomm channel id.***\n" ];
        return;
    }
    
    // Open asyncronously the rfcomm channel when all the open sequence is completed my implementation of "rfcommChannelOpenComplete:" will be called.
    if ( ( [device openRFCOMMChannelAsync:&chan withChannelID:rfcommChannelID delegate:self] != kIOReturnSuccess ) && ( chan != nil ) ) {
        // Something went bad (looking at the error codes I can also say what, but for the moment let's not dwell on
        // those details). If the device connection is left open close it and return an error:
        [self log: @"Error - open sequence failed.***\n" ];
        [self close:nil];
        return;
    }
    
    mRFCOMMChannel = chan;
    
}

- (void)rfcommChannelOpenComplete:(IOBluetoothRFCOMMChannel*)rfcommChannel status:(IOReturn)error
{
    
    if ( error != kIOReturnSuccess ) {
        [self log:@"Error - failed to open the RFCOMM channel with error %08lx.\n"];
        
        return;
    }
    else{
        [self log:@"Connected\n"];
    }
    
}

- (void)rfcommChannelData:(IOBluetoothRFCOMMChannel*)rfcommChannel data:(void *)dataPointer length:(size_t)dataLength
{
    
    NSString  *message = [[NSString alloc] initWithBytes:dataPointer length:dataLength encoding:NSUTF8StringEncoding];
    [self log:message];
    
    if ([message isEqualToString:@"knocked"])
        self.knocked = YES;
    else
        self.passstring = message;
}

-(void)sendMessage:(NSString *)message
{
    [self log:@"Sending Message"];
    [self log:message];
    NSData *dataToSend = [message dataUsingEncoding:NSUTF8StringEncoding];
    [mRFCOMMChannel writeSync:(void*)dataToSend.bytes length:dataToSend.length];
}

- (IBAction)close:(id)sender;
{
    [self log:@"Closing"];
    [mRFCOMMChannel closeChannel];
    mRFCOMMChannel = nil;
    [device closeConnection];
    device = nil;
}

- (void)log:(NSString *)msg;
{
    NSLog(@"%@\n", msg);
}

@end
