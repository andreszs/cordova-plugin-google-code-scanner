package com.andreszs.gcs;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GoogleCodeScanner extends CordovaPlugin {
    private CallbackContext mCallbackContext;

    private static final String TAG = "BiometricAuth";

    @Override
    public boolean execute(String action, JSONArray jsonArgs, CallbackContext callbackContext) throws JSONException {
        mCallbackContext = callbackContext;

        if (action.equals("startScan")) {
            executeStartScan(jsonArgs);
            return true;
        } else if (action.equals("getBarcodeConstant")) {
            executeGetBarcodeConstant(jsonArgs);
            return true;
        } else {
            Log.e(TAG, String.format("Invalid action passed: %s", action));
            PluginResult pluginResult = new PluginResult(PluginResult.Status.INVALID_ACTION);
            callbackContext.sendPluginResult(pluginResult);
            return false;
        }
    }

    private void executeStartScan(JSONArray jsonArgs) {
        Log.d(TAG, "executeStartScan");
        int barcodeFormats = getInt(jsonArgs, "barcodeFormats", Barcode.FORMAT_ALL_FORMATS);

        cordova.getActivity().runOnUiThread(() -> {
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(barcodeFormats)
                    .build();

            // Configure options
            Context context = cordova.getContext();
            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(context, options);

            try {
                scanner
                        .startScan()
                        .addOnSuccessListener(
                                barcode -> {
                                    // Task completed successfully
                                    Log.w(TAG, "Code scanned");
                                    JSONObject jsonBarcode = getJsonFromBarcode(barcode);
                                    mCallbackContext.success(jsonBarcode);
                                })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    Log.e(TAG, e.getMessage());
                                    mCallbackContext.error(e.getMessage());
                                })
                        .addOnCanceledListener(() -> {
                            // Task canceled
                            Log.w(TAG, "Canceled by user");
                            mCallbackContext.error("Canceled");
                        });
            } catch (Exception e) {
                mCallbackContext.error(e.getMessage());
            }
        });

    }

    private void executeGetBarcodeConstant(JSONArray jsonArgs) {
        Log.d(TAG, "executeGetBarcodeConstant");
        String barcodeFormat = getString(jsonArgs, "barcodeFormat", null).toUpperCase();

        try {
            int formatValue = getBarcodeConstant(barcodeFormat);
            JSONObject jsonBarcodeConstant = new JSONObject();
            try {
                jsonBarcodeConstant.put("formatName", barcodeFormat);
                jsonBarcodeConstant.put("formatValue", formatValue);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                mCallbackContext.sendPluginResult(result);
            }
            mCallbackContext.success(jsonBarcodeConstant);
        } catch (Exception e) {
            mCallbackContext.error(e.getMessage());
        }
    }

    @NonNull
    private JSONObject getJsonFromBarcode(Barcode barcode) {
        JSONObject jsonBarcode = new JSONObject();
        try {
            int format = barcode.getFormat();
            jsonBarcode.put("formatValue", format);
            jsonBarcode.put("formatName", getConstantName(Barcode.class, format));
            jsonBarcode.put("rawValue", barcode.getRawValue());
            jsonBarcode.put("valueType", barcode.getValueType());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
            mCallbackContext.sendPluginResult(result);
        }
        return jsonBarcode;
    }

    private static Boolean getBoolean(@NonNull JSONArray jsonArray, String key, Boolean defaultValue) {
        try {
            if (!jsonArray.isNull(0)) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has(key) && !jsonObject.isNull(key)) {
                    return jsonObject.getBoolean(key);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Can't parse '" + key + "'. Default will be used.", e);
        }
        return defaultValue;
    }

    private static int getInt(@NonNull JSONArray jsonArray, String key, int defaultValue) {
        try {
            if (!jsonArray.isNull(0)) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has(key) && !jsonObject.isNull(key)) {
                    return jsonObject.getInt(key);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Can't parse '" + key + "'. Default will be used.", e);
        }
        return defaultValue;
    }

    private static String getString(@NonNull JSONArray jsonArray, String key, String defaultValue) {
        try {
            if (!jsonArray.isNull(0)) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has(key) && !jsonObject.isNull(key)) {
                    return jsonObject.getString(key);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Can't parse '" + key + "'. Default will be used.", e);
        }
        return defaultValue;
    }

    /**
     * @param name The Barcode constant name as String.
     * @return The constant int value.
     * @throws NullPointerException
     */
    private static int getBarcodeConstant(String name) throws NullPointerException {
        try {
            return (int) Barcode.class.getDeclaredField(name).get(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Constant value not found: Barcode." + name, e);
        }
    }

    /**
     * @param cls   The *.class which to traverse
     * @param value The constant value to look for
     */
    @Nullable
    private String getConstantName(@NonNull Class<?> cls, int value) throws NullPointerException {
        for (Field f : cls.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)) {
                try {
                    // Log.d(TAG, String.format("%s = %d%n", f.getName(), (int) f.get(null)));
                    if ((int) f.get(null) == value) {
                        return f.getName();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
