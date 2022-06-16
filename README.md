![npm](https://img.shields.io/npm/dt/cordova-plugin-google-code-scanner) ![npm](https://img.shields.io/npm/v/cordova-plugin-google-code-scanner) ![GitHub package.json version](https://img.shields.io/github/package-json/v/andreszs/cordova-plugin-google-code-scanner?color=FF6D00&label=master&logo=github) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/andreszs/cordova-plugin-google-code-scanner) ![GitHub top language](https://img.shields.io/github/languages/top/andreszs/cordova-plugin-google-code-scanner) ![GitHub](https://img.shields.io/github/license/andreszs/cordova-plugin-google-code-scanner) ![GitHub last commit](https://img.shields.io/github/last-commit/andreszs/cordova-plugin-google-code-scanner)

# cordova-plugin-google-code-scanner

The [Google code scanner API](https://developers.google.com/ml-kit/code-scanner) provides a complete solution for scanning codes without requiring your app to request camera permission, while preserving user privacy. This is accomplished by delegating the task of scanning the code to Google Play services and returning only the scan results to your app.


# Platforms

- Android 5+ (minSDK 21)
- Browser (filler platform)

# Installation

Install the plugin from NPM:
```bash
cordova plugin add cordova-plugin-google-code-scanner
```

By default plugin is installed with [play-services-code-scanner](https://maven.google.com/web/index.html?q=play-services-code-scanner#com.google.android.gms:play-services-code-scanner "play-services-code-scanner") version 16.0.0-beta1. To install with a newer version in the future use the **PLAY_SERVICES_CGS_VERSION** variable as follows:

```bash
cordova plugin add cordova-plugin-google-code-scanner --variable PLAY_SERVICES_CGS_VERSION="16.0.0-beta1"
```

# Methods

## startScan

Opens the code scanner view to scan barcode.

```javascript
cordova.plugins.GoogleCodeScanner.startScan(successCallback, errorCallback, [options])
```

| **options** | |
| --- | --- |
| barcodeFormats | **int**: An optional bit field representing the accepted barcode formats as defined in [Barcode.BarcodeFormat](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode.BarcodeFormat). |

If you know which barcode formats you expect to read, you can improve the speed of the barcode detector by configuring it to only detect those formats. For example, to detect only Aztec code and QR codes, set **barcodeFormats** to **4352** (256 + 4096).

### Success callback return values

JSON object with the following properties:

| **jsonBarcode** | |
| --- | --- |
| rawValue | **String**:  the barcode's raw, unmodified, and uninterpreted content |
| formatName | **String**: the barcode type name. E.g: `FORMAT_EAN_13`. |
| formatValue | **int**:  the barcode format type (i.e. its encoding) constant value. |
| valueType | **int**: the format type of the barcode value. |


### Error callback return values

- **String**: The error description. On initial use, this message equals `Waiting for the Barcode UI module to be downloaded.`

The first time **startScan** is invoked, the error callback will notify you that the barcode UI module is being downloaded in the background, if it has not already been installed for another use case. It's up to you to detect and handle this first-time use error. To handle this, it would be wise to show a [loading spinner](https://github.com/greybax/cordova-plugin-native-spinner "loading spinner"), wait a few seconds, and retry the scan after the module was downloaded.

### Example 1

Scan code in any format and catch the error whenever the UI module was not yet downloaded:

```javascript
var onSuccess = function (jsonBarcode) {
	var rawValue = jsonBarcode.rawValue;
	var formatName = jsonBarcode.formatName;
	var formatValue = jsonBarcode.formatValue;
	var valueType = jsonBarcode.valueType;
	// Do things with the code.
};
var onError = function (strError) {
	if(strError == 'Waiting for the Barcode UI module to be downloaded.'){
		// Downloading barcode UI: consider showing a full-screen spinner, and auto-retry scan in a few seconds.
	}
	console.error(strError);
};
cordova.plugins.GoogleCodeScanner.startScan(onSuccess, onError);
```

### Example 2

Scan only QR codes:

```javascript
var onSuccess = function (jsonBarcode) {
	var rawValue = jsonBarcode.rawValue;
	// Do things with the code.
};
var onError = function (strError) {
	console.error(strError);
};
var options = {};
options.barcodeFormats = cordova.plugins.GoogleCodeScanner.BarcodeFormat.FORMAT_QR_CODE;
cordova.plugins.GoogleCodeScanner.startScan(onSuccess, onError, options);
```

### Example 3

Scan either EAN8 or EAN13 barcodes:

```javascript
var onSuccess = function (jsonBarcode) {
	var rawValue = jsonBarcode.rawValue;
	// Do things with the code.
};
var onError = function (strError) {
	console.error(strError);
};
var options = {};
options.barcodeFormats = cordova.plugins.GoogleCodeScanner.BarcodeFormat.FORMAT_EAN_8 + cordova.plugins.GoogleCodeScanner.BarcodeFormat.FORMAT_EAN_13 ;
cordova.plugins.GoogleCodeScanner.startScan(onSuccess, onError, options);
```

## getBarcodeConstant

Retrieve the Barcode format constant value by its String name.

```javascript
cordova.plugins.GoogleCodeScanner.getBarcodeConstant(onSuccess, onError, options);
```

| **options** | |
| --- | --- |
| barcodeFormat | **String**: The [Barcode format constant](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode#constants) name to query. Eg: `FORMAT_AZTEC` |

### Success callback return values

JSON object with the following properties:

| **jsonConstant** | |
| --- | --- |
| formatName | **String**: the barcode type name. E.g: `FORMAT_EAN_13`. |
| formatValue | **int**:  the barcode type (i.e. its encoding) constant value. |

### Example

```javascript
var onSuccess = function (jsonBarcode) {
	var formatName = jsonBarcode.formatName;
	var formatValue = jsonBarcode.formatValue;
};
var onError = function (strError) {
	console.error(strError);
};
var options = {};
options.barcodeFormat = "FORMAT_DATA_MATRIX";
cordova.plugins.GoogleCodeScanner.getBarcodeConstant(onSuccess, onError, options);
```
# Predefined barcode formats

All formats from the ML Kit [Barcode](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode "Barcode") class are supported.

The following barcode constants are pre-defined by the plugin:

```javascript
cordova.plugins.GoogleCodeScanner.BarcodeFormat {
	FORMAT_ALL_FORMATS: 0,
	FORMAT_CODE_128: 1,
	FORMAT_CODE_39: 2,
	FORMAT_CODE_93: 4,
	FORMAT_CODABAR: 8,
	FORMAT_DATA_MATRIX: 16,
	FORMAT_EAN_13: 32,
	FORMAT_EAN_8: 64,
	FORMAT_ITF: 128,
	FORMAT_QR_CODE: 256,
	FORMAT_UPC_A: 512,
	FORMAT_UPC_E: 1024,
	FORMAT_PDF417: 2048,
	FORMAT_AZTEC: 4096
}
```

# Remarks

- The plugin uses Google Code Scanner [16.0.0-beta1](https://maven.google.com/web/index.html?q=play-services-code-scanner#com.google.android.gms:play-services-code-scanner "16.0.0-beta1") by default. This is the first release.
- To use a newer version in the future, the install accepts the **PLAY_SERVICES_CGS_VERSION** parameter.
- It seems that whenever Play Store app is disabled, the UI module cannot be downloaded. Further tests required to verify this.
- If you are viewing this README in NPM, there is probably a more up-to-date version in [GitHub](https://github.com/andreszs/cordova-plugin-google-code-scanner "GitHub").

# Plugin demo app

Under construction, please check back soon.

# Contributing

Please report any issue with this plugin in GitHub by providing detailed context and sample code.
PRs to improve and add new features or platforms are always welcome.

