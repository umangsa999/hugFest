'use strict';
var hugFestApp = angular.module('hugFestApp', [
	'ngRoute',
	'hugFestControllers'
]);

hugFestApp.config(['$routeProvider',
	function($routeProvider){
		//check cookies for user data. if no user data, use this call. if is, do another $routeProvider.when branch
		$routeProvider.
			when('/start',{
				templateUrl: 'partials/start_login/start.html',
				controller: 'hugFestStartCtrl'
			}).
			when('/signUp', {
				templateUrl: 'partials/start_login/login.html',
				controller: 'hugFestLoginCtrl'
			}).
			otherwise({
				redirectTo: '/start'
			});
}]);