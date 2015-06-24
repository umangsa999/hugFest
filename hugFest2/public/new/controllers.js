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
	//do a call here for friends
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