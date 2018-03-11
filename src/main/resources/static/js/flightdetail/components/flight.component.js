angular.module("flightdetail").component("flight", {
    controller: function($routeParams) {
        var ctrl = this;
        ctrl.$onInit = function() {
            ctrl.id = $routeParams.id
        }
    },
    templateUrl: "js/flightdetail/components/flight.template.html"
});