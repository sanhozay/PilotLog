angular.module("flightdetail").component("altitude", {
    bindings: {
        flightId: "<"
    },
    controller: function($http, $interval) {
        var ctrl = this;
        var complete = false;
        var autoRefresh;
        ctrl.$onInit = function() {
            ctrl.refresh();
            autoRefresh = $interval(ctrl.refresh, 1000)
        }
        ctrl.$onDestroy = function() {
            $interval.cancel(autoRefresh);
        }
        ctrl.refresh = function() {
            if (complete) {
                return;
            }
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
                        type: "line",
                        data: {
                            labels: minutes,
                            datasets: [{
                                label: "Altitude (ft)",
                                borderColor: "rgb(180, 64, 64)",
                                backgroundColor: "rgb(255, 240, 240)",
                                fill: true,
                                pointRadius: 0,
                                data: altitudes
                            }]
                        },
                        options: {
                            animation: {
                                duration: 0
                            },
                            legend: {
                                display: false
                            },
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
                }
            );
            url = "api/flights/flight/" + ctrl.flightId;
            $http.get(url)
                .then(function(response) {
                    complete = response.data.complete;
                }
            );
        }
    },
    templateUrl: "js/flightdetail/components/altitude.template.html"
});
