'use strict';
var hugFestControllers = angular.module('hugFestControllers', []);

var imageURL = "res/title.png";
hugFestControllers.controller('hugFestStartCtrl', ['$scope', function($scope){
	$scope.logo = imageURL;
	$scope.buttons = [{name: "Hug On!", url: '#/home'}, {name: "Sign In", url: '#/signUp'}];
}]);

hugFestControllers.controller('hugFestLoginCtrl', ['$scope', function($scope){
	$scope.logo == imageURL;
	$scope.inputs = [{text:"Username:", id: "user"}, {text:"Password:", id: "pass"}];
	$scope.buttonLabel = "Sign up";
}]);