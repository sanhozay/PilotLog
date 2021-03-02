angular.module("aircraftsummary").component("aircraft", {
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
    templateUrl: "js/aircraftsummary/components/aircraft.template.html"
});


