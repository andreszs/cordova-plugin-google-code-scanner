![npm downloads](https://img.shields.io/npm/dt/cordova-plugin-google-code-scanner) ![npm version](https://img.shields.io/npm/v/cordova-plugin-google-code-scanner) ![GitHub package.json version](https://img.shields.io/github/package-json/v/andreszs/cordova-plugin-google-code-scanner?color=FF6D00&label=main&logo=github) [![Android demo](https://img.shields.io/badge/demo-Android-2ea44f?logo=apachecordova&logoColor=white)](https://github.com/andreszs/cordova-plugin-demos/tree/main/com.andreszs.gcs.demo) ![GitHub license](https://img.shields.io/github/license/andreszs/cordova-plugin-google-code-scanner)

# cordova-plugin-google-code-scanner

The [Google code scanner API](https://developers.google.com/ml-kit/code-scanner) provides a complete solution for scanning codes without requiring your app to request camera permission, while preserving user privacy. This is accomplished by delegating the task of scanning the code to Google Play services and returning only the scan results to your app.

## Table of contents

- [Platforms](#platforms)
- [Installation](#installation)
- [Methods](#methods)
  - [`startScan`](#startscan)
  - [`getBarcodeConstant`](#getbarcodeconstant)
- [Predefined barcode formats](#predefined-barcode-formats)
- [Remarks](#remarks)
- [Plugin demo app](#plugin-demo-app)
- [Contributing](#contributing)

# Platforms

- Android 5+ (minSDK 21)
- Browser (filler platform)

# Installation

Install the plugin from NPM:
```bash
cordova plugin add cordova-plugin-google-code-scanner
```

By default plugin is installed with [play-services-code-scanner](https://maven.google.com/web/index.html?q=play-services-code-scanner#com.google.android.gms:play-services-code-scanner "play-services-code-scanner") version 16.1.0. To install with a newer version in the future use the **PLAY_SERVICES_GCS_VERSION** variable as follows:

```bash
cordova plugin add cordova-plugin-google-code-scanner --variable PLAY_SERVICES_GCS_VERSION="16.1.0"
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

JSON object as follows:

| **jsonBarcode** | |
| --- | --- |
| rawValue | **String**:  the barcode's raw, unmodified, and uninterpreted content. |
| formatName | **String**: the barcode type name. E.g: `FORMAT_EAN_13`. |
| formatValue | **int**:  the barcode format type (i.e. its encoding) constant value. |
| valueType | **int**: the format type of the barcode value. |

### Error callback return values

- **Object** containing:
  - `errorCode`: numeric ML Kit error code, or `-1` for module installation errors.
  - `errorName`: stable error name such as `CODE_SCANNER_UNAVAILABLE`, `INTERNAL`, or `MODULE_INSTALL_FAILED`.
  - `message`: diagnostic message returned by Google Play services.
  - `retryable`: whether retrying the operation can succeed.
  - `canceled`: whether the user canceled the scanner.
  - `exception`: native exception class, when available.

Before opening the scanner, the plugin checks whether the Barcode UI module is available and requests an urgent installation when necessary. Keep a loading indicator visible while `startScan` is pending. Retry only errors whose `retryable` property is `true`.

### Example 1

Scan a code in any format and handle structured errors:

```javascript
var onSuccess = function (jsonBarcode) {
	var rawValue = jsonBarcode.rawValue;
	var formatName = jsonBarcode.formatName;
	var formatValue = jsonBarcode.formatValue;
	var valueType = jsonBarcode.valueType;
	// Do things with the code.
};
var onError = function (error) {
	if (error.canceled) {
		return;
	}
	if (error.retryable) {
		// Offer a bounded retry.
	}
	console.error(error.errorName, error.errorCode, error.message);
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

### Browser quirks

The browser platform method will show a prompt to enter the code string manually, no scanning is performed.

## getBarcodeConstant

Retrieve the Barcode format constant value by its String name. Useful to scan new barcode formats added in the future.

```javascript
cordova.plugins.GoogleCodeScanner.getBarcodeConstant(onSuccess, onError, options);
```

| **options** | |
| --- | --- |
| barcodeFormat | **String**: The [Barcode format constant](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode#constants) name to query. Eg: `FORMAT_AZTEC` |

### Success callback return values

JSON object as follows:

| **jsonConstant** | |
| --- | --- |
| formatName | **String**: the barcode format name. E.g: `FORMAT_EAN_13`. |
| formatValue | **int**:  the barcode format constant value. |

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

### Browser quirks

The browser platform does not implement this method.

# Predefined barcode formats

Because the **startScan** method accepts an integer, all formats from the ML Kit [Barcode](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode "Barcode") class are supported.

The following barcode constants are pre-defined by the plugin for your convenience:

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

- Camera usage permission not required.
- Reads all barcodes blazingly faster than any other barcode plugin thanks to the Google Play Services API.
- Plugin uses Google Code Scanner [16.0.0-beta2](https://maven.google.com/web/index.html?q=play-services-code-scanner#com.google.android.gms:play-services-code-scanner "16.0.0-beta2") by default.
- To use a newer version in the future, the install accepts the **PLAY_SERVICES_GCS_VERSION** parameter.
- Whenever Play Store app is disabled, the UI module cannot be downloaded.
- If you are viewing this README in NPM, there is probably a more up-to-date version in [GitHub](https://github.com/andreszs/cordova-plugin-google-code-scanner "GitHub").

# Plugin demo app

- [Compiled debug APK and reference](https://www.andreszsogon.com/cordova-google-code-scanner-plugin-demo/)
- [Source code for www folder](https://github.com/andreszs/cordova-plugin-demos)

<img src="https://github.com/andreszs/cordova-plugin-demos/blob/main/com.andreszs.gcs.demo/screenshots/android/google-code-scanner-1.jpg?raw=true" width="200" /> <img src="https://github.com/andreszs/cordova-plugin-demos/blob/main/com.andreszs.gcs.demo/screenshots/android/google-code-scanner-2.jpg?raw=true" width="200" /> <img src="https://github.com/andreszs/cordova-plugin-demos/blob/main/com.andreszs.gcs.demo/screenshots/android/google-code-scanner-3.jpg?raw=true" width="200" />

# Contributing

Please report any issue with this plugin in GitHub by providing detailed context and sample code.
PRs to improve and add new features or platforms are always welcome.
