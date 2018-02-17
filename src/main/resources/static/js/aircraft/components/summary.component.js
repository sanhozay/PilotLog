angular.module("aircraft").component("summary", {
    bindings: {
        content: "<",
        totals: "<",
        onClickColumn: "&",
        onClickHeading: "&"
    },
    controller: function() {
        var ctrl = this
        ctrl.clickColumn = function(flight, property) {
            ctrl.onClickColumn({flight: flight, property: property})
        }
        ctrl.clickHeading = function(property) {
            ctrl.onClickHeading({property: property})
        }
    },
    templateUrl: "js/aircraft/components/summary.template.html"
});


