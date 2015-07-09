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
			var user = new User({name: nm, username: un, password: ps, status: 1}); //create the user model following the User schema
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
  res.send('The name of user ' + id + ' is Derp-Lord!');
};

exports.getStatus = function(req, res){
  var id = req.query.id;
  res.send('Attempting to find status of user ' + id);
};

exports.getHugs = function(req, res){
  var id = req.query.id;
  res.send('Attempting to find lifetime hugs of user ' + id);
};

exports.getImage = function(req, res){
  var id = req.query.id;
  res.send('Attempting to find local copy of image for user ' + id);
};

exports.getGames = function(req, res){
  var id = req.query.id;
  res.send('Attempting to find lifetime games user ' + id + ' has played.');
};

exports.get = function(req, res){
  var id = req.query.id; //check for both id and username!
  User.findById(id).lean().exec(function(err, user){
    if (err){
        res.JSON({result: "find error"});
    }else{
            res.JSON(JSON.stringify(user));
    }
  });
};

exports.login = function(req, res){
        var user = req.query.user;
        var pass = req.query.pass
        res.send("Attempting to login " + user + " with " + pass );
};

exports.getFriends = function(req, res){
        var id = req.query.id;
        res.send("Attempting to send friends of " + id);
};

exports.getFBFriends = function(req, res){
        var id = req.query.id;
        res.send("Attempting to send Facebook friends of " + id);
};

exports.getGLFriends = function(req, res){
        var id = req.query.id;
        res.send("Attempting to send Google Plus friends of " + id);
};

/**********************************************************\
|PUT                                                       |
\**********************************************************/
exports.putStatus = function(req, res){
  var id = req.params.id;
  var status = req.body;
  res.send('Attempting to change status of user ' + id + " to " + status);
};

exports.putFinish = function(req, res){
  var id = req.params.id;
  var text = req.body;
  res.send('Attempting to increase lifetime stats of user ' + id + " by " + text);
};

exports.putImage = function(req, res){ //this one needs more work than rest, get image
  var id = req.params.id;
  res.send('Attempting to change local copy of image for user ' + id);
};

exports.putRemoveFriend = function(req, res){
  var userID = req.params.id;
  var friendID = req.body;
  res.send('Removing ' + friendID + ' and ' + userID +' friendship');
};

exports.putAddFriend = function(req, res){ //this one needs more work than rest
  var userID = req.params.id;
  var friendID = req.body;
  res.send('Adding ' + friendID + ' and ' + userID +' friendship');
};

exports.putFacebook = function(req, res){
  var userID = req.params.userID;
  var fbID = req.body;
  res.send('Adding ' + fbID + ' as Facebook link to ' + userID);
};

exports.putGoogle = function(req, res){ //this one needs more work than rest
  var userID = req.params.userID;
  var glID = req.body;
  res.send('Adding ' + glID + ' as Google Plus link to ' + userID);
};

/**********************************************************\
|DELETE                                                    |
\**********************************************************/

exports.deleteUser = function(req, res){
        var user = req.params.id;
        res.send('Attempting to delete a user with username/userid: ' + user);
};