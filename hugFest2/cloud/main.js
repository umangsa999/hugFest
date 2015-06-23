
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

//call this on load for home.html
Parse.Cloud.define("homeRefresh", function(request, response){
	var query = Parse.Query(Parse.User);
	query.get(request.userId,{
		success: function(result){
			var friends = results.get("friends");//incomplete
		},
		error: function(results){
			
		}
	});
});

//call this for getting rules while in game
Parse.Cloud.define("getRules", function(request, response){
	var query = Parse.Query("hugGame");
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
Parse.Cloud.define("setRules", function(request, response){
	var query = Parse.Query("hugGame");
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

//call this to update a player's profile outside a game
Parse.Cloud.define("updatePlayerProfile", function(request, response){
	var query = Parse.Query(Parse.User);
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
	var query = Parse.Query(Parse.User);
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

//call to get target for target screen
Parse.Cloud.define("getCurrentTarget", function(request, response){
	var query = Parse.Query(Parse.User);
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
Parse.Cloud.define("getTopHuggers", function(request, response){
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
	var query = Parse.Query("hugGame");
	query.get(request.gameId,{
		success: function(result){
			response.success({/*get all players from result*/});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});