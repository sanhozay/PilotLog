angular.module("core").component("heading", {
    bindings: {
        title: "@",
        url: "@",
        version: "@"
    },
    templateUrl: "js/core/components/heading.template.html"
});