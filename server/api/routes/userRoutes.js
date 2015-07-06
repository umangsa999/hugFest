'use strict';
//var User = require('../models/User');

//here is where you write the various functions to talk to server and calls

exports.getName = function(req, res){
  res.send('The name of user ' + req.params.id + ' is Derp-Lord!');
};
