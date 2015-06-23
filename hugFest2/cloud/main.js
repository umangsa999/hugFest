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

/****************************************************************\
|* Functions below are for general use
|*
\****************************************************************/

//call this to get a specific User
//Param: {userId: objectId of target User}
//Return: Parse.User
function getUser (request, response){
	var query = new Parse.Query(Parse.User);
	query.get(request.userId, {
		success: function(result){
			response.success(result);
		},
		error: function(error){
			response.error(error){
		}
	});
});

//call this to get a specific hugGame
//Param: {gameId: objectId of target hugGame}
//Return: hugGame
function getGame (request, response){
	var query = new Parse.Query("hugGame");
	query.get(request.gameId, {
		success: function(result){
			response.success(result);
		},
		error: function(error){
			response.error(error){
		}
	});
});

/****************************************************************\
|* Functions below are for changing/viewing User
|*
\****************************************************************/

//call this to update a player's profile outside a game
//Param: {userId: current User objectId, name: new name, nickname: new nickname, status: new status, currentLocation: new location}
//Note: inputs are optional, only add what needs to be changed
Parse.Cloud.define("updatePlayerProfile", function(request, response){
	getUser(request, {
		sucess: function(result){
			if (request.name)
				result.set("name", request.name);
			if (request.nickname)
				result.set("nickname", request.nickname);
			if (request.status)
				result.set("status", request.status);
			if (request.currentLocation)
				result.set("currentLocation", request.currentLocation);
			
			response.success();
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call this to update a player's profile during/after a game
//Param: {userId: current User objectId, currentHugs: hugs from game)
Parse.Cloud.define("playerFinishGame", function(request, response){
	getUser(request, {
		success: function(result){
			result.set("totalHugs", result.get("totalHugs") + request.currentHugs);
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
|* Functions below are for Friends
|*
\****************************************************************/

//call this to see all friends
//Param: {userId: current User's objectId}
//Return: {friends:[friend1:{objectId: objectId, name: name, status: status, hugs: totalHugs, games: totalGames}, ...]}
Parse.Cloud.define("getAllFriendsWithStats", function(request, response){
	getUser(request, {
		success: function(result){
			//get all friends
			//accumulate only id, name, status, skill (hugs / game)
			response.success({/*the accumulated JSON*/});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call this on load for home.html
//Param: {userId: current User's objectId}
//Return: unsure
Parse.Cloud.define("homeRefresh", function(request, response){
	getUser(request, {
		success: function(result){
			//get friends
			//find only those with status 2
			//find mutual friends?
			response.success(/*list of friends*/);
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call for invites. 
//Param: {userId: [current User's objectId]}
//Return: {friends:[friend1:{objectId: objectId, name: name, hugs: totalHugs, games: totalGames}, ...]}
Parse.Cloud.define("getFreeFriends", function(request, response){
	getUser(request, {
		success: function(result){
			//getFriends
			//query friends for not in game (status == 1)
			response.success(/*list of friends with status 1*/);
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

/****************************************************************\
|* Functions below are for Rules
|*
\****************************************************************/
//call this for getting rules while in game
//Rules are in format: {rules:[time: "xx:xx", hugs: xx, canRun: 1, canIndoor: 0, ...]}
Parse.Cloud.define("getRules", function(request, response){
	getGame(request, {
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
//Rules should be in format: {rules:[time: "xx:xx", hugs: xx, canRun: 1, canIndoor: 0, ...]}
Parse.Cloud.define("setGameRules", function(request, response){
	getGame(request, {
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
|* Functions below are for queries within/about a game
|*
\****************************************************************/

//call to get target for target screen
//Param: {userId: objectId of current User}
//Returns: Parse.User that is target of current User
Parse.Cloud.define("getCurrentTarget", function(request, response){
	getUser(request, {
		success: function(result){
			//give target
			response.success();
		},
		error: function(error){
			response.error({err: error});
		}
	});
);

//call this to update the target page scoreboard
//Param: {gameId: objectId of currentGame, num: number of players wanted}
//Returns: {player1: [objectId: id, name: name, score: currentHugs], ...}
Parse.Cloud.define("getTopHuggersInGame", function(request, response){
	getGame(request, {
		success: function(result){
			//get players in the game
			//query the players for descending currentHugs limited to num
			response.success({/*requested players*/});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

//call this for the players inside the current game
//Param: {gameId: objectId of currentGame}
//Returns: {player1: [objectId: id, name: name, score: currentHugs], ...}
Parse.Cloud.define("getPlayersInGame", function(request, response){
	getGame(request, {
		success: function(result){
			response.success({/*get all players from result*/});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});