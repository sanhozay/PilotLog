/*
 * PilotLog
 *
 * Copyright © 2018 Richard Senior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

var app = angular.module('application', [])

app.controller('controller', function($scope, $http, $interval) {
    $scope.refresh = function() {
        $http.get("/api/flights/?page=" + ($scope.currentPage - 1))
        .then(function(response) {
            $scope.flights = response.data.content
            $scope.totalPages = response.data.totalPages
            $scope.totalFlights = response.data.totalElements
            $scope.currentPage = response.data.number + 1
            $scope.totalMins = 0
            $scope.pages = []
            for (var i = 1; i <= $scope.totalPages; ++i) {
                $scope.pages.push(i)
            }
        })
    }
    $scope.next = function() {
        if ($scope.currentPage < $scope.totalPages) {
            $scope.currentPage += 1
            $scope.refresh()
        }
    }
    $scope.prev = function() {
        if ($scope.currentPage > 1) {
            $scope.currentPage -= 1
            $scope.refresh()
        }
    }
    $scope.gotoPage = function(page) {
        $scope.currentPage = page
        $scope.refresh()
    }
    $scope.refresh()
    $interval($scope.refresh, 1000)
})

app.filter('duration', function() {
    return function(minutes) {
        var hours = Math.floor(minutes / 60)
        var m = Math.floor(minutes % 60)
        var mins = m.toString()
        if (m < 10) {
            mins= '0' + mins
        }
        return hours + ":" + mins
    }
})

app.filter('capitalize', function() {
    return function(s) {
        if (s.length == 0) {
            return s
        }
        return s[0].toUpperCase() + s.substring(1)
    }
})