angular.module("core").component("status", {
    bindings: {
        count: "<",
        entity: "@",
        plural: "@"
    },
    templateUrl: "js/core/components/status.template.html"
});
