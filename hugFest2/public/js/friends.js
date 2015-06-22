arse.initialize("RNWZPB7mbzkA9YL7pWy2TW4GW5MzeLvfxjonPoEl", "t8uqZXplrRlSJ3qp5ILEJQPWjQFPM7zsCDvmw3qe");
function addFriend(){
	var userName = prompt("Enter your friends name you lonely person");

	//Call cloud code newFriend
	Parse.Cloud.run( 'hello', {user: userName}, {
		success: function(code){
			alert("Success");
		},
		error: function(error){
			alert("Error");
		}
	});

}