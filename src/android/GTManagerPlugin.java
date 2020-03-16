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
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
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
* This class echoes a string called from JavaScript.
*/
public class GTManagerPlugin extends CordovaPlugin {

    private Activity mContext;
    private CallbackContext mCallbackContext;
    private SharedPreferences sharedPreferences;
    private static final long TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS = 100000;
    private static final String CONTAINER_ID = "GTM-MMZ4FVG";

    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mContext= cordova.getActivity();
        sharedPreferences = mContext.getSharedPreferences("FoodlionApp",mContext.getBaseContext().MODE_PRIVATE);
        
        //Get GA client id
        String clientId = GoogleAnalytics.getInstance(mContext).newTracker("UA-1002630-24").get("&cid");
        Log.d("GTManagerPlug.java GA client id retrieved", clientId);

        // save client id 
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gaClientId", clientId);
        editor.apply();

        initializeGTM();
        Log.d("GTMEvent","com.foodlion.mobile.GTManagerPlugin initialize()");
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        }
        return false;
    }

    private void echo(String message, CallbackContext callbackContext) {
        
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void initializeGTM() {
        Log.d(this.getClass().getSimpleName(), "Initializing GTM");
        // Initialize Singleton
        TagManager tagManager = TagManager.getInstance(mContext.getApplicationContext());

        // Initialize Default Container
        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(CONTAINER_ID,
                        R.raw.gtm_android_binary_default);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the 2-second timeout occurs
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {

            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();

                Log.d("GTMContainer", "Container returned " + containerHolder.getStatus());

                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e(this.getClass().getSimpleName(), "failure loading container");
                    //					displayErrorToUser(R.string.load_error);
                    Log.d(this.getClass().getSimpleName(), "Container failed: "
                            + containerHolder.getStatus().getStatusCode() + " - "
                            + containerHolder.getStatus().getStatusMessage());
                    return;
                }
                ContainerLoadedCallback.registerCallbacksForContainer(container);
                // containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
                containerHolder.setContainerAvailableListener(new ContainerHolder.ContainerAvailableListener() {
                    @Override
                    public void onContainerAvailable(ContainerHolder containerHolder, String s) {
                        //get ga client id
                        String clientId = sharedPreferences.getString("gaClientId", "");
                        // This dataLayer push must be the first dataLayer push that happens. This push contains campaign information, client id information, tracking id information.
                        DataLayer dataLayer = TagManager.getInstance(mContext.getApplicationContext()).getDataLayer();
                        dataLayer.push(DataLayer.mapOf(
                                "event", "app-init",
                                "clientId", clientId
                        ));
                        Log.d("GTMEvent", "Container app-init event pushed");
                        Log.d("GTManagerPlugin.java GA client id", clientId);
                    }
                });
                // Intent intent = new Intent(MFPApplication.this, MainActivity.class);
                // startActivity(intent);
            }
        }, TIMEOUT_FOR_CONTAINER_OPEN_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            registerCallbacksForContainer(container);
        }

        public static void registerCallbacksForContainer(Container container) {
            // Register two custom function call macros to the container.
            container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
            container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
            // Register a custom function call tag to the container.
            container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());
        }

        private static class CustomMacroCallback implements Container.FunctionCallMacroCallback {
            private int numCalls;

            @Override
            public Object getValue(String name, Map<String, Object> parameters) {
                if ("increment".equals(name)) {
                    return ++numCalls;
                } else if ("mod".equals(name)) {
                    return (Long) parameters.get("key1") % Integer.valueOf((String) parameters.get("key2"));
                } else {
                    throw new IllegalArgumentException("Custom macro name: " + name + " is not supported.");
                }
            }
        }

        private static class CustomTagCallback implements Container.FunctionCallTagCallback {
            @Override
            public void execute(String tagName, Map<String, Object> parameters) {
                // The code for firing this custom tag.
                Log.i(this.getClass().getSimpleName(), "Custom function call tag :" + tagName + " is fired.");
            }
        }
    }
}