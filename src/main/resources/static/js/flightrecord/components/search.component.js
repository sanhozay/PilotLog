angular.module("flightrecord").factory("example", function() {
    var example = {};
    example.flight = {};
    example.observers = [];
    example.apply = function(form) {
        example.flight.aircraft = form.aircraft;
        example.flight.callsign = form.callsign;
        example.flight.origin = form.origin;
        example.flight.destination = form.destination;
    }
    example.setTerm = function(property, value) {
        example.flight[property] = value;
        example.notify();
    }
    example.addObserver = function(observer) {
        example.observers.push(observer);
    }
    example.notify = function() {
        example.observers.forEach(function (observer) {
            observer.didChangeExample();
        });
    }
    return example;
}).component("search", {
    bindings: {
        exampleChanged: "&",
    },
    controller: function(example) {
        var ctrl = this;
        ctrl.$onInit = function() {
            ctrl.form = {};
            ctrl.bind();
            example.addObserver(ctrl);
        }
        ctrl.bind = function() {
            if (example.flight) {
                ctrl.form.aircraft = example.flight.aircraft;
                ctrl.form.callsign = example.flight.callsign;
                ctrl.form.origin = example.flight.origin;
                ctrl.form.destination = example.flight.destination;
            }
        }
        ctrl.search = function() {
            example.apply(ctrl.form);
            ctrl.exampleChanged();
        }
        ctrl.clear = function() {
            ctrl.form = {};
            ctrl.search();
        }
        ctrl.didChangeExample = function() {
            ctrl.bind();
            ctrl.search();
        }
    },
    templateUrl: "js/flightrecord/components/search.template.html"
});


