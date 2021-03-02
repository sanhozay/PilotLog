angular.module("airportsummary").component("airportsummary", {
    controller: function($http, $cookies) {
        var ctrl = this
        var cookie = "pilotlog.page.size"
        var nextYear = new Date(new Date().setFullYear(new Date().getFullYear() + 1))
        ctrl.$onInit = function() {
            if (!$cookies.get(cookie)) {
                $cookies.put(cookie, 10, {expires: nextYear})
            }
            ctrl.pageable = {pageNumber: 1,
                size: $cookies.get(cookie) || 10,
                sort: {property: "movements", direction: "DESC"}
            }
            ctrl.refreshPage(1)
        }
        ctrl.adjustPageSize = function(pageSize) {
            ctrl.pageable.size = pageSize
            $cookies.put(cookie, pageSize, {expires: nextYear})
            ctrl.refreshPage(1)
        }
        ctrl.headingClicked = function(property) {
            if (ctrl.pageable.sort.property == property) {
                var direction = ctrl.pageable.sort.direction
                ctrl.pageable.sort.direction = direction == "ASC" ? "DESC" : "ASC"
            } else {
                if (property == "icao" || property == "name") {
                    ctrl.pageable.sort.direction = "ASC"
                } else {
                    ctrl.pageable.sort.direction = "DESC"
                }
                ctrl.pageable.sort.property = property
            }
            ctrl.refresh()
        }
        ctrl.refreshPage = function(pageNumber) {
            ctrl.pageable.pageNumber = pageNumber
            ctrl.refresh()
        }
        ctrl.refresh = function() {
            var url = "/api/airports/?page=" + (ctrl.pageable.pageNumber - 1)
            url += "&size=" + ctrl.pageable.size
            if (ctrl.pageable.sort) {
                var sort = ctrl.pageable.sort
                if (sort.property && sort.direction) {
                    var sortParam = "sort=" + sort.property + "," + sort.direction
                    url += "&" + sortParam
                }
            }
            $http.get(url)
                .then(function(response) {
                    ctrl.data = response.data
                    if (ctrl.pageable.pageNumber > ctrl.totalPages) {
                        ctrl.refreshPage(ctrl.totalPages)
                    }
                })
        }
    },
    templateUrl: "js/airportsummary/airportsummary.template.html"
});
