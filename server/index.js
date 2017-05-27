const express = require('express')
const app = express()
const server = require('http').createServer(app)
const io = require('socket.io')(server)
const players = [];

server.listen(8085, () => console.log("Servidor está rodando..."));

io.on('connection', socket => {
	console.log("Jogador Conectado!");
	socket.emit('socketID', { id: socket.id });
	socket.emit('getPlayers', players);

	socket.broadcast.emit('newPlayer', { id: socket.id });
	socket.on('playerMoved', data => {
		data.id = socket.id;
		socket.broadcast.emit('playerMoved', data);

		const addDataToPlayer = player => {
			if (players[i].id == data.id){
				players[i].x = data.x;
				players[i].y = data.y;
			}
		}
		player.map(addDataToPlayer)
	});
	socket.on('disconnect', () => {
		console.log("Jogador Desconectado");
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		player.map( player => { if (players[i].id == socket.id) players.splice(i, 1)})
	});
	players.concat(new player(socket.id, 0, 0));
});

const player = (id, x, y) => {
	this.id = id;
	this.x = x;
	this.y = y;
}