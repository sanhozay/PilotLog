angular.module("flightrecord").component("flightrecord", {
    controller: function($http, $interval, $filter) {
        var ctrl = this
        ctrl.$onInit = function() {
            ctrl.page = 1
            ctrl.sort = {property: "id", direction: "DESC"}
            ctrl.example = {}
            ctrl.form = {}
            ctrl.refresh()
            $interval(ctrl.refresh, 1000)
        }
        ctrl.clickColumn = function(flight, property) {
            if (property == "startTime") {
                var route = "from " + flight.origin + " to " + flight.destination
                var aircraft = $filter('capitalize')(flight.aircraft)
                if (confirm("Delete " + aircraft + "  flight " + route + "?")) {
                    $http.delete("/api/flights/flight/" + flight.id).then(ctrl.refresh)
                }
            } else if (property == "aircraft") {
                ctrl.form["aircraft"] = $filter('capitalize')(flight["aircraft"])
                ctrl.example["aircraft"] = $filter('capitalize')(flight["aircraft"])
                ctrl.gotoPage(1)
            } else {
                ctrl.form[property] = flight[property]
                ctrl.example[property] = flight[property]
                ctrl.gotoPage(1)
            }
        }
        ctrl.clickHeading = function(property) {
            if (ctrl.sort.property == property) {
                ctrl.sort.direction = ctrl.sort.direction == "ASC" ? "DESC" : "ASC"
            } else {
                ctrl.sort.property = property
                if (ctrl.sort.property == "startTime" || ctrl.sort.property == "endTime") {
                    ctrl.sort.direction = "DESC"
                } else {
                    ctrl.sort.direction = "ASC"
                }
            }
            ctrl.refresh()
        }
        ctrl.gotoPage = function(page) {
            ctrl.page = page
            ctrl.refresh()
        }
        ctrl.refresh = function() {
            var url = "/api/flights/?page=" + (ctrl.page - 1)
            if (ctrl.sort && ctrl.sort.property && ctrl.sort.direction) {
                var sortParam = "sort=" + ctrl.sort.property + "," + ctrl.sort.direction
                url += "&" + sortParam
            }
            $http.post(url, ctrl.example)
                .then(function(response) {
                    ctrl.content = response.data.content
                    ctrl.totals = response.data.totals
                    ctrl.totalFlights = response.data.totalElements
                    ctrl.totalPages = response.data.totalPages
                })
        }
    },
    templateUrl: "js/flightrecord/flightrecord.template.html"
});