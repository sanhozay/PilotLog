angular.module("flightdetail").component("altitude", {
    bindings: {
        flightId: "<"
    },
    controller: function($http) {
        var ctrl = this;
        ctrl.$onInit = function() {
            ctrl.refresh();
        }
        ctrl.refresh = function() {
            var url = "/api/flights/flight/" + ctrl.flightId + "/track";
            $http.get(url)
                .then(function(response) {
                    var track = response.data.features[1];
                    var points = track.geometry.coordinates;
                    var altitudes = [];
                    for (var i = 0; i < points.length; ++i) {
                        var altitude = points[i][2];
                        altitudes.push(altitude);
                    }
                    var data = {
                        series: [altitudes]
                    };
                    var chart = new Chartist.Line(".ct-chart", data, {});
                });
        }
    },
    templateUrl: "js/flightdetail/components/altitude.template.html"
});
