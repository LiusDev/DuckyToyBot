package jsclub.codefest.sdk.model;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jsclub.codefest.sdk.constant.ServerSocketConfig;
import jsclub.codefest.sdk.socket.data.Dir;
import jsclub.codefest.sdk.socket.data.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jsclub.codefest.sdk.util.SocketUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class Hero {
    private static final Logger LOGGER = LogManager.getLogger(Hero.class);
    private String playerID = "";
    private String gameID = "";
    private Socket socket;
    private Emitter.Listener onTickTackListener = objects -> {
    };

    public Hero(String playerID, String gameID) {
        this.playerID = playerID;
        this.gameID = gameID;
    }

    /**
     * This function sets the listener for the tick-tack event.
     * 
     * @param onTickTackListener This is the listener that will be called when the
     *                           server sends a tick or a tack.
     */
    public void setOnTickTackListener(Emitter.Listener onTickTackListener) {
        this.onTickTackListener = onTickTackListener;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getGameID() {
        return gameID;
    }

    /**
     * It connects to the server and sets up the listeners for the events that the
     * server will emit
     * 
     * @param serverUrl The URL of the server.
     * @return A boolean value.
     */
    public Boolean connectToServer(String serverUrl) {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
        socket = SocketUtil.init(serverUrl);

        if (socket == null) {
            LOGGER.error("Socket null - can't connect");
            return false;
        }

        socket.on(Socket.EVENT_CONNECT, objects -> {
            String gameParams = new Game(gameID, playerID).toString();
            try {
                socket.emit(ServerSocketConfig.JOIN_GAME, new JSONObject(gameParams));
                LOGGER.info("{} connected into game {}!", this.playerID, this.gameID);
            } catch (JSONException e) {
                LOGGER.error(e);
            }
        });
        socket.on(ServerSocketConfig.TICKTACK_PLAYER, onTickTackListener);
        socket.on(Socket.EVENT_CONNECT_ERROR, objects -> LOGGER.error("Connect Failed " + objects[0].toString()));
        socket.on(Socket.EVENT_DISCONNECT, objects -> LOGGER.info("{} Disconnected!", this.playerID));

        socket.connect();
        return true;
    }

    /**
     * > The `move` function takes a string as an argument and emits a
     * `DRIVE_PLAYER` event to the server with the string as the data
     * 
     * @param step The direction to move the player.
     */
    public void move(String step) {
        if (socket != null && step.length() > 0) {
            Dir dir = new Dir(step);
            LOGGER.debug("Player = {} - Dir = {}", this.playerID, dir);
            try {
                socket.emit(ServerSocketConfig.DRIVE_PLAYER, new JSONObject(dir.toString()));
            } catch (JSONException e) {
                LOGGER.error(e);
            }
        }
    }
}
