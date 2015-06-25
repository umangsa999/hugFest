'use strict';
var hugFestApp = angular.module('hugFestApp', [
	'ngRoute',
	'hugFestControllers'
]);

hugFestApp.config(['$routeProvider',
	function($routeProvider){
		//check cookies for user data. if no user data, use this call. if is, do another $routeProvider.when branch
		$routeProvider.
			when('/settings', {
				templateUrl: 'partials/tabScreens/settings.html',
				controller: 'hugFestSettingsCtrl'
			}).
			when('/friends', {
				templateUrl: 'partials/tabScreens/friends.html',
				controller: 'hugFestFriendCtrl'
			}).
			when('/home', {
				templateUrl: 'partials/tabScreens/home.html',
				controller: 'hugFestHomeCtrl'
			}).
			when('/start', {
				templateUrl: 'partials/start_login/start.html',
				controller: 'hugFestStartCtrl'
			}).
			when('/target', {
				templateUrl: 'partials/gameScreens/target.html',
				controller: 'hugFestTargetCtrl'
			}).
			when('/create', {
				templateUrl: 'partials/gameScreens/create.html',
				controller: 'hugFestCreateCtrl'
			}).
			when('/rules', {
				templateUrl: 'partials/gameScreens/rules.html',
				controller: 'hugFestCreateCtrl'
			}).
			when('/profile', {
				templateUrl: 'partials/start_login/profile.html',
				controller: 'hugFestProfileCtrl'
			}).
			otherwise({
				redirectTo: '/home'
			});
}]);