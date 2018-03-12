angular.module("flightdetail").component("detail", {
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
    templateUrl: "js/flightdetail/components/detail.template.html"
});

