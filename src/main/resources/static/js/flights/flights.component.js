angular.module("flights").component("flights", {
    bindings: {
        content: "<",
        durationTotals: "<",
        totalFlights: "<",
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
    templateUrl: "js/flights/flights.template.html"
});


