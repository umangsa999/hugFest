
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

Parse.Cloud.define("homeRefresh", function(request, response){
	var query = Parse.Query(Parse.User);
	query.find(request.userId,{
		success: function(result){
			var friends = results.get("friends");
		},
		error: function(results){
			
		}
	});
});

Parse.Cloud.define("getRules", function(request, response){
	var query = Parse.Query("hugGame");
	query.equalTo("objectId", request.gameId);
	query.find({
		success: function(result){
			response.success({rules: result.get("rulesString")});
		},
		error: function(error){
			response.error({err: error});
		}
	});
});

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

Parse.Cloud.define("updatePlayer", function(request, response){
	var query = Parse.Query(Parse.User);
	query.find(request.gameId, {
		sucess: function(result){
			if (request.name)
				result.set("name", request.name);
			if (request.nickname)
				result.set("nickname", request.nickname);
			if (request.status)
				result.set("status", request.status);
			if (request.target)
				result.set("target", request.target);
			if (request.currentGame)
				result.set("currentGame", request.currentGame);
			if (request.finishGame){
				result.set("totalGames", result.get("totalGames") + 1);
				result.set("totalHugs", result.get("totalHugs") + request.finishGame);
			}
			if (request.currentLocation)
				result.set("currentLocation", request.currentLocation);
			if (request.friends)
				result.set("friends", request.friends);
			if (request.currentImage)
				result.set("currentImage", request.currentImage);
			
			response.success();
		},
		error: function(error){
			response.error(err: error);
		}
	});
});