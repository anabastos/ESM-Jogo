const express = require('express')
const app = express()
const server = require('http').createServer(app)
const io = require('socket.io')(server)
let players = [];

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
			if (player.id == data.id){
				player.x = data.x;
				player.y = data.y;
			}
		}
		players.map(addDataToPlayer)
	});
	socket.on('disconnect', () => {
		console.log("Jogador Desconectado");
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		players.forEach( player => { if (player.id == socket.id) 
			players = players.filter(p => p.id != player.id)
		})
	});
	players.push(Player(socket.id, 0, 0));
});

const Player = (id, x, y) => {
	let player = 
	{
		id: id,
		x: x,
		y: y
	}
	return player;
}