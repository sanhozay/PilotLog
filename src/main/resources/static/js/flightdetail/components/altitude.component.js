angular.module("flightdetail").component("altitude", {
    bindings: {
        flightId: "<"
    },
    controller: function($http, $interval, $filter) {
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
                    var track = response.data;
                    var altitude = [];
                    var time = [];
                    var start;
                    track.forEach(function (point) {
                        altitude.push(point.altitude);
                        var ts = point.timestamp;
                        var date = Date.parse(ts.substring(0, ts.indexOf("+")));
                        if (!start) {
                            start = date;
                        }
                        time.push((date - start) / 1000);
                    });
                    var ctx = document.getElementById("altitude").getContext("2d");
                    var chart = new Chart(ctx, {
                        type: "line",
                        data: {
                            labels: time,
                            datasets: [{
                                label: "Altitude (ft)",
                                borderColor: "rgb(180, 64, 64)",
                                backgroundColor: "rgb(255, 240, 240)",
                                fill: true,
                                pointRadius: 0,
                                data: altitude
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
                                        display: true
                                    },
                                    scaleLabel: {
                                        display: true,
                                        labelString: "Duration (hh:mm)"
                                    },
                                    ticks: {
                                        autoSkipPadding: 50,
                                        callback: function(value, index, values) {
                                            return $filter('duration')(value)
                                        },
                                        maxRotation: 0,
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
