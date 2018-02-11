var app = angular.module('application', ["core"])
app.controller('controller', function($scope, $http, $interval, $filter) {
    $scope.refresh = function() {
        var pageParam = "page=" + ($scope.currentPage - 1)
        var sortParam = "sort=" + $scope.sortKey + "," + $scope.sortDirection
        $http.post("/api/flights/?" + pageParam + "&" + sortParam, $scope.example)
        .then(function(response) {
            $scope.flights = response.data.content

            $scope.currentPage = response.data.number + 1
            $scope.totalPages = response.data.totalPages
            $scope.totalFlights = response.data.totalElements

            $scope.pages = []
            for (var i = 1; i <= $scope.totalPages; ++i) {
                $scope.pages.push(i)
            }

            $scope.pageDuration = response.data.pageDuration
            $scope.otherDuration = response.data.otherDuration
            $scope.totalDuration = response.data.totalDuration
        })
    }
    $scope.delete = function(flight) {
        var route = "from " + flight.origin + " to " + flight.destination
        var description = $filter('capitalize')(flight.aircraft) + " flight " + route
        if (confirm("Delete " + description + "?")) {
            $http.delete("/api/flights/flight/" + flight.id)
            .then($scope.refresh)
        }
    }
    $scope.addTerm = function(term, value) {
        $scope.form[term] = value
        $scope.search()
    }
    $scope.sortBy = function(key) {
        if ($scope.sortKey == key) {
            $scope.sortDirection = $scope.sortDirection == "ASC" ? "DESC" : "ASC"
        } else {
            $scope.sortKey = key
            if (key == "startTime" || key == "endTime") {
                $scope.sortDirection = "DESC"
            } else {
                $scope.sortDirection = "ASC"
            }
        }
    }
    $scope.search = function() {
        $scope.example.aircraft = $scope.form.aircraft
        $scope.example.callsign = $scope.form.callsign
        $scope.example.destination = $scope.form.destination
        $scope.example.origin = $scope.form.origin
        $scope.gotoPage(1)
    }
    $scope.clear = function() {
        $scope.form = {}
        $scope.example = {}
        $scope.gotoPage(1)
    }
    $scope.gotoPage = function(page) {
        $scope.currentPage = page
        $scope.refresh()
    }
    $scope.sortKey = "startTime"
    $scope.sortDirection = "DESC"
    $scope.clear()
    $interval($scope.refresh, 1000)
})
