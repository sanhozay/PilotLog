angular.module("application", ["ngRoute", "flightrecord", "aircraftsummary", "flightdetail"])
angular.module("application").config(function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(false);
    $routeProvider
        .when("/", {template: "<flightrecord></flightrecord>"})
        .when("/aircraft", {template: "<aircraftsummary></aircraftsummary>"})
        .when("/flight/:id", {template: "<flightdetail></flightdetail>"})
        .otherwise("/");
});
