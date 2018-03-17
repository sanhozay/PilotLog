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
                    var minutes = [];
                    for (var i = 0; i < points.length; i += 6) {
                        var altitude = points[i][2];
                        altitudes.push(altitude);
                        minutes.push(i / 6);
                    }
                    var data = {
                        labels: minutes,
                        series: [altitudes]
                    };
                    var options = {
                        axisX: {
                            type: Chartist.AutoScaleAxis,
                            onlyInteger: true,
                            scaleMinSpace: 40,
                            showGrid: false,
                        }
                    };
                    var chart = new Chartist.Line(".ct-chart", data, options);
                });
        }
    },
    templateUrl: "js/flightdetail/components/altitude.template.html"
});
