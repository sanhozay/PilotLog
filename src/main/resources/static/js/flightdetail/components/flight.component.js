angular.module("flightdetail").component("flight", {
    bindings: {
        flightId: "<"
    },
    controller: function($http, $interval) {
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
            var url = "/api/flights/flight/" + ctrl.flightId;
            $http.get(url)
                .then(function(response) {
                    ctrl.flight = response.data;
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
    templateUrl: "js/flightdetail/components/flight.template.html"
});

