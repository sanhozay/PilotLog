angular.module("airportsummary").component("airports", {
    bindings: {
        content: "<",
        totals: "<",
        onClickHeading: "&"
    },
    controller: function() {
        var ctrl = this
        ctrl.clickHeading = function(property) {
            ctrl.onClickHeading({property: property})
        }
    },
    templateUrl: "js/airportsummary/components/airports.template.html"
});


