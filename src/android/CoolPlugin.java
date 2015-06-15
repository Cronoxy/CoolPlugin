import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import android.provider.Settings;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CoolPlugin extends CordovaPlugin {
    public static final String TAG = "Cool Plugin";

    private CallbackContext canvasCameraCallback;

    private static final int CANVAS_CAMERA = 2;
    public static final String QUALITY = "_quality";
    public static final String DESTTYPE = "_destType";
    public static final String ENCODETYPE = "_encodeType";
    public static final String SAVETOPHOTOALBUM = "_saveToPhotoAlbum";
    public static final String CORRECTORIENTATION = "_correctOrientation";
    public static final String WIDTH = "_width";
    public static final String HEIGHT = "_height";
    public static final String ALLOWEDIT = "_allowEdit";
    public static final String FLASH = "flash";
    public static final String REVERT = "revert";

    // parameter
    public static final String kQualityKey          = "quality";
    public static final String kDestinationTypeKey = "destinationType";
    public static final String kEncodingTypeKey     = "encodingType";

    public static final String kSaveToPhotoAlbumKey     = "saveToPhotoAlbum";
    public static final String kCorrectOrientationKey = "correctOrientation";

    public static final String kWidthKey        = "width";
    public static final String kHeightKey       = "height";

    // options
    private int _quality = 85;
    private int _destType = DestinationTypeDataURL;
    private boolean _allowEdit = false;
    private int _encodeType = EncodingTypeJPEG;
    private boolean _saveToPhotoAlbum = false;
    private boolean _correctOrientation = true;

    private int _width = 640;
    private int _height = 480;

    public static final int DONE=1;
    public static final int NEXT=2;
    public static final int PERIOD=1;

    public static CoolPlugin sharedCanvasCamera = null;

    // DestinationType
    public static final int DestinationTypeDataURL = 0;
    public static final int DestinationTypeFileURI = 1;

    // EncodingType
    public static final int EncodingTypeJPEG = 0;
    public static final int EncodingTypePNG = 1;

    // CameraPosition
    public static final int CameraPositionBack = 1;
    public static final int CameraPositionFront = 2;

    /**
    * Constructor.
    */
    public CoolPlugin() {}

    /**
    * Sets the context of the Command. This can then be used to do things like
    * get file paths associated with the Activity.
    *
    * @param cordova The context of the main Activity.
    * @param webView The CordovaWebView Cordova is running in.
    */
    public void initialize(CordovaInterface cordova, CordovaWebView webView)
    {
        super.initialize(cordova, webView);
        Log.v(TAG,"Init CoolPlugin");
    }

    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException
    {
        sharedCanvasCamera = this;
        this.canvasCameraCallback = callbackContext;

        if ("startCapture".equals(action)) {
            if (args.length() > 0)
                startCapture(args);

            return true;
        }

        final int duration = Toast.LENGTH_SHORT;
        // Shows a toast
        Log.v(TAG,"CoolPlugin received:"+ action);
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(cordova.getActivity().getApplicationContext(), action, duration);
                toast.show();
            }
        });

        return true;
    }

    private void startCapture(JSONArray args)
    {
        // init parameters - default values
        _quality = 85;
        _destType = DestinationTypeFileURI;
        _encodeType = EncodingTypeJPEG;
        _saveToPhotoAlbum = false;
        _correctOrientation = true;
        _width = 320;
        _height = 240;

        // parse options
        if (args.length() > 0) {
            try {
                JSONObject jsonData = args.getJSONObject(0);
                getOptions(jsonData);
            } catch (Exception e) {
                Log.d("CoolPlugin", "Parsing options error : " + e.getMessage());
            }
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.cordova.getActivity()).edit();
        editor.putInt(QUALITY, _quality);
        editor.putInt(DESTTYPE, _destType);
        editor.putInt(ENCODETYPE, _encodeType);
        editor.putBoolean(SAVETOPHOTOALBUM, _saveToPhotoAlbum);
        editor.putBoolean(CORRECTORIENTATION, _correctOrientation);
        editor.putInt(WIDTH, _width);
        editor.putInt(HEIGHT, _height);
        editor.commit();

  		//Intent intent = new Intent(this.cordova.getActivity(), CanvasCameraView.class);
   		//this.cordova.startActivityForResult(this, intent, CANVAS_CAMERA);
    }

    /**
    * parse options parameter and set it to local variables
    *
    */
    private void getOptions(JSONObject jsonData) throws Exception
    {
        if (jsonData == null)
            return;

        // get parameters from argument.

        // quaility
        String obj = jsonData.getString(kQualityKey);
        if (obj != null)
            _quality = Integer.parseInt(obj);

        // destination type
        obj = jsonData.getString(kDestinationTypeKey);
        if (obj != null)
        {
            int destinationType = Integer.parseInt(obj);
            _destType = destinationType;
        }

        // encoding type
        obj = jsonData.getString(kEncodingTypeKey);
        if (obj != null)
        {
            int encodingType = Integer.parseInt(obj);
            _encodeType = encodingType;
        }

        // width
        obj = jsonData.getString(kWidthKey);
        if (obj != null)
        {
            _width = Integer.parseInt(obj);
        }

        // height
        obj = jsonData.getString(kHeightKey);
        if (obj != null)
        {
            _height = Integer.parseInt(obj);
        }

        // saveToPhotoAlbum
        obj = jsonData.getString(kSaveToPhotoAlbumKey);
        if (obj != null)
        {
            _saveToPhotoAlbum = Boolean.parseBoolean(obj);
        }

        // correctOrientation
        obj = jsonData.getString(kCorrectOrientationKey);
        if (obj != null)
        {
            _correctOrientation = Boolean.parseBoolean(obj);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
    	if (resultCode == Activity.RESULT_OK) {
    		if (requestCode == CANVAS_CAMERA) {
    	        if (intent == null) {
    	        	canvasCameraCallback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Error: data is null"));
    	        } else {
            		cordova.getThreadPool().execute(new Runnable() {
        	            public void run()
        	            {
        	        		canvasCameraCallback.success();
        	            }
        			});
   		        }
   			}
   		}
   	}
}