function createStart()
{
	var output = "{\"rules\":[\n";
	output += "{\"time\":\"" + document.getElementById("hr").value + "\"},\n";
	output += "{\"hugs\":\"" + document.getElementById("hugs").value + "\"},\n";
	output += "{\"run\":\"" + document.getElementById("running").value + "\"},\n";
	output += "{\"indoor\":\"" + document.getElementById("indoor").value + "\"}\n";
	output += "]}";
	alert(output);
	location.href="target.html";
}