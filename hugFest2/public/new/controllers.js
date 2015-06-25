'use strict';
var hugFestControllers = angular.module('hugFestControllers', []);

var imageURL = "res/title.png";
hugFestControllers.controller('hugFestStartCtrl', ['$scope', function($scope){
	$scope.logo = imageURL;
	$scope.buttons = [{name: "Hug On!", url: '#/home'}, {name: "Sign In", url: '#/signUp'}];
}]);

hugFestControllers.controller('hugFestLoginCtrl', ['$scope', function($scope){
	$scope.logo = imageURL;
	$scope.inputs = [{text:"Username:", id: "user"}, {text:"Password:", id: "pass"}];
	$scope.buttonLabel = "Sign up";
}]);

hugFestControllers.controller('hugFestHomeCtrl', ['$scope', function($scope){
	//do a call here for games here
	$scope.columns = ["Name", "Number of Friends", "Status"];
	$scope.friendGames = [
		{owner: "Mike", mutual: "2", status: "Playing"},
		{owner: "Andy", mutual: "14", status: "Playing"},
		{owner: "Manjot", mutual: "5", status: "Started"},
		{owner: "chris", mutual: "0", status: "Lobby"}
	];
	$scope.gameSortFunction = function(game){
		return parseInt(game.mutual);
	}
}]);

hugFestControllers.controller('hugFestFriendCtrl', ['$scope', function($scope){
	$scope.titles = ["Name", "Status", "Hugs", "Games"];
	//do a call here for friends
	$scope.friends = [
		{name: "Mike", status: "Online", hugs: "52", games: "3"},
		{name: "Manjot", status: "Offline", hugs: "2", games: "1"},
		{name: "Chris", status: "In Game", hugs: "0", games: "5"},
		{name: "Ryan", status: "In Game", hugs: "5", games: "3"}
	];
}]);

hugFestControllers.controller('hugFestSettingsCtrl', ['$scope', function($scope){
	$scope.settings = [
	{name: "Notifications", description: "Allow notifications", isOn: false},
	{name: "Join Automatically", description: "Skip confirmation when invited to a game", isOn: true},
	{name: "Share Background Location", description: "Share your location while app is running in the background", isOn: true}
	];
	
	$scope.buttons = [
	{text: "Log Out"},
	{text: "Reset Stats"} //make sure to use confirm()
	];
}]);













