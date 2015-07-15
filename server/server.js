'use strict';

var express = require('express');
var http = require('http');
var mongoose = require('mongoose');
var bodyparser = require('body-parser');

//tell node where to look for delegates
var users = require('./api/routes/userRoutes');
var games = require('./api/routes/gameRoutes');

var app = express();
app.use(bodyparser.json());
app.use(express.static( __dirname + '/dist'));
app.set('port', process.env.PORT || 3000);

mongoose.connect('mongodb://localhost/hugFest');

var server = http.createServer(app);
server.listen(app.get('port'), function() {
  console.log('Server running on ' + app.get('port'));
});

//Delegate calls to other code

app.get('/', function(req, res){
  res.json({response: "this works"});
});

app.get('/test', users.getTest);

/**********************************************************\
|REMEMBER REMEMBER THE WONDER OF                           |
|CREATE       =      POST                                  |
|RETRIEVE     =      GET                                   |
|UPDATE       =      PUT                                   |
|DELETE       =      DELETE                                |
\**********************************************************/

//POST
app.post('/user/create', users.createUser);
app.post('/game/create', games.createGame);

//GET
app.get('/user', users.get);
app.get('/user/name', users.getName);
app.get('/user/status', users.getStatus);
app.get('/user/hugs/total', users.getTotHugs);
app.get('/user/hugs/current', users.getCurrHugs);
app.get('/user/target', users.getTarget);
app.get('/user/image', users.getImage);
app.get('/user/games', users.getGames);
app.get('/user/profile', users.getProfile);
app.get('/login', users.login);
app.get('/friends', users.getFriends);
app.get('/friends/fb', users.getFBFriends);
app.get('/friends/gl', users.getGLFriends);

app.get('/game', games.get);
app.get('/game/players', games.getPlayers);
app.get('/game/rules', games.getRules);
app.get('/game/time', games.getTime);
app.get('/game/top', games.getTop);

//PUT
app.put('/user/status', users.putStatus);
app.put('/user/image', users.putImage);
app.put('/user/add', users.putAddFriend);
app.put('/user/remove', users.putRemoveFriend);
app.put('/user/fb', users.putFacebook);
app.put('/user/gl', users.putGoogle);
app.put('/user/profile', users.putProfile);

app.put('/game/include', games.include);
app.put('/game/exclude', games.exclude);
app.put('/game/next', games.next);

//DELETE
app.delete('/user/delete', users.deleteUser);

/*(function(){
  var childProcess = require("child_process");
  var oldSpawn = childProcess.spawn;
  function mySpawn(){
    console.log('spawn called');
    console.log(arguments);
    var result = oldSpawn.apply(this, arguments);
    return result;
  }
  childProcess.spawn = mySpawn;
})();*/

//console.log(process.env.PATH);
/*var child = require('child_process').spawn('java',['Game']).on('error', function(err){
  throw err});
child.stdout.on('data', function(data){
  console.log('stdout: ' + data);
});
*/