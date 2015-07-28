Parse.Cloud.define("getTarget", function(request, response){
	Parse.Cloud.useMasterKey();
	var hunterHarry = request.params.hunter;
	console.log("Hunter is: " + hunterHarry);

	var harryQuery = new Parse.Query(Parse.User);
	harryQuery.include("currentTarget");
	var harryQueryPromise = harryQuery.get(hunterHarry);
	harryQueryPromise.then(
		function(harry){

			console.log("found harry: " + harry);
			var gamePointer = harry.get("currentGame");
			var gameFetchPromise = gamePointer.fetch();
			gameFetchPromise.then(
				function(game){

					console.log("found game: " + game.id);
					var gamePlayers = game.relation("players");
					var removeHarryQuery = gamePlayers.query();
					removeHarryQuery.notEqualTo("objectId", harry.id);
					var removeHarryPromise = removeHarryQuery.find();
					removeHarryPromise.then(
						function(withoutHarryList){

							var idList = [];
							for (var i = 0; i < withoutHarryList.length; ++i){
								console.log(i + ": " + withoutHarryList[i].id);
								idList.push(withoutHarryList[i].id);
							}

							var oldTarget = harry.get("currentTarget");
							console.log("list size: "+withoutHarryList.length);
							if (oldTarget !== null){
								console.log("remove: " + oldTarget.id);
								var currIndex = idList.indexOf(oldTarget.id);
								console.log("found to remove at " + currIndex);
								withoutHarryList.splice(currIndex, 1);
							}

							for (var i = 0; i < withoutHarryList.length; ++i){
								console.log(i + ": " + withoutHarryList[i].id);
							}
							var yourTargetNum = Math.floor(
								withoutHarryList.length * Math.random() );
							console.log("your new targetnum: " + yourTargetNum);
							var target = withoutHarryList[yourTargetNum];
							console.log("found target: " + target.id);
							harry.set("currentTarget", target);
							var harrySavePromise = harry.save();
							harrySavePromise.then(
								function(harryAgain){

									console.log("success!");
									response.success(target);
								},
								function(error){

									response.error(error);
								}
							);
						}
					);
				},
				function(error){

					response.error(error.message);
				}
			);
		},
		function(error){

			response.error(error.message);
		}
	);
});



Parse.Cloud.define("getPlayerEndScores", function(request, response){
	var gameID = request.params.gameID;
	console.log("Trying to find players end scores with game ID: " + gameID);
	var Game = Parse.Object.extend("Game");
	var gameQuery = new Parse.Query(Game);
	var gameHolder = gameQuery.get(gameID, {
		success:function(game){
			console.log("Found game: " + game.id);
			var userQuery = new Parse.Query(Parse.User);
			userQuery.equalTo("currentGame", game);
			userQuery.addDescending("currentHugs");
			userQuery.limit(4);
			var promise = userQuery.find({
				success:function(users){
					console.log("results found! \n" + users);
					var usersData = [];
					for (var i = 0; i < users.length; ++i){
						var userObject =
						{"id": users[i].id,
						"name": users[i].get("name"),
						"pictureLink": users[i].get("pictureLink"),
						"hugs": users[i].get("currentHugs")};
						usersData.push(userObject);
					}
					console.log(usersData);
					response.success(usersData);
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



//Get sessionToken
//Input: facebookID
//Output: session token
Parse.Cloud.define("getUserSessionToken", function(request, response) {

    Parse.Cloud.useMasterKey();

    var facebookID = request.params.facebookID;

    var query = new Parse.Query(Parse.User);
    query.equalTo("facebookID", facebookFriendIDsD);
    query.first({
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



Parse.Cloud.define("addFriendUNMutual", function(request, response){
	var hostHelenID = request.params.hostHelenID;
	var targetTomUN = request.params.friendUN;
	console.log("hostHelenID: " + hostHelenID);
	console.log("targetTomUN: " + targetTomUN);
	var tomQuery = new Parse.Query(Parse.User);
	tomQuery.equalTo("username", targetTomUN);
	tomQuery.first({
		success:function(tom){
			console.log(tom);
			var promise = Parse.Cloud.run("addFriendMutual",
				{"hostHelen": hostHelenID, "targetTom": tom.id});
			promise.then(
				function(results){
					console.log("result is:\n\t" + results);
					if (results[0].id == hostHelenID)
						response.success(results[1]);
					else
						response.success(results[0]);
				},
				function(error){
					response.error(error.message);
				}
			);
		},
		error:function(error){
			response.error(error.message);
		}
	});
});



Parse.Cloud.define("addFriendMutual", function(request, response){
	var targetTom = request.params.targetTom;
	var hostHelen = request.params.hostHelen;
	console.log("targetTom: " + targetTom);
	console.log("hostHelen: " + hostHelen);
	var query = new Parse.Query(Parse.User);
	query.get(hostHelen, {
		success:function(Helen){
			console.log("Found helen!");
			var HelenFriends = Helen.relation("friends");
			var friendQuery = new Parse.Query(Parse.User);
			friendQuery.get(targetTom, {
				success:function(Tom){
					console.log("Found tom!");
					Parse.Cloud.useMasterKey();
					var TomFriends = Tom.relation("friends");
					TomFriends.add(Helen);
					HelenFriends.add(Tom);
					console.log("just added friends");
					Parse.Object.saveAll([Helen, Tom]).then(
						function(list){
							console.log("now we friends");
							response.success(list);
						}, function(error){
							response.error(2);//2 means cannot save
						}
					);
				},
				error:function(er){
					response.error(1); //1 means cannot obtain Tom
				}
			});
		},
		error:function(err){
			response.error(0); //0 means cannot obtain Helen
		}
	});
});



Parse.Cloud.define("deleteGameData", function(request, response){
	Parse.Cloud.useMasterKey();
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
	Parse.Cloud.useMasterKey();
	var playerID = request.params.playerID;
	console.log("searching for: " + playerID);
	var userQuery = new Parse.Query(Parse.User);
	userQuery.get(playerID, {
		success:function(user){
			var currentGame = user.get("currentGame").fetch({
				success:function(game){
					var gamePlayers = game.relation("players");
					gamePlayers.remove(user);
					console.log(Number(game.get("numberPlayers")) +
						" players before");
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
										var currentGameMarker =
										game.get("marker").fetch({
											success:function(marker){
												var leftOver =
												Number(marker.get("numberPlayers")) - 1;
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

	console.log("Friends to add to game: " + friendsLength);
	console.log("game to add to: " + gameID);
	for (var i = 0; i < friendsLength; ++i){
		console.log("friend " + i + ": " + friendIDs[i]);
	}

	Parse.Cloud.useMasterKey();
	var Game = Parse.Object.extend("Game");
	var gameQuery = new Parse.Query(Game);
	gameQuery.include("marker");
	var gameQueryPromise = gameQuery.get(gameID);
	gameQueryPromise.then(
		function(game){

			var actualInvitedPlayers = [];
			var numNotAddable = 0;
			console.log("begin processing");
			for (var i = 0; i < friendsLength; ++i){
				console.log("Process invitee #" + i);
				var invitedQuery = new Parse.Query(Parse.User);
				var promise = invitedQuery.get(friendIDs[i]);
				promise.then(
					function(friend){

						if (friend.get("inGame") == true){
							console.log("friend " + friend.get("username") + " is already in a game");
							numNotAddable++;
							console.log(numNotAddable);
						}else{
							actualInvitedPlayers.push(friend);
							console.log(actualInvitedPlayers.length + " are to join");
						}

						if (numNotAddable + actualInvitedPlayers.length == friendsLength){
							console.log("We got everyone!");
							for (var j = 0; j < actualInvitedPlayers.length; ++j){
								console.log("now setting player #" + j);
								actualInvitedPlayers[j].set("inGame", true);
								actualInvitedPlayers[j].set("currentGame", game);
								actualInvitedPlayers[j].set("currentHugs", 0);
								actualInvitedPlayers[j].set("currentTarget", null);
							}
							console.log("all players ready to save");

							Parse.Object.saveAll(actualInvitedPlayers, {
								success:function(list){

									console.log("Everyone saved successfully");
									var gamePlayers = game.relation("players");
									for (var j = 0; j < actualInvitedPlayers.length; ++j){
										console.log("Adding player " + j + " to game");
										gamePlayers.add(actualInvitedPlayers[j]);
									}
									console.log("Added all players to game");
									game.set("numberPlayers", actualInvitedPlayers.length);

									var gameSavePromise = game.save();
									gameSavePromise.then(
										function(gameAgain){

											console.log("successfully saved game");
											var marker = game.get("marker");
											marker.set("numberPlayers", 1 + actualInvitedPlayers.length);
											var markerPromise = marker.save();
											markerPromise.then(
												function(markerAgain){

													console.log("marker saved");
													response.success();
												},
												function(error){
													response.error(error.message);
												}
											);
										},
										function(error){

											response.error(error.message);
										}
									);
								},
								error:function(error){

									response.error(error.message);
								}
							})
						}
					},
					function(error){

						response.error(error.message);
					}
				);
			}
		},
		function(error){

			response.error(error.message);
		}
	);
});



Parse.Cloud.define("increaseScore", function( request, response){
	Parse.Cloud.useMasterKey();
	//Someone has scored, I will get the ParseUser himself, increase his score & alert all player in game
	//1. I want to increase the score for a single player
	var userID = request.params.userID;
	console.log("For user: " + userID);
	//no problemo, I got the parseUser itself

	var parseUserQuery = new Parse.Query(Parse.User);
	parseUserQuery.get(userID).then(
		function(parseUser){
			parseUser.get("currentGame").fetch({
				success:function(game){
					var currentHugs = parseUser.get("currentHugs");
					console.log("hugs are: " + currentHugs);
					parseUser.set( "currentHugs", ++currentHugs);
					parseUser.save().then(
						function(parseUser){
							//TODO notify all users here
							console.log("hugs are now: "+parseUser.get("currentHugs"));
							if (currentHugs == game.get("pointsToWin")){
								//TODO push notification to end game sent to all
								response.success(true);
							}else{
								response.success(false);
							}
						},
						function(error){
							response.error(error);
						}
					);
				},
				error:function(error){
					response.error(error.message);
				}
			});
		},
		function(error){
			response.error(error.message);
		}
	);
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