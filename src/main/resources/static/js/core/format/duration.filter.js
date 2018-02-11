angular.module("core")
    .filter("duration", function() {
        return function(minutes) {
            if (minutes == null) {
                return ""
            }
            var hours = Math.floor(minutes / 60)
            var m = Math.floor(minutes % 60)
            var mins = m.toString()
            if (m < 10) {
                mins= '0' + mins
            }
            return hours + ":" + mins
        }
    });