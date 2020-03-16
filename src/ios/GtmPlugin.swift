import UIKit

@objc(GtmPlugin) class GtmPlugin : CDVPlugin {
    
    override func pluginInitialize() {

    }
    
    @objc(getGtmClientId:)
    func getGtmClientId(_ command: CDVInvokedUrlCommand) {
        let pluginResult: CDVPluginResult
        let clientIdKey = "gaClientId"
        //  Look in UserDefaults for the Google Analytics Client ID.
        let defaults = UserDefaults.standard
        let clientId = defaults.object(forKey: clientIdKey)
        
        if (clientId != nil) {
            pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: clientId as! String)
        } else {
            pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "")
        }
        
        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }
}
