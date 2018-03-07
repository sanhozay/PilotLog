angular.module("flightrecord").component("search", {
    bindings: {
        exampleChanged: "&"
    },
    require: {
        parent: "^flightrecord"
    },
    controller: function() {
        var ctrl = this
        ctrl.clear = function() {
            ctrl.parent.search.form = {}
            ctrl.parent.search.example = {}
            ctrl.exampleChanged()
        }
        ctrl.search = function() {
            ctrl.parent.search.example.aircraft = ctrl.parent.search.form.aircraft
            ctrl.parent.search.example.callsign = ctrl.parent.search.form.callsign
            ctrl.parent.search.example.destination = ctrl.parent.search.form.destination
            ctrl.parent.search.example.origin = ctrl.parent.search.form.origin
            ctrl.exampleChanged()
        }
    },
    templateUrl: "js/flightrecord/components/search.template.html"
});


