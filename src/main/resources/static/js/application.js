angular.module("application", ["ngRoute", "aircraft", "flightdetail", "flightrecord"])
angular.module("application").config(function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(false);
    $routeProvider
        .when("/", {template: "<flightrecord></flightrecord>"})
        .when("/aircraft", {template: "<aircraft></aircraft>"})
        .when("/flight/:id", {template: "<flight></flight>"})
        .otherwise("/");
});
