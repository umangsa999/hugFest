'use strict';
var express = require('express');
var mongoose = require('mongoose');
var gameCode = require('../../game.js');

var gameSchema = mongoose.Schema({
	host: String,
	players: [],
	rules: String,
	start: Date,
	duration: Number,
	hugLimit: Number,
	winner: String
});

var Game = mongoose.model('Game', gameSchema);

/**********************************************************\
|POST                                                      |
\**********************************************************/

exports.createGame = function(req, res){
	var h = req.body.host;
	var r = req.body.rules;
	var u = req.body.users;
	
	var game = new Game({host:h, players: u, rules: r, duration:r.duration, hugLimit: r.hugLimit});
	
	game.save(function(err, g){
		if (err){
			res.json({result:"create error"});
		}else{
			//var cpp = require('child_process').spawn('java', ['Game', host, g._id]);
			gameCode.game(g._id, h, "create");
			res.json({result:g._id});
		}
	});
};

/**********************************************************\
|GET                                                       |
\**********************************************************/
exports.get = function(req, res){
	var game = req.query.gameID;
	Game.findById(game).lean().exec(function(err, g){
		if (err){
			res.json({result:"find error"});
		}else if (g.length > 0){
			res.json({result:g});
		}else{
			res.json({result:"game not found"});
		}
	});
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
	var game = req.body.gameID;
	var user = req.body.userID;
	var newTarget = Math.floor(Math.random() * 20 + 1);
	res.json({idIS: game, unIS: user, targetIS: target});
};