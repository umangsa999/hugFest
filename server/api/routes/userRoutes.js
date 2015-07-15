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
	friends: [],
	target: String,
	hunter: [],
	currentHugs: Number,
	totalHugs: Number,
	games: Number,
	profile: String
});

var User = mongoose.model('User', userSchema);
exports.User = User;

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
exports.getTest = function(req, res){
	User.find().exec(function(err, users){
		res.json({result:users});
	});
}

exports.get = function(req, res){
  var id = req.query.id;
  User.findById(id).lean().exec(function(err, user){
    if (err){
        res.json({result: "find error"});
    }else{
        res.json(user);
    }
  });
};

exports.getName = function(req, res){
  var id = req.query.id;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.name});
    }
  });
};

exports.getStatus = function(req, res){
  var id = req.query.id;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.status});
    }
  });
};

exports.getTotHugs = function(req, res){
  var id = req.query.id;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.totalHugs});
    }
  });
};

exports.getCurrHugs = function(req, res){
  var id = req.query.id;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.currentHugs});
    }
  });
};

exports.getTarget = function(req, res){
	var id = req.query.id;
	User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.target});
    }
  });
};

exports.getImage = function(req, res){
  var id = req.query.id;
  res.json({idIS: id});
};

exports.getGames = function(req, res){
  var id = req.query.id;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.games});
    }
  });
};

exports.getProfile = function(req, res){
  var id = req.query.id;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.profile});
    }
  });
};

exports.login = function(req, res){
	var user = req.body.user;
	var pass = req.body.pass
	User.find({username:user, password:pass}).exec(function(err, user){
		if (err){
			res.json({result:"find error"});
		}else if (user.length == 0){
			res.json({result:"matching user not found"});
		}else{
			res.json({result:user._id});
		}
	});
	res.json({userIs: user, passIs: pass});
};

exports.getFriends = function(req, res){
	var id = req.query.id;
	User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        res.json({result:user.friends});
    }
  });
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
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        user.status = status;
        user.save(function(err, u){
            if (err){
				res.json({result:"save error"});
            }else{
                res.json({result:"success"});
            }
        });
    }
  });
};

exports.putImage = function(req, res){ //this one needs more work than rest, get image
  var id = req.body.id;
  console.log(id);
  res.json({idIS: id});
};

exports.putProfile = function(req, res){ //this one needs more work than rest, get image
  var id = req.body.id;
  var profile = req.body.profile;
  User.findById(id).exec(function(err, user){
    if (err){
        res.json({result:"find error"});
    }else{
        user.profile = profile;
        user.save(function(err, u){
            if (err){
				res.json({result:"save error"});
            }else{
                res.json({result:"success"});
            }
        });
    }
  });
};

exports.putRemoveFriend = function(req, res){
  var userID = req.body.userID;
  var friendID = req.body.friendID;
  User.findById(userID).exec(function(err, hostHelen){
    if (err){
        res.json({result:"find error Helen"});
    }else{
        User.findById(friendID).exec(function(err, targetTom){
		    if (err){
		        res.json({result:"find error Tom"});
		    }else{
		        var indexTom = hostHelen.friends.indexOf(targetTom._id);
		        hostHelen.friends.splice(indexTom, 1);
		        hostHelen.save(function(err, h){
		            if (err){
		                res.json({result:"remove error Helen"});
		            }else{
		                var indexHelen = targetTom.friends.indexOf(h._id);
		                targetTom.friends.splice(indexHelen, 1);
		                targetTom.save(function(err, h){
		                    if (err){
		                        hostHelen.friends.push(targetTom._id);
		                        hostHelen.save(function(err,hH){
		                            if (err){
		                                res.json({result:"save error Helen"});
		                            }else{
		                                res.json({result:"worst error"});
		                            }
		                        });
		                    }else{
		                        res.json({result:"success"});
		                    }
		                });
		            }
		        });
		    }
	    });
    }
  });
};

exports.putAddFriend = function(req, res){ //this one needs more work than rest
  var userID = req.body.userID;
  var friendID = req.body.friendID;
  User.findById(userID).exec(function(err, hostHelen){
    if (err){
        res.json({result:"find error Helen"});
    }else{
        User.findById(friendID).exec(function(err, targetTom){
		    if (err){
		        res.json({result:"find error Tom"});
		    }else{
		        if (hostHelen.friends.indexOf(targetTom._id) == -1){
		            hostHelen.friends.push(targetTom._id);
			        hostHelen.save(function(err, h){
			            if (err){
			                res.json({result:"save error Helen"});
			            }else{
			                targetTom.friends.push(h._id);
			                targetTom.save(function(err, h){
			                    if (err){
			                        var indexTom = hostHelen.friends.indexOf(targetTom._id);
			                        hostHelen.friends.splice(indexTom, 1);
			                        hostHelen.save(function(err, hH){
			                            if (err){
			                                res.json({result:"worse error"});
			                            }else{
			                                res.json({result:"remove error Helen"});
			                            }
			                        });
			                    }else{
			                        res.json({result:"success"});
			                    }
			                });
			            }
			        });
				}else{
			        res.json({result:"already friend"});
			    }
		    }
	    });
    }
  });
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
	users.findById(user).exec(function(err, u){
		if (err){
			res.json({result:"find error"});
		}else{
			u.remove(function(err, doc){
				if (err){
					res.json({result:"remove error"});
				}else if (doc){
					res.json({result:doc});
				}else{
					res.json({result:"success"});
				}
			}); 
		}
	});
};
