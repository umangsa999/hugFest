'use strict';
var express = require('express');
var mongoose = require('mongoose');

/**********************************************************\
|POST                                                      |
\**********************************************************/

exports.createGame = function(req, res){
	var host = req.body;
	var id = Math.floor(Math.random() * 10000 * Math.random());
	res.json({idIS:id, hostIS:host});
};

/**********************************************************\
|GET                                                       |
\**********************************************************/

exports.get = function(req, res){
	var game = req.query.gameID;
	res.json({gameIS:game});
};

exports.getGameUser = function(req, res){
	var id = req.query.id;
	res.json({idIS: id});
};

exports.getFriends = function(req, res){
	var id = req.query.id;
	res.json({idIS: id});
};

exports.getTarget = function(req, res){
	var game = req.query.gameID;
	var id = req.query.userID;
	res.json({idIS: id, gameIDIS: game});
};

exports.getHugs = function(req, res){
	var game = req.query.gameID;
	var id = req.query.userID;
	res.json({idIS: id, gameIDIS: game});
};

exports.getPlayers = function(req, res){
	var game = req.query.gameID;
	res.json({gameIDIS: game});
};

exports.getRules = function(req, res){
	var game = req.query.gameID;
	res.json({gameIDIS: game});
};

exports.getTime = function(req, res){
	var game = req.query.gameID;
	res.json({gameIDIS: game});
};

exports.getTop = function(req, res){
	var game = req.query.gameID;
	res.json({gameIDIS: game});
};

/**********************************************************\
|PUT                                                       |
\**********************************************************/

exports.include = function(req, res){
	var id = req.body.gameID;
	var un = req.body.username;
	res.json({idIS: id, unIS: un});
};

exports.exclude = function(req, res){
	var id = req.body.gameID;
	var un = req.body.username;
	res.json({idIS: id, unIS: un});
};

exports.next = function(req, res){
	var id = req.body.gameID;
	var un = req.body.username;
	var newTarget = Math.floor(Math.random() * 20 + 1);
	res.json({idIS: id, unIS: un, targetIS: target});
};

exports.start = function(req, res){
	var id = req.body.gameID;
	res.json({idIS: id});
};

/**********************************************************\
|DELETE                                                    |
\**********************************************************/

exports.deleteGame = function(req, res){
	var id = req.body.gameID;
	res.json({idIS: id});
};