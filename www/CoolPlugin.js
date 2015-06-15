/**
 * Created by marius on 15.06.2015.
 */

var exec = require('cordova/exec');

function CoolPlugin() {
    var _orientation = 'landscape';
    var _obj = null;
    var _context = null;
    var _camImage = null;

    var _x = 0;
    var _y = 0;
    var _width = 0;
    var _height = 0;

    var DestinationType = {
        DATA_URL : 0,
        FILE_URI : 1
    };

    var PictureSourceType = {
        PHOTOLIBRARY : 0,
        CAMERA : 1,
        SAVEDPHOTOALBUM : 2
    };

    var EncodingType = {
        JPEG : 0,
        PNG : 1
    };

    console.log("CoolPlugin.js: is created");
}

CoolPlugin.prototype.showToast = function (aString) {
    console.log("CoolPlugin.js: showToast");
    exec(function (result) { /*alert("OK" + reply);*/
    }, function (result) { /*alert("Error" + reply);*/
    }, "CoolPlugin", aString, []);
};

CoolPlugin.prototype.start = function(options) {
    cordova.exec(false, false, "CanvasCamera", "startCapture", [options]);
};

var coolPlugin = new CoolPlugin();
module.exports = coolPlugin;