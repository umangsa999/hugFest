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

hugFestControllers.controller('hugFestTargetCtrl', ['$scope', function($scope){
	$scope.title = "Top Huggers"
	#scope.time = "8:00:00" //placeholder
	//Container for player information
	$scope.huggers = [
		{name: "Mike",hugs: "4"},
		{name: "Manjot",hugs: "2"},
		{name: "Chris",hugs: "0"},
		{name: "Ryan", hugs: "5"}
	];
	//Should be pulled from server side or something and the server determines target
	//Place holder for now
	$scope.target = [
		{name: "Andy", reason: "Being too awesome", img:"Some image here"},
		{name: "Ryan", reason: "Being too awesome", img:"Some image here"},
		{name: "Chris", reason: "Being a loser", img:"Some image here"},
	];
}]);

hugFestControllers.controller('hugFestCreateCtrl', ['$scope', function($scope){
	//Read below
	$scope.presetRules = [
		{text: "Time", qty:"2 Hours"}
		{text: "Hugs to win",qty:"10"}
	];
	$scope.additionalRules = [
		{text: "No Indoors", remove:false}
		{text: "No Running", remove:false}
	];
	$scope.buttons = [
		{text: "Save"},
		{text: "Load"}
	];
	$scope.friends = [
		{name: "Mike"},
		{name: "Manjot"},
		{name: "Chris"},
		{name: "Ryan"}
	];
	/* Due to all controllers being in same file, no idea how to create these functions
	$scope.addRules = function(){
		
	};
	$scope.removeRules = function(){
		
	};
	*/
}]);

hugFestControllers.controller('hugFestProfileCtrl', ['$scope', function($scope){
	$scope.profile = [
		{name: "Andy", 
		 email:"sample@test.com",
		 phone:"123-456-7890", 
		 nickname:"The Mastermind", 
		 joinDate: new Date(2013,11,31,00,00,00,00),
		 hugs: 32,
		 img: "Something here"
		},
	];
}]);

/*
hugFestControllers.controller('hugFestRulesCtrl', ['$scope', function($scope){
	$scope.titles = ["Name", "Status", "Hugs", "Games"];
	//do a call here for friends
	$scope.huggers = [
		{name: "Mike", status: "Online", hugs: "52", games: "3"},
		{name: "Manjot", status: "Offline", hugs: "2", games: "1"},
		{name: "Chris", status: "In Game", hugs: "0", games: "5"},
		{name: "Ryan", status: "In Game", hugs: "5", games: "3"}
	];
}]);
*/












