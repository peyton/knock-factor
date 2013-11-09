#import "MyHTTPConnection.h"
#import "HTTPDataResponse.h"
#import "HTTPDynamicFileResponse.h"
#import "HTTPResponseTest.h"
#import "HTTPLogging.h"
#import "DynamicServerAppDelegate.h"

// Log levels: off, error, warn, info, verbose
// Other flags: trace
static const int httpLogLevel = HTTP_LOG_LEVEL_WARN; // | HTTP_LOG_FLAG_TRACE;

NSObject<HTTPResponse> *stringResponse(NSString *string);

@implementation MyHTTPConnection

- (NSObject<HTTPResponse> *)httpResponseForMethod:(NSString *)method URI:(NSString *)path
{
	// Use HTTPConnection's filePathForURI method.
	// This method takes the given path (which comes directly from the HTTP request),
	// and converts it to a full path by combining it with the configured document root.
	// 
	// It also does cool things for us like support for converting "/" to "/index.html",
	// and security restrictions (ensuring we don't serve documents outside configured document root folder).
	
	NSString *filePath = [self filePathForURI:path];
	
	// Convert to relative path
	
	NSString *documentRoot = [config documentRoot];
	
	if (![filePath hasPrefix:documentRoot])
	{
		// Uh oh.
		// HTTPConnection's filePathForURI was supposed to take care of this for us.
		return nil;
	}
	
	NSString *relativePath = [filePath substringFromIndex:[documentRoot length]];
	
    if ([relativePath isEqualToString:@"/knocked"])
    {
        if ([(DynamicServerAppDelegate *)[NSApp delegate] knocked])
        {
            [(DynamicServerAppDelegate *)[NSApp delegate] setKnocked:NO];
            return stringResponse(@"yes");
        }
        
        return stringResponse(@"no");
    }
    
    if ([relativePath isEqualToString:@"/request"])
    {
        [(DynamicServerAppDelegate *)[NSApp delegate] setPassstring:nil];
        
        
        HTTPLogVerbose(@"Requesting token");
        
        if (![method isEqualToString:@"GET"])
            return stringResponse(@"error: only GET is supported");
        
        NSDictionary *params = [self parseGetParams];
        
        if (![params objectForKey:@"hostname"])
            return stringResponse(@"error: missing hostname parameter");
        
        NSString *hostname = [params objectForKey:@"hostname"];
        
        [(DynamicServerAppDelegate *)[NSApp delegate] sendMessage:hostname];
        
        return [[HTTPDataResponse alloc] initWithData:[hostname dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    if ([relativePath isEqualToString:@"/passstring"])
    {
        if (![(DynamicServerAppDelegate *)[NSApp delegate] passstring])
            return stringResponse(@"nothing yet");
        
        return stringResponse([(DynamicServerAppDelegate *)[NSApp delegate] passstring]);
    }
	
	return [super httpResponseForMethod:method URI:path];
}

@end

NSObject<HTTPResponse> *stringResponse(NSString *string) {
    return [[HTTPDataResponse alloc] initWithData:[string dataUsingEncoding:NSUTF8StringEncoding]];
}
