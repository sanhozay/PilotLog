angular.module("application", ["ngRoute", "aircraft", "flightrecord"])
angular.module("application").config(function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
    $routeProvider
        .when("/", {template: "<flightrecord></flightrecord>"})
        .when("/aircraft", {template: "<aircraft></aircraft>"})
        .otherwise("/");
});
