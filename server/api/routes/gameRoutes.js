'use strict';
var express = require('express');
var mongoose = require('mongoose');

/**********************************************************\
|POST                                                      |
\**********************************************************/

exports.createGame = function(req, res){
	var host = req.body;
	var id = Math.floor(Math.random() * 10000 * Math.random());
	res.send('Attempting to create a game with\n\tid: ' + id + "\n\thost: " + host);
};

/**********************************************************\
|GET                                                       |
\**********************************************************/

exports.get = function(req, res){
	var game = req.query.gameID;
	res.send("Attempting to give game that is " + game);
};

exports.getGameUser = function(req, res){
	var id = req.query.id;
	res.send("Attempting to give game that contains " + id);
};

exports.getFriends = function(req, res){
	var id = req.query.id;
	res.send("Attempting to give all games with friends of " + id + " in it");
};

exports.getTarget = function(req, res){
	var game = req.query.gameID;
	var id = req.query.userID;
	res.send("Attempting to get target of " + id + " in game " + game);
};

exports.getHugs = function(req, res){
	var game = req.query.gameID;
	var id = req.query.userID;
	res.send("Attempting to get current hugs of " + id + " in game " + game);
};

exports.getPlayers = function(req, res){
	var game = req.query.gameID;
	res.send("Attempting to get all players in game " + game);
};

exports.getRules = function(req, res){
	var game = req.query.gameID;
	res.send("Attempting to get rules in game " + game);
};

exports.getTime = function(req, res){
	var game = req.query.gameID;
	res.send("Attempting to get time left in game " + game);
};

exports.getTop = function(req, res){
	var game = req.query.gameID;
	res.send("Attempting to get top players in game " + game);
};

/**********************************************************\
|PUT                                                       |
\**********************************************************/

exports.include = function(req, res){
	var id = req.params.gameID;
	var un = req.body;
	res.send("Adding " + un + " to game " + id );
};

exports.exclude = function(req, res){
	var id = req.params.gameID;
	var un = req.body;
	res.send("Removing " + un + " to game " + id );
};

exports.next = function(req, res){
	var id = req.params.gameID;
	var un = req.body;
	var newTarget = Math.floor(Math.random() * 20 + 1);
	res.send("Noting hug of " + un + " to game " + id + ". New target is: " + newTarget);
};

exports.start = function(req, res){
	var id = req.params.gameID;
	res.send("Starting game " + id);
};

/**********************************************************\
|DELETE                                                    |
\**********************************************************/

exports.deleteGame = function(req, res){
	var id = req.params.gameID;
	res.send('Attempting to delete a game with id: ' + id);
};