angular.module("flightdetail").component("map", {
    bindings: {
        flightId: "<"
    },
    controller: function($http, $interval) {
        var ctrl = this

        var autoRefresh;
        var complete = false;
        var line, map, marker;
        var mode = "FIT";

        var plane = L.icon({
            iconUrl: '../../../../../images/plane.png',
            iconSize: [40, 40],
            iconAnchor: [20, 20]
        });

        ctrl.$onInit = function() {
            map = L.map('map').fitWorld();
            var url = 'https://server.arcgisonline.com/' +
                'ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}';
            var	attribution = 'Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, ' +
                'Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012'
            L.tileLayer(url, {attribution: attribution}).addTo(map);
            L.control.scale().addTo(map);

            var command = L.control({position: 'topright'});
            command.onAdd = function (map) {
                var div = L.DomUtil.create('div', 'command');
                div.innerHTML = '<form>' +
                    '<input name="mode" type="radio" value="FIT" checked="true"/>&nbsp;Overview&nbsp;' +
                    '<input name="mode" type="radio" value="FOLLOW"/>&nbsp;Follow&nbsp;' +
                    '<input name="mode" type="radio" value="MANUAL"/>&nbsp;Manual' +
                    '</form>';
                return div;
            }
            command.addTo(map);
            document.querySelectorAll('input[name="mode"]').forEach(function(b) {
                b.addEventListener ("click", ctrl.modeChanged, false)
            });

            ctrl.refresh();
            autoRefresh = $interval(ctrl.refresh, 1000)
        }

        ctrl.$onDestroy = function() {
            $interval.cancel(autoRefresh);
        }

        ctrl.modeChanged = function() {
            mode = document.querySelector('input[name="mode"]:checked').value;
            complete = false;
            ctrl.refresh();
        }

        ctrl.refresh = function() {
            if (complete) {
                return;
            }
            var url = "/api/flights/flight/" + ctrl.flightId + "/featurecollection";
            $http.get(url)
                .then(function(response) {
                    if (line) {
                        line.clearLayers();
                    }
                    line = L.geoJSON(response.data, {
                        style: function(feature) {
                            return {color: "RoyalBlue"};
                        }
                    }).bindPopup(function(layer) {
                        var movement = layer.feature.properties;
                        if (movement.icao) {
                            return "<b>" + movement.name + "&nbsp;(" + movement.icao + ")</b>" +
                                "<br/> " + (movement.type == "O" ? "Departed" : "Arrived") + " " + movement.date;
                        }
                    });
                    line.addTo(map);

                    var track = response.data.features[1];
                    var points = track.geometry.coordinates;
                    var p = points[points.length - 1];
                    var coordinate = new L.LatLng(p[1], p[0]);

                    if (complete) {
                        if (marker) {
                            map.removeLayer(marker);
                        }
                    } else if (heading) {
                        if (marker) {
                            marker.setLatLng(coordinate);
                            marker.setRotationAngle(heading);
                        } else {
                            marker = L.marker(coordinate, {icon: plane, rotationAngle: heading});
                            marker.addTo(map);
                        }
                    }

                    if (mode == "FOLLOW") {
                        map.panTo(coordinate);
                    } else if (mode == "FIT") {
                        var bl = [180, 180];
                        var tr = [-180, -180];
                        for (var i = 0; i < points.length; ++i) {
                            var lon = points[i][0];
                            var lat = points[i][1];
                            if (lat < bl[0]) bl[0] = lat;
                            if (lat > tr[0]) tr[0] = lat;
                            if (lon < bl[1]) bl[1] = lon;
                            if (lon > tr[1]) tr[1] = lon;
                        }
                        map.fitBounds([bl, tr], {padding: [20, 20]});
                    }
                }
            );

            url = "api/flights/flight/" + ctrl.flightId;
            $http.get(url)
                .then(function(response) {
                    complete = response.data.complete;
                    heading = response.data.heading;
                }
            );
        }
    },
    templateUrl: "js/flightdetail/components/map.template.html"
});
