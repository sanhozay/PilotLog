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
        $http.get("/api/flights/")
        .then(function(response) {
            $scope.flights = response.data
            var mins = 0
            for (i = 0; i < response.data.length; ++i) {
                if (i in response.data) {
                    var flight = response.data[i];
                    mins += flight.duration
                }
            }
            $scope.totalMins = mins
        })
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