angular.module("flightrecord").component("status", {
    bindings: {
        count: "<",
        entity: "@",
        plural: "@"
    },
    templateUrl: "js/flightrecord/components/status.template.html"
});
