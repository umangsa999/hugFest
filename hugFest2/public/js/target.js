var rules;
window.onload = function(){
	rules = location.href;
	rules = rules.replace(/%22/g, "\"");
	
	rules = rules.substring(rules.indexOf("?") + 1, rules.length);
	//alert(rules);
}

function changeLocation(){
	location.href = "rules.html" + "?" + rules;
}