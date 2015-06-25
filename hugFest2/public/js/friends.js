Parse.initialize("RNWZPB7mbzkA9YL7pWy2TW4GW5MzeLvfxjonPoEl", "t8uqZXplrRlSJ3qp5ILEJQPWjQFPM7zsCDvmw3qe");
var currentUserObject;
function findFriend(){
	//var userName = prompt("Enter your friends name you lonely person");
	//Call cloud code newFriend

	//hard code getting current user and adding chris to friends


	// var cookie = document.cookie;
	// var id = cookie.split("=");
	// var id = id[1];

	// //getting the current querying user
	// var query = new Parse.Query(Parse.User);
	// query.get( id, {
	// 	success: function(result1){
	// 		currentUserObject = result1;
	// 		var query = new Parse.Query(Parse.User);
	// 		alert("first: " + result1.get("username"));
	// 		query.get( "JWrXFZc0CV", {
	// 			success: function(result2){
	// 				console.log(24);


	// 				var relation = currentUserObject.relation("friends");
	// 				console.log(28);
	// 				relation.add( result2 );
	// 				console.log(30);
	// 				result2.relation("friends").add(result1);
	// 				console.log(32);
	// 				result2.save();
	// 				console.log(34);
	// 				currentUserObject.save();
	// 				console.log(36);
	// 			},
	// 			error: function(error){
	// 				alert( "get chris error: " + error.message );
	// 				response.error(error);
	// 			}
	// 		});
	// 	},
	// 	error: function(error){
	// 		alert( "get myself error: " + error.message );
	// 		response.error(error);
	// 	}
	// });
	var cookie = document.cookie;
	var id = cookie.split("=");
	var id = id[1];
	Parse.Cloud.run( 'addFriend', {user: id}, {
		success: function(code){
			alert("Friend Added");
		},
		error: function(error){
			alert("Error friend not found: " + error.message);
		}
	});

}