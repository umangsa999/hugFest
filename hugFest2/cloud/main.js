/****************************************************************\
|* Functions below are for Friends
|*
\****************************************************************/

// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("findFriend", function(request, response) {
	var query = new Parse.Query(Parse.User);
	var queryPromise = query.find({ //This gets all the users
	  success: function(results) {
	  	response.success({message:"success"});
	  },
	  error: function(error) {
	    // error is an instance of Parse.Error.
	    response.error({message:"Error in query"});
	  }
	})
});

//call this to see all friends
Parse.Cloud.define("getAllFriendsWithStats", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId,{
		success: function(result){
			//get all friends
			//accumulate only name, status, skill (hugs / game)
			response.success({/*the accumulated JSON*/});
		},
		error: function(error){
			response.error({err: error});
		}
});

//call this on load for home.html
Parse.Cloud.define("homeRefresh", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId,{
		success: function(result){
			//get friends here
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call for invites
Parse.Cloud.define("getFreeFriends", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId,{
		success: function(result){
			//getFriends
			//query friends for not in game (status == 1)
			response.success(/*list of friends with status 1*/);
		},
		error: function(error){
			response.error({err: error});
		}
});

/****************************************************************\
|* Functions below are for Rules
|*
\****************************************************************/
//call this for getting rules while in game
Parse.Cloud.define("getRules", function(request, response){
	var query = new Parse.Query("hugGame");
	query.get(request.gameId,{
		success: function(result){
			response.success({rules: result.get("rulesString")});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call this for setting rules after create page
//this function may be redundant
Parse.Cloud.define("setGameRules", function(request, response){
	var query = new Parse.Query("hugGame");
	query.get(request.gameId,{
		success: function(result){
			result.set("rulesString", request.rules, {
				error: function(error){
					response.error({err: error});
				}
			});
			response.success();
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

/****************************************************************\
|* Functions below are for changing User
|*
\****************************************************************/

//call this to update a player's profile outside a game
Parse.Cloud.define("updatePlayerProfile", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId, {
		sucess: function(result){
			if (request.name)
				result.set("name", request.name);
			if (request.nickname)
				result.set("nickname", request.nickname);
			if (request.status)
				result.set("status", request.status);
			if (request.currentLocation)
				result.set("currentLocation", request.currentLocation);
			if (request.currentImage)
				result.set("currentImage", request.currentImage);
			
			response.success();
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call this to update a player's profile during/after a game
Parse.Cloud.define("playerFinishGame", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId, {
		success: function(result){
			result.set("totalHugs", result.get("totalHugs") + request.currentHugs;
			result.set("totalGames", result.get("totalGames") + 1);
			result.set("currentHugs", 0);
			//change game to none
			//change target to none
			response.success();
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

/****************************************************************\
|* Functions below are for queries within a game
|*
\****************************************************************/

//call to get target for target screen
Parse.Cloud.define("getCurrentTarget", function(request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId, {
		success: function(result){
			//give target
			response.success();
		},
		error: function(error){
			response.error({err: error});
		}
});

//call this to update the target page scoreboard
Parse.Cloud.define("getTopHuggersInGame", function(request, response){
	var query = Parse.Query("hugGame");
	query.get(request.gameId,{
		success: function(result){
			//get players in the game
			//query the players for descending currentHugs
			response.success({/*top 3 players*/});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call this for the players inside the current game
Parse.Cloud.define("getPlayersInGame", function(request, response){
	var query = new Parse.Query("hugGame");
	query.get(request.gameId,{
		success: function(result){
			response.success({/*get all players from result*/});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});