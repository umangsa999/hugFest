//TODO chris please put some shit here if needed

function game( gameID, playerDB, action ){

	//Get the gameID from DB, we always want to do this
	Game.findById(gameID).exec(function(err, game) {
		if(err){
			return {result:"find error"};
		}else{
			switch(action){
				case "create":
					//we are creating a new game
					var playerList = game.players;
					var playerListObjects = new Array();
					var playerListIDs = new Array();
					
					//put player IDS into the playerListID
					for( var playerIndex = 0; playerIndex<playerList.length; playerIndex++){
						var playerID = game.players[playerIndex];
						playerListIDs.push( playerID ); //push to the end of the array
					}
					
					//Create a array of playerobjects and set people they can target
					for( var playerIndex = 0; playerIndex<playerList.length; playerIndex++){
						// create currentPlayer objects with setting playerListID as all the people
						// they can target and their own ID
						var currentPlayerID = playerListIDs[playerIndex];
						var currentPlayerObject = new Player( playerListIDs, currentPlayerID );

						//remove him/herself from the canTargetlist
						var ownIDIndex = currentPlayerObject.canTarget.indexOf( currentPlayerID );
						if( index > -1 ){
							currentPlayerObject.canTarget.splice( ownIDIndex , 1 );
						}

						playerListObjects.push( currentPlayerObject );
					}

					//Assign targets to the players
					for( var playerIndex = 0; playerIndex<playerList.length; playerIndex++){

						var currentPlayerObject = playerListObjects[ playerIndex ]; //call this x
						var targetIndex = Math.random()*currentPlayerObject.playerList.length;
						var playerTargetObject = currentPlayerObject.playerListObjects[ targetIndex ]; //call this y
						
						//now we set the target in the DB
						//we need to get the user, from the game array from players (need callback)
						user.findById( currentPlayerObject.playerListID ).exec(function(err, u) {
							if(err){
								return {result: "finding player by ID error for setting target"}
							}else{
								u.target = playerTargetObject.playerID;
								u.save( function(err, u) {
									if(err){
										return {result: "save user's target error"};
									}else{
										//get the playerTargetObject and say he can't target currentPlayerObject
										var removeIndex = playerTargetObject.canTarget.indexOf( currentPlayerObject.playerID );
										if( removeIndex > -1){
											playerTargetObject.canTarget.splice( removeIndex, 1 );
										}

										//now remove y as a potential target in all users
										for( var playerIndex2 = 0; playerIndex2 < playerList.length; playerIndex2++ ){

											//get the current person
											var currentPlayerObject2 = playerListObjects[ playerIndex2 ];
											var removeIndex2 = currentPlayerObject2.canTarget.indexOf( playerTargetObject );
											if( removeIndex2 > -1 ){
												currentPlayerObject2.canTarget.splice( removeIndex2 , 1 );
											}
										}

										//Finally, set the playTargetObject as hunted by currentPlayerobject
										user.findById( playerTargetObject.playerListID ).exec(function(err, u2) {
											if(err){
												return {result: "finding player by ID error for setting hunter"}
											}else{
												u2.hunter = currentPlayerObject.playerID;
												u2.save( function(err, u){
													return{ result: "save succcess"}
												});
											}
										});
									}
								});
							}
						});
					}
					break;

				case "update": //someone scores

					 //currentPlayerDB scored, find it via game and increase the numHug by 1;
					var currentPlayerID = playerDB;
					user.findById( currentPlayerID ).exec(function(err, u){
						if(err){
							return {result: "find user in update case error"}
						}else{
							u.numHugs = u.numHugs + 1;
							//get the target that he hugged on
							var currentPlayerTarget = u.target;
							var currentPlayerTargetIndex = game.players.indexOf( currentPlayerTarget );
							var currentPlayerIndex = game.players.indexOf( currentPlayerID );

							//get all the players still in the game
							var currentPlayersList = game.players;
							currentPlayersList.splice( currentPlayerIndex , 1 );
							currentPlayersList.splice(  currentPlayerTargetIndex , 1 );
							//we've removed the current player & last target, now find a new target within this

							var newTargetIndex = Math.random()*currentPlayersList.length;
							var newTargetID = currentPlayersList[newTargetIndex];

							//set the new target for the current player
							u.target = newTargetID;
							u.save({ function(err, u){
									if(err){
										return {result:"Save target and/or update point error in switch case"}
									}else{
										//now we need to set another person hunting the target, get the target
										user.findById( newTargetID ).exec(function(err, u2){
											if(err){
												return { result: "finding target error" }
											}else{
												//get the array of hunters hunting target and add this current player to it
												//u2.hunter = currentPlayerTarget;
											}
										});
										//return {result:"success save target and/or update point in switch case"};
									}

								}
							});
						}
					});
					break;

				case "leave": //someone leaves the game, call him LARRY (larry leaves)

					//1a. Check if there are only two players now, if so end the game & set winner
		
					//There are at least still 3 players in the game (go to 1b-4)

					//1b. Remove LARRY from game.players
					//2. Get whoever LARRY was targeting (TOM the target)
					//3. Get whoever was hunting LARRY, (HENRY the hunter)
					//4. Set HENRY to hunt TOM 

					break;

				case "join": //someone joins the game

					//

					break;

				default:
					return {result:"Player action error"};
			}

			//SAVE THE CHANGES, 
			game.save( function (err, g){
					if( err){
						return {result:"save gave error"};
					}else{
						return {result: "save success!"};
					}
				}
			);
		}
	});
}

function Player( playerListIDs, playerID ){
	this.playerListIDs = playerListIDs;
	this.playerID = playerID;
};