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
                    var ctx = document.getElementById("altitude").getContext("2d");
                    var chart = new Chart(ctx, {
                        // The type of chart we want to create
                        type: "line",

                        // The data for our dataset
                        data: {
                            labels: minutes,
                            datasets: [{
                                label: "Altitude (ft)",
                                borderColor: "rgb(0, 0, 128)",
                                fill: false,
                                pointRadius: 0,
                                data: altitudes
                            }]
                        },
                        options: {
                            scales: {
                                xAxes: [{
                                    gridLines: {
                                        display: false
                                    },
                                    ticks: {
                                        callback: function(value, index, values) {
                                            if (index % 5 == 0) {
                                                return value;
                                            }
                                            return null;
                                        }
                                    }
                                }]
                            }
                        }
                    });
                });
        }
    },
    templateUrl: "js/flightdetail/components/altitude.template.html"
});
