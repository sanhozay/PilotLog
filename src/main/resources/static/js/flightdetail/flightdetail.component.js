angular.module("flightdetail").component("flightdetail", {
    controller: function($routeParams) {
        var ctrl = this;
        ctrl.$onInit = function() {
            ctrl.id = $routeParams.id
        }
    },
    templateUrl: "js/flightdetail/flightdetail.template.html"
});
