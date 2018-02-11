angular.module("core")
    .filter("capitalize", function() {
        return function capitalize(s) {
            if (s.length == 0) {
                return s
            }
            return s[0].toUpperCase() + s.substring(1)
        }
    });