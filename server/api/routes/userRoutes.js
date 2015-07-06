'use strict';
//var User = require('../models/User');

//here is where you write the various functions to talk to server and calls

exports.getName = function(req, res){
  var id = req.params.id;
  res.send('The name of user ' + id + ' is Derp-Lord!');
};
