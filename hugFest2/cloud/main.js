
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
	var query = new Parse.Query(Parse.User);

	console.error("Tried to call hello");
	query.find({
	  success: function(results) {
	  	var testUser = results[0];
	  	testUser.setStatus(2, {
	  		success: function(r){

	  		},
	  		error: function(r){

	  		}
	  	});
	  	testUser.setEmail("WORKS@BALLER.com", {
	  		success: function(r){
	  		},
	  		error: function(r){
	  		}
	  	});

	  	testUser.save();
	  	console.error("WE GOT HERE!");
	  },

	  error: function(error) {
	    // error is an instance of Parse.Error.
	  }
	});
 	response.success({message:"Hello world!"});
 	//response.error("Hello world!");
});