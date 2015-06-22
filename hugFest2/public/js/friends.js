Parse.initialize("yingEebCnDBZKPt7WfM6Hn7Jy6SEjtU3l3dHsWNj", "AdmT1bexKKsNvr8KU3wIGAtBafoKcfPolcmStnS4");
function addFriend(){
	var userName = prompt("Enter your friends name you lonely person");


	

	//Call cloud code newFriend
	Parse.Cloud.run( 'hello', {user: userName}, {
		success: function(code){
			alert(code.message);
		},
		error: function(error){
			alert(error.message);
		}
	});
}