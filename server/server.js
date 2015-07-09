'use strict';

var express = require('express');
var http = require('http');
var mongoose = require('mongoose');
var bodyparser = require('body-parser');
//var noteRoutes = require('./api/routes/noteRoutes');

//tell node where to look for delegates
var users = require('./api/routes/userRoutes');
var games = require('./api/routes/gameRoutes');

var app = express();
app.use(bodyparser.json());
app.use(express.static( __dirname + '/dist'));
app.set('port', process.env.PORT || 3000);

/*app.get('/api/v1/notes', noteRoutes.collection);
app.post('/api/v1/notes',  noteRoutes.create);
app.get('/api/v1/notes/:id', noteRoutes.findById);
app.put('/api/v1/notes/:id', noteRoutes.update);
app.delete('/api/v1/notes/:id', noteRoutes.destroy);*/
mongoose.connect('mongodb://localhost/hugFest');

var server = http.createServer(app);
server.listen(app.get('port'), function() {
  console.log('Server running on ' + app.get('port'));
});

//Delegate calls to other code

app.get('/', function(req, res){
  res.json({response: "this works", sender: "server", recipient: "Android"});
});

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
app.get('/user/name/:id', users.getName);
app.get('/user/status/:id', users.getStatus);
app.get('/user/hugs/:id', users.getHugs);
app.get('/user/image/:id', users.getImage);
app.get('/user/games/:id', users.getGames);
app.get('/user/:id', users.get);
app.get('/login', users.login);
app.get('/friends/:id', users.getFriends);
app.get('/friends/fb/:id', users.getFBFriends);
app.get('/friends/gl/:id', users.getGLFriends);

app.get('/game/:gameID', games.get);
app.get('/game/user/:id', games.getGameUser);
app.get('/game/friends/:id', games.getFriends);
app.get('/game/target/:gameID', games.getTarget);
app.get('/game/hugs/:gameID', games.getHugs);
app.get('/game/players/:gameID', games.getPlayers);
app.get('/game/rules/:gameID', games.getRules);
app.get('/game/time/:gameID', games.getTime);
app.get('/game/top/:gameId', games.getTop);

//PUT
app.put('/user/status/:id', users.putStatus);
app.put('/user/finish/:id', users.putFinish);
app.put('/user/image/:id', users.putImage);
app.put('/user/add/:id', users.putAddFriend);
app.put('/user/remove/:id', users.putRemoveFriend);
app.put('/user/fb/:id', users.putFacebook);
app.put('/user/gl/:id', users.putGoogle);

app.put('/game/include/:gameID', games.include);
app.put('/game/exclude/:gameID', games.exclude);
app.put('/game/next/:gameID', games.next);
app.put('/game/start/:gameID', games.start);

//DELETE
app.delete('/user/delete/:id', users.deleteUser);
app.delete('/game/delete/:gameID', games.deleteGame);
