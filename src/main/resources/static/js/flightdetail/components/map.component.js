angular.module("flightdetail").component("map", {
    bindings: {
        flightId: "<"
    },
    controller: function($http, $interval) {
        var ctrl = this
        var complete = false;
        var map;
        var line;
        var autoRefresh;
        ctrl.$onInit = function() {
            map = L.map('map').fitWorld();
            L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="http://openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(map);
            L.control.scale().addTo(map);
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
                    if (line) {
                        line.clearLayers();
                    }
                    line = L.geoJSON(response.data, {
                        style: function(feature) {
                            return {color: "DarkBlue"};
                        }
                    }).bindPopup(function(layer) {
                        if (layer.feature.properties.icao) {
                            return layer.feature.properties.icao +
                                "<br/> " + layer.feature.properties.date;
                        }
                    });
                    line.addTo(map);
                    var track = response.data.features[1];
                    var points = track.geometry.coordinates;
                    var bl = [180, 180];
                    var tr = [-180, -180];
                    for (var i = 0; i < points.length; ++i) {
                        var lon = points[i][0];
                        var lat = points[i][1];
                        if (lat < bl[0]) bl[0] = lat
                        if (lat > tr[0]) tr[0] = lat
                        if (lon < bl[1]) bl[1] = lon
                        if (lon > tr[1]) tr[1] = lon
                    }
                    map.fitBounds([bl, tr]);
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
    templateUrl: "js/flightdetail/components/map.template.html"
});
