#import <UIKit/UIKit.h>
#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>
@class TAGManager;
@class TAGContainer;


@interface GTManagerPlugin : CDVPlugin <TAGContainerOpenerNotifier> {
}

// - (void)coolMethod:(CDVInvokedUrlCommand*)command;
@property (nonatomic, strong) TAGManager *tagManager;
@property (nonatomic, strong) TAGContainer *container;





@end
