'use strict';
var express = require('express');
var mongoose = require('mongoose');

var userSchema = mongoose.Schema({
        name: String,
        username: String,
        password: String,
        status: Number, //0-offline, 1-online, 2-ingame
        FaceBook: String,
        Google: String,
        friends: []
});

var User = mongoose.model('User', userSchema);

/**********************************************************\
|POST                                                      |
\**********************************************************/

exports.createUser = function(req, res){
	var un = req.body.username;
	var ps = req.body.password;
	var nm = req.body.name;

	User.find({username: un}, function(err, user){
		if (err){ //error in attempting to find
			res.json({result:"find error"});
		}
		else if (user.length > 0){ //found existing user
			res.json({result: "exist error"});
		}else{ //user not found
			//create the user model following the User schema
			var user = new User({name: nm, username: un, password: ps, status: 1});
			user.save(function(err, user){
				if (err){
					res.json({result: "create error"});
				}else{
					res.json({result: user._id});
				}
			});
		}
	});
};

/**********************************************************\
|GET                                                       |
\**********************************************************/
exports.getName = function(req, res){
  var id = req.query.id;
  User.find({_id:id}, function(err, user){
    if (err){
        res.json({result:"find error"});
    }else if (user.length > 0){
        res.json({result:user});
    }else{
        res.json({result:"user not found"});
    }
  });
  res.json({idIS: id});
};

exports.getStatus = function(req, res){
  var id = req.query.id;
  res.json({idIS: id});
};

exports.getHugs = function(req, res){
  var id = req.query.id;
  res.json({idIS: id});
};

exports.getImage = function(req, res){
  var id = req.query.id;
  res.json({idIS: id});
};

exports.getGames = function(req, res){
  var id = req.query.id;
  res.json({idIS: id});
};

exports.get = function(req, res){
  var id = req.query.id; //check for both id and username!
  User.findById(id).lean().exec(function(err, user){
    if (err){
        res.json({result: "find error"});
    }else{
            res.json(JSON.stringify(user));
    }
  });
};

exports.login = function(req, res){
	var user = req.body.user;
	var pass = req.body.pass
	res.json({userIs: user, passIs: pass});
};

exports.getFriends = function(req, res){
	var id = req.query.id;
  res.json({idIS: id});
};

exports.getFBFriends = function(req, res){
	var id = req.query.id;
  res.json({idIS: id});
};

exports.getGLFriends = function(req, res){
	var id = req.query.id;
  res.json({idIS: id});
};

/**********************************************************\
|PUT                                                       |
\**********************************************************/
exports.putStatus = function(req, res){
  var id = req.body.id;
  var status = req.body.status;
  res.json({statusIs: status, idIs: id});
};

exports.putFinish = function(req, res){
  var id = req.body.id;
  var hugs = req.body.hugs;
  res.json({idIS: id, hugsIs: hugs});
};

exports.putImage = function(req, res){ //this one needs more work than rest, get image
  var id = req.body.id;
  res.json({idIS: id});
};

exports.putRemoveFriend = function(req, res){
  var userID = req.body.userID;
  var friendID = req.body.friendID;
  res.json({userIDIS: userID, friendIDIS: friendID});
};

exports.putAddFriend = function(req, res){ //this one needs more work than rest
  var userID = req.body.userID;
  var friendID = req.body.friendID;
  res.json({userIDIS: userID, friendIDIS: friendID});
};

exports.putFacebook = function(req, res){
  var userID = req.body.userID;
  var fbID = req.body.fbID;
  res.json({userIDIS: userID, fbIDIS: fbID});
};

exports.putGoogle = function(req, res){ //this one needs more work than rest
  var userID = req.body.userID;
  var glID = req.body.glID;
  res.json({userIDIS: userID, glIDIS: glID});
};

/**********************************************************\
|DELETE                                                    |
\**********************************************************/

exports.deleteUser = function(req, res){
	var user = req.body.id;
  res.json({userIDIS: user});
};