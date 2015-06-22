Parse.initialize("RNWZPB7mbzkA9YL7pWy2TW4GW5MzeLvfxjonPoEl", "t8uqZXplrRlSJ3qp5ILEJQPWjQFPM7zsCDvmw3qe");
function login(){
	var user = new Parse.User();
	user.set("username", document.getElementsByName("User")[0].value);
	user.set("password", document.getElementsByName("Pass")[0].value);
	  
	user.signUp(null, {
	  success: function(user) {
	    // Hooray! Let them use the app now.
	    location.href='home.html';
	  },
	  error: function(user, error) {
	    // Show the error message somewhere and let the user try again.
	    alert("Error: " + error.code + " " + error.message);
	  }
	});
};