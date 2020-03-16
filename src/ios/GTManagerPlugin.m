#import <Cordova/CDV.h>
#import "TAGContainer.h"
#import "TAGContainerOpener.h"
#import "TAGManager.h"
#import "TAGDataLayer.h"
#import "GTManagerPlugin.h"
#import "GAIFields.h"
#import "GAI.h"

@interface GTManagerPlugin () {
    TAGManager *_tagManager;
    TAGContainer *_container;

}

@end


@implementation GTManagerPlugin

- (void)pluginInitialize {

    id<GAITracker> tracker = [[GAI sharedInstance] trackerWithTrackingId:@"UA-1002630-24"]; 
    NSString *gaClientID = [tracker get:kGAIClientId];
    NSString *clientIdKey = @"gaClientId";

    [[NSUserDefaults standardUserDefaults] setObject:gaClientID forKey:clientIdKey];
    
    self.tagManager = [TAGManager instance];

    // Optional: Change the LogLevel to Verbose to enable logging at VERBOSE and higher levels.
    [self.tagManager.logger setLogLevel:kTAGLoggerLogLevelError]; // kTAGLoggerLogLevelVerbose
    
    /*
     * Opens a container.
     *
     * @param containerId The ID of the container to load.
     * @param tagManager The TAGManager instance for getting the container.
     * @param openType The choice of how to open the container.
     * @param timeout The timeout period (default is 2.0 seconds).
     * @param notifier The notifier to inform on container load events.
     */
    [TAGContainerOpener openContainerWithId:@"GTM-PS2GMGX"   // Update with your Container ID.
                                 tagManager:self.tagManager
                                   openType:kTAGOpenTypePreferFresh
                                    timeout:nil
                                   notifier:self];
    
    NSString *clientID = [[NSUserDefaults standardUserDefaults] stringForKey: clientIdKey];
    
    [[self.tagManager dataLayer] push:@{
        @"event": @"app-init",
        @"clientId": clientID }];
    NSLog(@"GTMEvent: GTManagerPlugin.pluginInitialize()");
}

// TAGContainerOpenerNotifier callback.
- (void)containerAvailable:(TAGContainer *)container {
    // Note that containerAvailable may be called on any thread, so you may need to dispatch back to
    // your main thread.
    dispatch_async(dispatch_get_main_queue(), ^{
        self.container = container;
    });
}




@end
