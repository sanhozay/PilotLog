angular.module("aircraft").component("summary", {
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
    templateUrl: "js/aircraft/components/summary.template.html"
});


