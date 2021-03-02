angular.module("core").component("heading", {
    bindings: {
        title: "@",
        url: "@",
    },
    controller: function() {
        var ctrl = this
        ctrl.version = '${projectVersion}'
    },
    templateUrl: "js/core/components/heading.template.html"
});
