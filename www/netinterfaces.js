var exec = require("cordova/exec")

module.exports = {
    getInfo: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, "NetInterfaces", "getInfo", []);
    },
}
