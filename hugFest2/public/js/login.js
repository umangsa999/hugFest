
Parse.initialize("RNWZPB7mbzkA9YL7pWy2TW4GW5MzeLvfxjonPoEl", "t8uqZXplrRlSJ3qp5ILEJQPWjQFPM7zsCDvmw3qe");
function login(){
	var user = new Parse.User();
	user.set("username", "my name");
	user.set("password", "my pass");
	  
	user.signUp(null, {
	  success: function(user) {
	    // Hooray! Let them use the app now.
	  },
	  error: function(user, error) {
	    // Show the error message somewhere and let the user try again.
	    alert("Error: " + error.code + " " + error.message);
	  }
	});
};