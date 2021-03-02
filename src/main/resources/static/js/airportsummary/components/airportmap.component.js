angular.module("airportsummary").component("airportmap", {
    controller: function($http, $interval, $filter) {
        var ctrl = this
        var map, markers;
        ctrl.$onInit = function() {
            map = L.map('airportmap').fitWorld();
            var url = 'https://server.arcgisonline.com/' +
                'ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}';
            var	attribution = 'Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, USGS, Intermap, iPC, NRCAN, ' +
                'Esri Japan, METI, Esri China (Hong Kong), Esri (Thailand), TomTom, 2012'
            L.tileLayer(url, {attribution: attribution}).addTo(map);
            L.control.scale().addTo(map);
            markers = L.layerGroup();
            map.addLayer(markers);
            ctrl.refresh();
        }
        ctrl.refresh = function() {
            var url = "/api/airports.json";
            $http.get(url)
                .then(function(response) {
                    var bl = [180, 180];
                    var tr = [-180, -180];
                    markers.clearLayers();
                    response.data.forEach(function(airport) {
                        var lat = airport.coordinate.latitude;
                        var lon = airport.coordinate.longitude;
                        if (lat < bl[0]) bl[0] = lat;
                        if (lat > tr[0]) tr[0] = lat;
                        if (lon < bl[1]) bl[1] = lon;
                        if (lon > tr[1]) tr[1] = lon;
                        var marker = L.marker([lat, lon], {title: airport.icao});
                        marker.bindPopup(
                            "<b>" + airport.name + "&nbsp;(" + airport.icao + ")</b>" +
                            "<br/>Visited " + airport.movements + " times" +
                            "<br/>Last visited " + $filter("date")(airport.last, "dd/MM/yy", "GMT")
                        ).openPopup();
                        markers.addLayer(marker);
                    });
                    map.fitBounds([bl, tr], {padding: [20, 20]});
                    once = false;
                }
            );
        }
    },
    templateUrl: "js/airportsummary/components/airportmap.template.html"
});
