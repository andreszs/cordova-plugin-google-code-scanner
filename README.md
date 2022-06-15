![npm](https://img.shields.io/npm/dt/cordova-plugin-google-code-scanner) ![npm](https://img.shields.io/npm/v/cordova-plugin-google-code-scanner) ![GitHub package.json version](https://img.shields.io/github/package-json/v/andreszs/cordova-plugin-google-code-scanner?color=FF6D00&label=master&logo=github) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/andreszs/cordova-plugin-google-code-scanner) ![GitHub top language](https://img.shields.io/github/languages/top/andreszs/cordova-plugin-google-code-scanner) ![GitHub](https://img.shields.io/github/license/andreszs/cordova-plugin-google-code-scanner) ![GitHub last commit](https://img.shields.io/github/last-commit/andreszs/cordova-plugin-google-code-scanner)

# cordova-plugin-google-code-scanner

The [Google code scanner API](https://developers.google.com/ml-kit/code-scanner) provides a complete solution for scanning codes without requiring your app to request camera permission, while preserving user privacy. This is accomplished by delegating the task of scanning the code to Google Play services and returning only the scan results to your app.


# Platforms

- Android (minSDK 21)
- Browser (basic support)

# Installation

Install the plugin from NPM:
```bash
cordova plugin add cordova-plugin-google-code-scanner
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
| formatValue | **int**:  the barcode type (i.e. its encoding) constant value. |
| valueType | **int**: the format type of the barcode value. |


### Error callback return values

- The error description.

The first time **startScan** is invoked, this callback will notify you that the barcode UI module is being downloaded in the background, if it has not already been installed for another use case. It's up to you to detect and handle this first-time use error. To handle this, it would be wise to show a [loading spinner](https://github.com/greybax/cordova-plugin-native-spinner "loading spinner"), wait a few seconds, and retry the scan after the module was downloaded.

### Example

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

var options = {};
options.barcodeFormats = $('#selBarcodeFormat').val();

var txtBarcodeFormats = $('#txtBarcodeFormats').val();
if (txtBarcodeFormats != '' && isNaN(txtBarcodeFormats)) {
	$('#status').html('<span class="error">Barcode format must be an integer value.</span>');
} else if (parseInt(txtBarcodeFormats) > 0) {
	options.barcodeFormats = txtBarcodeFormats;
}

cordova.plugins.GoogleCodeScanner.startScan(onSuccess, onError/*, options*/);
```

## getBarcodeConstant

Gets the Barcode constant value from its String name.

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

# Supported barcode types

All formats from the ML Kit [Barcode](https://developers.google.com/android/reference/com/google/mlkit/vision/barcode/common/Barcode "Barcode") class are supported.

    Barcode.FORMAT_CODE_128
    Barcode.FORMAT_CODE_39
    Barcode.FORMAT_CODE_93
    Barcode.FORMAT_CODABAR
    Barcode.FORMAT_DATA_MATRIX
    Barcode.FORMAT_EAN_13
    Barcode.FORMAT_EAN_8
    Barcode.FORMAT_ITF
    Barcode.FORMAT_QR_CODE
    Barcode.FORMAT_UPC_A
    Barcode.FORMAT_UPC_E
    Barcode.FORMAT_PDF417
    Barcode.FORMAT_AZTEC

Formats are not hardcoded internally, therefore any format supported by the API will be automatically available to the plugin.

# Plugin demo app

- Compiled debug APK and reference: coming soon.
- [Source code for www folder](https://github.com/andreszs/cordova-plugin-demos "Source code for www folder")

# Contributing

Please report any issue with this plugin in GitHub by providing detailed context and sample code.
PRs to improve and add new features or platforms are always welcome.

