angular.module("core").component("heading", {
    bindings: {
        title: "@",
        url: "@"
    },
    templateUrl: "js/core/components/heading.template.html"
});