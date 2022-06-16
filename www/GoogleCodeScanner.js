"use strict";
var exec = require('cordova/exec');

var GoogleCodeScanner = {
	startScan: function (successCallback, errorCallback, args) {
		exec(successCallback, errorCallback, "GoogleCodeScanner", "startScan", [args]);
	},
	getBarcodeConstant: function (successCallback, errorCallback, args) {
		exec(successCallback, errorCallback, "GoogleCodeScanner", "getBarcodeConstant", [args]);
	},
	BarcodeFormat: {
		FORMAT_UNKNOWN: -1,
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
};

module.exports = GoogleCodeScanner;
