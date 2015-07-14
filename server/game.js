//TODO chris please put some shit here if needed

function game( gameID, player, action ){

	//Get the gameID from DB, we always want to do this
	Game.findById(gameID).lean().exec(function(err, game)){
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
						var playerID = game.players[playerIndex]._id;
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
						game.players[playerIndex].target = playerTargetObject.playerID;

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
					}
					break;

				case "update": //someone scores
					break;


				case "leave": //someone leaves the game
					break;

				case "join": //someone joins the game 
					break;

				default:
					return {result:"Player action error"};
			}
		}
	}
}

var Player = Class({
	//array of players that the current player can target
	initialize: function( playerListIDs, playerID ){
		this.canTarget = playerListIDs;
		this.playerID = playerID;
	}

});
