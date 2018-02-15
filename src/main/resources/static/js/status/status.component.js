angular.module("status").component("status", {
    bindings: {
        count: "<",
        entity: "@",
        plural: "@"
    },
    templateUrl: "js/status/status.template.html"
});
