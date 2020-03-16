var exec = require('cordova/exec');
 

function GtmPlugin() {
	console.log("GtmPlugin.js: is created");
}

//method to get the gtm client id if already set
GtmPlugin.prototype.getGtmClientId = function(successCallback, errorCallback){
	
	exec(successCallback,errorCallback, "GtmPlugin", "getGtmClientId");
}

GtmPlugin.install = function () {
	  if (!window.plugins) {
	    window.plugins = {};
	  }

	  window.plugins.gtmPlugin = new GtmPlugin();

	  return window.plugins.gtmPlugin;
	};

cordova.addConstructor(GtmPlugin.install);	