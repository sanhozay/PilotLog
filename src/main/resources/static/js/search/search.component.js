angular.module("search").component("search", {
    bindings: {
        exampleChanged: "&"
    },
    require: {
        parent: "^flightrecord"
    },
    controller: function() {
        var ctrl = this
        ctrl.clear = function() {
            ctrl.parent.form = {}
            ctrl.parent.example = {}
            ctrl.exampleChanged()
        }
        ctrl.search = function() {
            ctrl.parent.example.aircraft = ctrl.parent.form.aircraft
            ctrl.parent.example.callsign = ctrl.parent.form.callsign
            ctrl.parent.example.destination = ctrl.parent.form.destination
            ctrl.parent.example.origin = ctrl.parent.form.origin
            ctrl.exampleChanged()
        }
    },
    templateUrl: "js/search/search.template.html"
});


