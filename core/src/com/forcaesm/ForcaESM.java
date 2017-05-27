package com.forcaesm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import io.socket.client.Socket;
import io.socket.client.IO;
import io.socket.emitter.Emitter;
import com.forcaesm.sprites.Boneco;

public class ForcaESM extends ApplicationAdapter {

	private final float UPDATE_TIME = 1/60f;
	float timer;
	SpriteBatch batch;
	private Socket socket;
	String id;
	Boneco boneco;
	Texture bonecoForca;
	HashMap<String, Boneco> bonecos;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		bonecoForca = new Texture("Boneco.png");
		bonecos = new HashMap<String, Boneco>();
		connectSocket();
		configSocketEvents();
	}

	public void updateServer( float dt ) {
		timer += dt;
		if ( timer > UPDATE_TIME && boneco != null & boneco.hasMoved()){
			JSONObject data = new JSONObject();
			try {
				data.put("x", boneco.getX());
				data.put("y", boneco.getY());
				socket.emit("playerMoved", data);
			} catch (JSONException e){
				Gdx.app.log("SOCKET.IO", "Error sending update data");
			}
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput(Gdx.graphics.getDeltaTime());

		updateServer(Gdx.graphics.getDeltaTime());

		batch.begin();
		if (boneco != null) {
			boneco.draw(batch);
		}
		for (HashMap.Entry<String, Boneco> entry : bonecos.entrySet()) {
			entry.getValue().draw(batch);
		}
		batch.end();
	}

	public void handleInput(float dt){
		if(boneco != null) {
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				boneco.setPosition(boneco.getX() + (-200 * dt), boneco.getY());
			} else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				boneco.setPosition(boneco.getX() + (+200 * dt), boneco.getY());
			}
		}
	}

	public void connectSocket () {
		try {
			socket = IO.socket("http://localhost:8085");
			socket.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void configSocketEvents () {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				boneco = new Boneco(bonecoForca);
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + id);
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error:" + e);
				}
 			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					Gdx.app.log("SocketIO", "New Player Connected: " + playerId);
					bonecos.put(playerId, new Boneco(bonecoForca));
				} catch (JSONException e) {
					Gdx.app.log("SocketIO", "Error getting new player:" + e);
				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					id = data.getString("id");
					bonecos.remove(id);
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					Double x = data.getDouble("x");
					Double y = data.getDouble("y");
					if (bonecos.get(playerId) != null) {
						bonecos.get(playerId).setPosition(x.floatValue(), y.floatValue());
					}
				}catch(JSONException e){
					Gdx.app.log("SocketIO", "Error getting disconnected PlayerID");
				}
			}
		}).on("getPlayers", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray objects = (JSONArray) args[0];
				try {
					for(int i = 0; i < objects.length(); i++){
						Boneco bonecoJogador = new Boneco(bonecoForca);
						Vector2 position = new Vector2();
						position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
						position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
						bonecoJogador.setPosition(position.x, position.y);

						bonecos.put(objects.getJSONObject(i).getString("id"), bonecoJogador);
					}
				} catch(JSONException e){

				}
			}
		});
	}
	
	@Override
	public void dispose () {
		super.dispose();
		bonecoForca.dispose();
	}
}
