package jsclub.codefest.bot;

import io.socket.emitter.Emitter.Listener;
import jsclub.codefest.bot.constant.GameConfig;
import jsclub.codefest.sdk.algorithm.AStarSearch;
import jsclub.codefest.sdk.algorithm.MyAlgorithm;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.*;
import jsclub.codefest.sdk.util.GameUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Test_Bot {
    final static String SERVER_URL = "https://codefest.jsclub.me/";
    private static final String GAME_ID = "ff3a6695-c6c0-4e4e-abe2-d51bce2837e7";

    public static void main(String[] args) {
        // Creating a new Hero object with name `player1-xxx` and game id
        // `GameConfig.GAME_ID`.
        Hero player1 = new Hero("player1-xxx", GAME_ID);

        Listener onTickTackListener = objects -> {
            GameInfo gameInfo = GameUtil.getGameInfo(objects);
            MapInfo mapInfo = gameInfo.getMapInfo();
            String path = MyAlgorithm.getEscapePath(mapInfo, player1);
            // Sending the path to the server.
            player1.move(path);
        };

        // This is the code that connects the player to the server.
        player1.setOnTickTackListener(onTickTackListener);
        player1.connectToServer(SERVER_URL);
    }
}
