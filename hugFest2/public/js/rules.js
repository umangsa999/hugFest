var rules;
window.onload = function(){
	rules = location.href;
	rules = rules.replace(/%22/g, "\"");
	
	rules = rules.substring(rules.indexOf("?") + 1, rules.length);
	parse(rules);
}

function parse(rules){
	var table = document.getElementById("ruleTable");
	while (table.rows.length > 0){table.deleteRow(0);}
	var JSONrules = JSON.parse(rules, function(k,v){
		if (k !='rules' && k.length > 2){
			//alert("k: "+ k);
			//alert("v: "+ v);
			//alert(k + ": " + v);
			var table = document.getElementById("ruleTable");
			var currentRow = table.insertRow(-1);
			var currentCell = currentRow.insertCell(-1);
			if (k == "time")
				currentCell.innerHTML = "You have up to " + v + " to hug";
			else if (k == "hugs")
				currentCell.innerHTML = "First to " + v + " hugs wins";
			else if (v == 1)
				currentCell.innerHTML = k + " is not allowed";
			else if (v == 0)
				currentCell.innerHTML = k + " is allowed";
			else if (v)
				currentCell.innerHTML = k + ": " + v;
			else
				currentCell.innerHTML = k;
		}
	});
}

function returnPage(){
	location.href='target.html?' + rules;
}