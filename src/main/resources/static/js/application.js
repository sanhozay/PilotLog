angular.module("application", ["ngRoute", "flightrecord", "aircraftsummary", "airportsummary", "flightdetail"]);
angular.module("application").config(function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(false);
    $routeProvider
        .when("/", {template: "<flightrecord></flightrecord>"})
        .when("/aircraft", {template: "<aircraftsummary></aircraftsummary>"})
        .when("/airports", {template: "<airportsummary></airportsummary>"})
        .when("/flight/:id", {template: "<flightdetail></flightdetail>"})
        .otherwise("/");
});
