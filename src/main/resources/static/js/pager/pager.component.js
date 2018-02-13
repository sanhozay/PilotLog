angular.module("pager").component("pager", {
    bindings: {
        page: "<",
        of: "<",
        onPageChanged: "&"
    },
    controller: function() {
        var ctrl = this
        ctrl.goto = function(page) {
            if (page > 0 && page <= ctrl.of) {
                ctrl.page = page
                ctrl.onPageChanged({page: ctrl.page})
            }
        }
        ctrl.pages = function() {
            pages = []
            for (var i = 1; i <= ctrl.of; ++i) {
                pages.push(i)
            }
            return pages
        }
    },
    templateUrl: "js/pager/pager.template.html"
});


