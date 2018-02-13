angular.module("pager").component("pager", {
    bindings: {
        page: "<",
        of: "<",
        pageSize: "<",
        onPageChanged: "&",
        onPageSizeChanged: "&"
    },
    controller: function($cookies) {
        var ctrl = this
        ctrl.goto = function(page) {
            if (page > 0 && page <= ctrl.of) {
                ctrl.page = page
                ctrl.onPageChanged({page: ctrl.page})
            }
        }
        ctrl.pageSizeChanged = function() {
            ctrl.onPageSizeChanged({pageSize: ctrl.pageSize})
        }
        ctrl.pages = function() {
            var target = 12
            pages = []
            gaps = [0, 0]
            for (var i = 1; i <= ctrl.of; ++i) {
                pages.push(i)
            }
            if (pages.length <= target) {
                return pages
            }
            while (pages.length + gaps[0] + gaps[1] > target) {
                var l = pages[1]
                var r = pages[pages.length - 2]
                if (Math.abs(l - ctrl.page) > Math.abs(r - ctrl.page)) {
                    pages.splice(1, 1)
                    gaps[0] = 1
                } else {
                    pages.splice(pages.length - 2, 1)
                    gaps[1] = 1
                }
            }
            var gapped = []
            var spacer = -1
            for (var i = 0; i < pages.length; ++i) {
                gapped.push(pages[i])
                if (i + 1 < pages.length && pages[i + 1] - pages[i] > 1) {
                    gapped.push(spacer--)
                }
            }
            return gapped
        }
    },
    templateUrl: "js/pager/pager.template.html"
});


