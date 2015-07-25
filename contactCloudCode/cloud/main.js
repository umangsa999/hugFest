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
            //response.success(user[0].getSessionToken());
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
	}else if (numIDs == 2){ //when 2, just OR once
		var wholeQuery = Parse.Query.or(queryArray[0], queryArray[1]);
		wholeQuery.find({
			success:function(users){
				Parse.Cloud.useMasterKey();
				response.success({friends:users});
			},
			error:function(error){
				response.error(error.message);
			}
		});
	}else{ //when more than 2, OR as many times as necessary
		var wholeQuery = Parse.Query.or(queryArray[0], queryArray[1]);
		for (var i = 2; i < numIDs.length; ++i){
			wholeQuery = Parse.Query.or(wholeQuery, queryArray[i]);
		}
		wholeQuery.find({
			success:function(users){
				Parse.Cloud.useMasterKey();
				response.success({friends:users});
			},
			error:function(error){
				response.error(error.message);
			}
		});
	}
});