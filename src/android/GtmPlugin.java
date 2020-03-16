package com.foodlion.mobile;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import android.content.SharedPreferences;
import android.app.Activity;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.foodlion.mobile.R;
import com.foodlion.mobile.ContainerHolderSingleton;
import com.foodlion.mobile.MainActivity;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import android.content.Context;
/**
 * Gtm Plugin the primary class where the implementation is contained
 *
 * @author IBM Baton Rouge team
 */

public class GtmPlugin extends CordovaPlugin {
	private Activity mContext;
    private CallbackContext mCallbackContext;
    private SharedPreferences sharedPreferences;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
        mContext= cordova.getActivity();
        Log.d("GTM Plugin","com.foodlion.mobile.GtmPlugin initialize()");
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		mCallbackContext = callbackContext;
		if(action.equals("getGtmClientId")){
			getGtmClientId(args, callbackContext);
		}
		return true;
	}

	private void getGtmClientId(JSONArray args, CallbackContext callbackContext) throws JSONException{

        sharedPreferences = mContext.getSharedPreferences("FoodlionApp",mContext.getBaseContext().MODE_PRIVATE);
        String clientId = sharedPreferences.getString("gaClientId", "");
        if (clientId != null && !clientId.isEmpty()) {
            callbackContext.success(clientId);
        } else {
            callbackContext.error("Error getting GA client id for Android");
        }
	}
}