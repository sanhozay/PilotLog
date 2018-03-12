angular.module("flightdetail").component("map", {
    bindings: {
        flightId: "<"
    },
    controller: function($http) {
        var ctrl = this
        var map;
        ctrl.$onInit = function() {
            map = L.map('map').fitWorld();
            L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(map);
            L.control.scale().addTo(map);
            ctrl.refresh();
        }
        ctrl.refresh = function() {
            var url = "/api/flights/flight/" + ctrl.flightId + "/track";
            $http.get(url)
                .then(function(response) {
                    L.geoJSON(response.data, {
                        style: function(feature) {
                            return {color: "#0080a0"};
                        }
                    }).bindPopup(function(layer) {
                        if (layer.feature.properties.icao) {
                            return layer.feature.properties.icao +
                                "<br/> " + layer.feature.properties.date;
                        }
                    }).addTo(map);
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
                });
        }
    },
    templateUrl: "js/flightdetail/components/map.template.html"
});
