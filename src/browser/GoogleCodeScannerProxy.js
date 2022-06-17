var GoogleCodeScanner = function () {};

GoogleCodeScanner.prototype.startScan = function (successCallback, errorCallback, args) {
	var rawValue = window.prompt("Enter barcode value (empty value will fire the error handler):");
	if (rawValue) {
		var result = {
			rawValue: rawValue,
			formatValue: args[0].barcodeFormats,
			formatName: null,
			valueType: null /* pending implementation, PR welcome */
		};
		successCallback(result);
	} else {
		errorCallback("Canceled");
	}
};

GoogleCodeScanner.prototype.getBarcodeConstant = function (successCallback, errorCallback, args) {
	errorCallback("Not implemented for Browser platform yet.");
};

module.exports = new GoogleCodeScanner();

require('cordova/exec/proxy').add('GoogleCodeScanner', module.exports);