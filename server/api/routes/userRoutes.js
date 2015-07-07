'use strict';
//var User = require('../models/User');

/**********************************************************\
|GET                                                       |
\**********************************************************/
exports.getName = function(req, res){
  var id = req.params.id;
  res.send('The name of user ' + id + ' is Derp-Lord!');
};

exports.getStatus = function(req, res){
  var id = req.params.id;
  res.send('Attempting to find status of user ' + id);
};

exports.getHugs = function(req, res){
  var id = req.params.id;
  res.send('Attempting to find lifetime hugs of user ' + id);
};

exports.getImage = function(req, res){
  var id = req.params.id;
  res.send('Attempting to find local copy of image for user ' + id);
};

exports.getGames = function(req, res){
  var id = req.params.id;
  res.send('Attempting to find lifetime games user ' + id + ' has played.');
};

exports.get = function(req, res){
  var id = req.params.id; //check for both id and username!
  res.send('Attempting to get user ' + id);
};

exports.login = function(req, res){
	var text = req.body;
	res.send("Attempting to login with " + text );
};

exports.getFriends = function(req, res){
	var id = req.params.id;
	res.send("Attempting to send friends of " + id);
};

exports.getFBFriends = function(req, res){
	var id = req.params.id;
	res.send("Attempting to send Facebook friends of " + id);
};

exports.getGLFriends = function(req, res){
	var id = req.params.id;
	res.send("Attempting to send Google Plus friends of " + id);
};

/**********************************************************\
|POST                                                      |
\**********************************************************/

exports.createUser = function(req, res){
	var values = req.body;
	var id = Math.floor(Math.random() * 10000 * Math.random());
	res.send('Attempting to create a user with\n\tusername and password: ' + values + "\n\tid: " + id);
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