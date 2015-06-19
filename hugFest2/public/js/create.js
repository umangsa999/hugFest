function createStart(){
	var output = "{\"rules\":[\n";
	output += "{\"time\":\"" + document.getElementById("hr").value + "\"},\n";
	output += "{\"hugs\":\"" + document.getElementById("hugs").value + "\"},\n";
	output += "{\"Running\":\"" + (document.getElementById("running").checked == true ? 1 : 0) + "\"},\n";
	output += "{\"Indoor\":\"" + (document.getElementById("indoor").checked == true ? 1 : 0) + "\"}\n";
	output += "]}";
	/*alert(output);
	var blob = new Blob([output], {type:"text/plain"});
	saveAs(blob, "current.json"); //only saves to downloads folder for privacy*/
	//alert("DONE");
	location.href="target.html" + "?" + output; //inelegant
}
/*
function updateElem(){
	if (
}*/