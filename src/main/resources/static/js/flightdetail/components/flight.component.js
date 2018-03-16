angular.module("flightdetail").component("flight", {
    bindings: {
        flightId: "<"
    },
    controller: function($http) {
        var ctrl = this;
        ctrl.$onInit = function() {
            ctrl.refresh();
        }
        ctrl.refresh = function() {
            var url = "/api/flights/flight/" + ctrl.flightId;
            $http.get(url)
                .then(function(response) {
                    ctrl.flight = response.data;
                });
        }
    },
    templateUrl: "js/flightdetail/components/flight.template.html"
});

