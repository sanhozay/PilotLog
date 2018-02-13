angular.module("pageTitle").component("pageTitle", {
    bindings: {
        title: "@",
        url: "@"
    },
    templateUrl: "js/pageTitle/pageTitle.template.html"
});