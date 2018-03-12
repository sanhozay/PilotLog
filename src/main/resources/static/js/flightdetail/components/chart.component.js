angular.module("flightdetail").component("chart", {
    bindings: {
        flightId: "<"
    },
    controller: function($http) {
        var ctrl = this;
        var chart;
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
                    var options = {
                        width: "600px", height: "400px"
                    };
                    chart = new Chartist.Line(".ct-chart", data, options);
                });
        }
    },
    templateUrl: "js/flightdetail/components/chart.template.html"
});
