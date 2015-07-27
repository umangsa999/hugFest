//Get sessionToken
//Input: facebookID
//Output: session token
Parse.Cloud.define("getUserSessionToken", function(request, response) {

    Parse.Cloud.useMasterKey();

    var facebookID = request.params.facebookID;

    var query = new Parse.Query(Parse.User);
    query.equalTo("facebookID", facebookID);
    query.find({
        success: function(user) {
            console.log(user.length);
            user[0].fetch({
                success: function (user) {
                    response.success(user.getSessionToken() );
                },
                error: function (user, err) {
                    response.error(err.message);
                }
            });
        },
        error: function(error) {
            response.error(error.description);
        }
    });
});

Parse.Cloud.define("addFriendFacebookMutual", function(request, response){
	Parse.Cloud.useMasterKey();
	
	var hostHelen = request.hostHelen;
	var facebookFriendIDs = request.params.friends;
	
	
});

Parse.Cloud.define("addFriendMutual", function(request, response){
	var targetTom = request.targetTom;
	var hostHelen = request.hostHelen;
	
	var query = new Parse.Query(Parse.User);
	query.get(hostHelen, {
		success:function(Helen){
			var HelenFriends = hostHelen.relation("friends");
			var helenFriendQuery = HelenFriends.query();
			helenFriendQuery.equalTo("username", targetTom);
			helenFriendQuery.find({
				success:function(HelenFindTom){
					response.error(1); //1 means friend exists
				},
				error:function(e){
					var friendQuery = new Parse.Query(Parse.User);
					friendQuery.equalTo("username", targetTom);
					friendQuery.find({
						success:function(Tom){
							Parse.Cloud.useMasterKey();
							var TomFriends = Tom.relation("friends");
							TomFriends.add(Helen);
							HelenFriends.add(Tom);
							Parse.Object.saveAll([Helen, Tom]);
						},
						error:function(er){
							response.error(2); //2 means cannot obtain Tom
						}
					});
				}
			});
		},
		error:function(err){
			response.error(0); //0 means cannot obtain Helen
		}
	});
});

Parse.Cloud.define("deleteGameData", function(request, response){
	var type = request.params.type;
	var ID = request.params.ID;
	var query = Parse.Query(Parse.Object.extend(type));
	query.get(ID, {
		success:function(data){
			data.destroy({
				success:function(object){
					response.success("good");
				},
				error:function(error){
					response.error(error.message);
				}
			});
		},
		error:function(error){
			response.error(error.message);
		}
	});
});

Parse.Cloud.define("removeFromGame", function(request, response){
	var playerID = request.params.playerID;
	console.log("searching for: " + playerID);
	var userQuery = new Parse.Query(Parse.User);
	userQuery.get(playerID, {
		success:function(user){
			var currentGame = user.get("currentGame").fetch({
				success:function(game){
					var gamePlayers = game.relation("players");
					gamePlayers.remove(user);
					console.log(Number(game.get("numberPlayers")) + " players before");
					var numPlayersLeft = Number(game.get("numberPlayers")) - 1;
					console.log("Players left: " + numPlayersLeft);
					
					if (numPlayersLeft <= 2){
						//TODO end game and display results
						console.log("You stupid");
						response.success(false);
					}else{
						game.set("numberPlayers", numPlayersLeft);
						game.save({
							success:function(something){
								console.log("removed player from game");
								Parse.Cloud.useMasterKey();
								console.log("about to unset game");
								user.unset("currentGame");
								console.log("unset game");
								user.save({inGame: false}, {
									success:function(something2){
										console.log("removed game from player");
										var currentGameMarker = game.get("marker").fetch({
											success:function(marker){
												var leftOver = Number(marker.get("numberPlayers")) - 1;
												marker.save({numberPlayers: leftOver}, {
													success:function(something3){
														response.success(true);
													},
													error:function(error){
														response.error(error.message);
													}
												});
											},
											error:function(error){
												response.error(error.message);
											}
										});
									},
									error:function(error){
										console.log("Cannot remove game from player");
										gamePlayers.add(user);
										game.save({numberPlayers: numPlayersLeft + 1}, {
											success:function(something4){
												response.error({message:"stay in"});
											},
											error:function(error){
												response.error({message:"worst case"});
											}
										});
									}
								});
							},
							error:function(error){
								console.log("Cannot remove user and save");
								response.error(error.message);
							}
						});
					}
				},
				error:function(error){
					console.log("Could not find game");
					response.error(error.message);
				}
			});
		},
		error:function(error){
			console.log("Cannot find user");
			response.error(error.message);
		}
	});
});

//GET array of ParseUser objectID
//Give nothing
Parse.Cloud.define("addFriendsToGame", function(request, response){
	var gameID = request.params.gameID;
	var friendIDs = request.params.friendIDs;
	var friendsLength = friendIDs.length;
	
	var Game = Parse.Object.extend("Game");
	var query = new Parse.Query(Game);
	query.get(gameID, {
		success:function(game){
			console.log("Found game with id: " + gameID);
			var host = game.get("host").fetch({
				success: function(user){
					console.log("found host");
					var friendsAvailable = user.relation("friends");
					var actualFriends = [];
					var numNotAddable = 0;
					console.log("begin processing");
					for (var i = 0; i < friendsLength; ++i){
						console.log("generating friend query " + i  + ": " + friendIDs[i]);
						var tempQuery = friendsAvailable.query();
						var holder = tempQuery.get(friendIDs[i], {
							success:function(tempFriend){
								console.log("found " + tempFriend.id);
								if (tempFriend.get("inGame") == true){
									++numNotAddable;
									console.log("now " + numNotAddable + " not added");
								}else{
									console.log("before: " + actualFriends.length);
									actualFriends.push(tempFriend);
									console.log("after: " + actualFriends.length);
									console.log("actual + not added: " + (actualFriends.length + numNotAddable));
									if (Number(actualFriends.length + numNotAddable) == friendIDs.length){
										console.log("found " + actualFriends.length + " users");
										var gamePlayers = game.relation("players");
										for (var j = 0; j < actualFriends.length; ++j){
											console.log("adding user " + j + ": " + actualFriends[j]);
											gamePlayers.add(actualFriends[j]);
										}
										console.log("saving");
										for (var k = 0; k < actualFriends.length; ++k){
											console.log("saving game to friend: " + k);
											actualFriends[k].set("currentGame", game);
											actualFriends[k].set("inGame", true);
										}
										actualFriends.push(game);
										console.log("pushed together");
										Parse.Object.saveAll(actualFriends,{
											success:function(list){//TODO FIX THIS
												response.success();
											},
											error:function(error){
												response.error("GO HOME");
											}
										});
									}
								}
							},
							error:function(error){
								response.error(error.message);
							}
						});
					}
					console.log("started all gets");
				},
				error: function(error){
					response.error(error.message);
				}
			});
		},
		error:function(error){
			response.error(error.message);
		}
	});
});


Parse.Cloud.define("increaseScore", function( request, response){

	//Someone has scored, I will get the ParseUser himself, increase his score & alert all player in game
	//1. I want to increase the score for a single player
	var parseUser = request.parseUser;
	//no problemo, I got the parseUser itself
	var currentHugs = parseUser.get("currentHugs");
	parseUser.set( "currentHugs", ++currentHugs);
	parseUser.save


});

//GET FBID array
//GIVE Parse array
Parse.Cloud.define("getParseFriendsFromFBID", function(request, response){
	var ids = request.params.ids;
	var numIDs = ids.length;
	console.log("number of ids is: " + numIDs);
	var queryArray = [];
	for (var i = 0; i < numIDs; ++i){
		console.log("Processing number: " + i); //create separate searches for each facebook friend
		queryArray.push((new Parse.Query(Parse.User)).equalTo("facebookID", ids[i].id));
	}

	if (numIDs == 1){ //when only one, no need for OR
		queryArray[0].find({
			success:function(user){
				Parse.Cloud.useMasterKey();
				response.success({friends:user});
			},
			error:function(error){
				response.error(error.message);
			}
		});
	}else {
		console.log("more than 1");
		var wholeQuery = Parse.Query.or(queryArray[0], queryArray[1]);
		if (numIDs > 2){ //for just two, no more than one or
			console.log("more than 2");
			for (var i = 2; i < numIDs.length; ++i){
				console.log("joining " + i + " to past");
				console.log("before: "+ wholeQuery.toJSON());
				wholeQuery = Parse.Query.or(wholeQuery, queryArray[i]);
				console.log("after: " + wholeQuery.toJSON());
			}
		}
		console.log("about to find");
		wholeQuery.find({
			success:function(users){
				Parse.Cloud.useMasterKey();
				console.log("found: " + users.length);
				for (var temp = 0; temp < users.length; ++temp)
					console.log(users[temp].id);
				response.success({friends:users});
			},
			error:function(error){
				response.error(error.message);
			}
		});
	}
});