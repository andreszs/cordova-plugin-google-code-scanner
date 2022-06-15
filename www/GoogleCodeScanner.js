"use strict";
var exec = require('cordova/exec');

var GoogleCodeScanner = {
	startScan: function (successCallback, errorCallback, args) {
		exec(successCallback, errorCallback, "GoogleCodeScanner", "startScan", [args]);
	},
	getBarcodeConstant: function (successCallback, errorCallback, args) {
		exec(successCallback, errorCallback, "GoogleCodeScanner", "getBarcodeConstant", [args]);
	}
};

module.exports = GoogleCodeScanner;
