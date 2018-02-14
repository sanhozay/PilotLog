angular.module("core").filter("duration", function() {
    return function(seconds) {
        if (seconds == null) {
            return ""
        }
        var hours = Math.floor(seconds / 3600)
        var m = Math.floor((seconds % 3600) / 60)
        var mins = m.toString()
        if (m < 10) {
            mins= '0' + mins
        }
        return hours + ":" + mins
    }
});