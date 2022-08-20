import io.socket.emitter.Emitter.Listener;
import jsclub.codefest.bot.constant.GameConfig;
import jsclub.codefest.sdk.algorithm.MyAlgorithm;
import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.GameInfo;
import jsclub.codefest.sdk.socket.data.MapInfo;
import jsclub.codefest.sdk.util.GameUtil;

public class Champion_Bot {
    final static String SERVER_URL = "https://jsclub.me/";
    final static String GAME_ID = "57837cfa-7ce5-4f51-afb9-188485e1a2cd";
    final static String PLAYER_ID = "player1-xxx";

    public static void main(String[] args) {
        Hero player = new Hero(PLAYER_ID, GAME_ID);
        Listener onTickTackListener = objects -> {
            GameInfo gameInfo = GameUtil.getGameInfo(objects);
            MapInfo mapInfo = gameInfo.getMapInfo();
            MyAlgorithm.getPath(mapInfo, player);
        };

        player.setOnTickTackListener(onTickTackListener);
        player.connectToServer(SERVER_URL);
    }
}
