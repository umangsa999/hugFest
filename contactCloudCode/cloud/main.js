Parse.Cloud.define("sendInvitePush", function(request, response){
	Parse.Cloud.useMasterKey();
	var gameID = request.params.gameID;
	var maxPoints = request.params.maxPoints;
	var hostHelenName = request.params.name;
	var listFriends = request.params.friends;
	var listFriendsLength = listFriends.length;
	var doneCount = 0;

	for (var i = 0; i < listFriendsLength; ++i){
		var userQuery = new Parse.Query(Parse.User);
		var userPromise = userQuery.get(listFriends[i]);
		userPromise.then(
			function(user){
				var installationQuery = new Parse.Query(Parse.Installation);
				installationQuery.equalTo("currentUserID", user.id);

				Parse.Push.send(
					{
						where:installationQuery,
						data:{
							title: hostHelenName + " wants to Contact you!",
							alert: "Click here to join " + hostHelenName + "'s game.",
							GAMEID:gameID,
							MAXPOINTS:maxPoints,
							action:"INVITE"
						}
					},{
						success:function(){
							++doneCount;
							if (doneCount == listFriendsLength)
								response.success();
						},
						error:function(error){
							++doneCount;
							if (doneCount == listFriendsLength)
								response.success();
						}
					}
				);
			},
			function(error){
				response.error(error.message);
			}			
		);
	}

});

Parse.Cloud.define("sendScorePush", function(request, response){
	Parse.Cloud.useMasterKey();
	console.log("START OF SENDSCOREPUSH");
	var gameID = request.params.gameID;
	var hunterHarryName = request.params.hunter;
	var hunterHarryID = request.params.hunterID;
	var targetTomName = request.params.targetName;
	console.log(hunterHarryName + " with id " + hunterHarryID + " just got " + targetTomName + " in " + gameID);

	var gameChannel = "game" + gameID;
	console.log("game channel is: " + gameChannel);

	var installationQuery = new Parse.Query(Parse.Installation);
	installationQuery.notEqualTo("currentUserID", hunterHarryID);
	installationQuery.equalTo("channels", gameChannel);

	Parse.Push.send(
		{
			where:installationQuery,
			data:{
				title:"Contact has been made!",
				alert:hunterHarryName + " has contacted " + targetTomName,
				SCORERID:hunterHarryID,
				NAME:hunterHarryName,
				SCOREEENAME:targetTomName,
				action:"SCORE"
			}
		},{
			success:function(){
				response.success();
			},
			error:function(error){
				response.error(error.message);
			}
		}
	);
});

Parse.Cloud.define("sendEndPush", function(request, response){
	Parse.Cloud.useMasterKey();
	console.log("START SENDENDPUSH");
	var winnerWallyID = request.params.winnerID;
	var gameID = request.params.gameID;
	console.log("ENDING GAME " + gameID);
	var installationQuery = new Parse.Query(Parse.Installation);
	installationQuery.equalTo("channels", "game"+gameID);
	installationQuery.notEqualTo("currentUserID", winnerWallyID);

	Parse.Push.send(
		{
			where:installationQuery,
			data:{
				title: "We have a winner!",
				alert: "Click to see who won!",
				GAMEID:gameID,
				action:"END"
			}
		},{
			success:function(){
				response.success();
			},
			error:function(error){
				response.error(error.message);
			}
		}
	);
});

Parse.Cloud.define("joinGame", function(request, response){
	Parse.Cloud.useMasterKey();
	var gameID = request.params.gameID;
	var joinerJerryID = request.params.userID;

	console.log("Trying to add " + joinerJerryID + " to game " + gameID);
	var userQuery = new Parse.Query(Parse.User);
	var userQueryPromise = userQuery.get(joinerJerryID);
	userQueryPromise.then(
		function(joinerJerry){

			console.log("Found " + joinerJerry.get("name"));
			var Game = Parse.Object.extend("Game");
			var gameQuery = new Parse.Query(Game);
			gameQuery.include("marker");
			var gameQueryPromise = gameQuery.get(gameID);
			gameQueryPromise.then(
				function(game){

					console.log("Found: " + game.id);
					var numPlayers = game.get("numberPlayers");
					if ( numPlayers == 20 ){
						console.log("game already maxed");
						response.error({"message": "Player limit reached for game", "code":20});
					}else{
						console.log("Adding player to game");
						var gamePlayers = game.relation("players");
						gamePlayers.add(joinerJerry);
						game.set("numberPlayers", numPlayers + 1);

						var gameSavePromise = game.save();
						gameSavePromise.then(
							function(gameAgain){

								console.log("Added player to game");
								joinerJerry.set("currentGame", game);
								joinerJerry.set("currentTarget", null);
								joinerJerry.set("currentHugs", 0);
								joinerJerry.set("inGame", true);

								var joinerJerryPromise = joinerJerry.save();
								joinerJerryPromise.then(
									function(joinerJerryAgain){

										console.log("time to edit marker");
										var marker = game.get("marker");
										marker.set("numberPlayers", numPlayers + 1);

										var markerPromise = marker.save();
										markerPromise.then(
											function(markerAgain){
												console.log("Added player to game! enjoy");
												var resultObject =
													{"points": game.get("pointsToWin"), "gameID": game.id};
												response.success(resultObject);
											},
											function(error){
												response.error(error.message);
											}
										);
									},
									function(error){

										reponse.error(error.message);
									}
								);
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
		},
		function(error){

			response.error(error.message);
		}
	);
});



Parse.Cloud.define("getNewTarget", function(request, response){
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

							if (withoutHarryList.length == 0){
								response.error("NO PLAYERS");
							}

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
								Parse.Cloud.run("sendScorePush",
									{
										"gameID":game.id,
										"hunter":harry.get("name"),
										"hunterID":harry.id,
										"targetName":oldTarget.get("name")
									}
								);
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
    query.equalTo("facebookID", facebookID);
    query.first({
        success: function(user) {
            console.log(user.length);
            user.fetch({
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



Parse.Cloud.define("removePlayerFromGame", function(request, response){
	Parse.Cloud.useMasterKey();
	var doneDianaID = request.params.playerID;
	console.log("searching for: " + doneDianaID);
	
	var userQuery = new Parse.Query(Parse.User);
	var userQueryPromise = userQuery.get(doneDianaID);
	userQueryPromise.then(
		function(doneDiana){

			var totalGames = doneDiana.get("totalGames") + 1;
			var oldTotalHugs = doneDiana.get("totalHugs");
			var currentHugs = doneDiana.get("currentHugs");
			doneDiana.set("inGame", false);
			doneDiana.set("currentTarget", null);
			doneDiana.set("currentGame", null);
			doneDiana.set("totalGames", totalGames);
			doneDiana.set("totalHugs", currentHugs + oldTotalHugs);
			doneDiana.set("currentHugs", 0);
			var savePromise = doneDiana.save();
			savePromise.then(
				function(doneDianaAgain){

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
	gameQuery.include("host");
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
							console.log("not addable: " + numNotAddable);
						}else{
							actualInvitedPlayers.push(friend);
							console.log(actualInvitedPlayers.length + " are to join");
						}

						if (numNotAddable + actualInvitedPlayers.length == friendsLength){
							if (numNotAddable == friendsLength){
								response.error({"code":-20, "message":"Not enough players can join"});
							}else{
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
										game.set("numberPlayers", actualInvitedPlayers.length + 1);

										var gameSavePromise = game.save();
										gameSavePromise.then(
											function(gameAgain){

												console.log("successfully saved game");
												var marker = game.get("marker");
												marker.set("numberPlayers", 1 + actualInvitedPlayers.length);
												var markerPromise = marker.save();
												markerPromise.then(
													function(markerAgain){

														var list = [];
														for (var k = 0; k < actualInvitedPlayers.length; ++k){
															list.push(actualInvitedPlayers[k].id);
														}
														console.log("marker saved");
														Parse.Cloud.run("sendInvitePush",
															{
																"gameID":game.id,
																"maxPoints":game.get("pointsToWin"),
																"name":game.get("host").get("name"),
																"friends":list
															}
														);
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
								game.set("isOver", true);
								var gameSavePromise = game.save();
								gameSavePromise.then(
									function(gameAgain){
										gameAgain.get("marker").fetch({
											success:function(marker){
												marker.set("isOver", true);
												var markerSavePromise = marker.save();
												markerSavePromise.then(
													function(markerAgain){
														Parse.Cloud.run("sendEndPush", {"gameID":game.id, "winnerID":parseUser.id});
														response.success(true);
													},function(error){
														response.error(error.message);
													}
												);
											},
											error:function(error){
												response.error(error.message);
											}
										});
									},function(error){
										response.error(error.message);
									}
								);
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