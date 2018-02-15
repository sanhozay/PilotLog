angular.module("heading").component("heading", {
    bindings: {
        title: "@",
        url: "@"
    },
    templateUrl: "js/heading/heading.template.html"
});