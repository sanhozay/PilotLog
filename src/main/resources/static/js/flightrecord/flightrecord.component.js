angular.module("flightrecord").component("flightrecord", {
    controller: function($http, $interval, $filter) {
        var ctrl = this
        ctrl.$onInit = function() {
            ctrl.pageable = {pageNumber: 1, sort: {property: "id", direction: "DESC"}}
            ctrl.search = {form: {}, example: {}}
            ctrl.refresh()
            $interval(ctrl.refresh, 1000)
        }
        ctrl.columnClicked = function(flight, property) {
            if (property == "startTime") {
                var route = "from " + flight.origin + " to " + flight.destination
                var aircraft = $filter('capitalize')(flight.aircraft)
                if (confirm("Delete " + aircraft + "  flight " + route + "?")) {
                    $http.delete("/api/flights/flight/" + flight.id)
                        .then(ctrl.refresh)
                }
            } else if (property == "aircraft") {
                ctrl.search.form["aircraft"] = $filter('capitalize')(flight["aircraft"])
                ctrl.search.example["aircraft"] = $filter('capitalize')(flight["aircraft"])
                ctrl.refreshPage(1)
            } else {
                ctrl.search.form[property] = flight[property]
                ctrl.search.example[property] = flight[property]
                ctrl.refreshPage(1)
            }
        }
        ctrl.headingClicked = function(property) {
            if (ctrl.pageable.sort.property == property) {
                var direction = ctrl.pageable.sort.direction
                ctrl.pageable.sort.direction = direction == "ASC" ? "DESC" : "ASC"
            } else {
                if (property == "startTime" || property == "endTime") {
                    ctrl.pageable.sort.direction = "DESC"
                } else {
                    ctrl.pageable.sort.direction = "ASC"
                }
                ctrl.pageable.sort.property = property
            }
            ctrl.refresh()
        }
        ctrl.refreshPage = function(pageNumber) {
            ctrl.pageable.pageNumber = pageNumber
            ctrl.refresh()
        }
        ctrl.refresh = function() {
            var url = "/api/flights/?page=" + (ctrl.pageable.pageNumber - 1)
            if (ctrl.pageable.sort) {
                var sort = ctrl.pageable.sort
                if (sort.property && sort.direction) {
                    var sortParam = "sort=" + sort.property + "," + sort.direction
                    url += "&" + sortParam
                }
            }
            $http.post(url, ctrl.search.example)
                .then(function(response) {
                    ctrl.content = response.data.content
                    ctrl.durationTotals = response.data.durationTotals
                    ctrl.totalFlights = response.data.totalElements
                    ctrl.totalPages = response.data.totalPages
                })
        }
    },
    templateUrl: "js/flightrecord/flightrecord.template.html"
});