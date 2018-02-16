angular.module("application", ["core", "flightrecord", "ngRoute"])
angular.module("application").config(function ($routeProvider) {
    $routeProvider
        .when("/", {template: "<flightrecord></flightrecord>"})
        .otherwise("/");
});
