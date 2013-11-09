//
//  CustomHTTPResponse.m
//  DynamicServer
//
//  Created by Peyton Randolph on 11/9/13.
//
//

#import "CustomHTTPResponse.h"

@implementation CustomHTTPResponse

- (NSDictionary *)httpHeaders;
{
    return @{@"Access-Control-Allow-Origin" : @"*"};
}

@end
