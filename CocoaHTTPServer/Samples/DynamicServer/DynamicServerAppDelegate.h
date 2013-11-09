#import <Cocoa/Cocoa.h>

@class HTTPServer;


@interface DynamicServerAppDelegate : NSObject <NSApplicationDelegate>
{
	HTTPServer *httpServer;
	
	NSWindow *__unsafe_unretained window;
}

@property (unsafe_unretained) IBOutlet NSWindow *window;

@property (nonatomic, copy) NSString *passstring;
@property (nonatomic, assign) BOOL knocked;

- (IBAction)discover:(id)sender;
- (IBAction)close:(id)sender;

-(void)sendMessage:(NSString *)message;

@end
