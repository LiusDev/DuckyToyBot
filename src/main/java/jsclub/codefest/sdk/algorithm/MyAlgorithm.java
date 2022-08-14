package jsclub.codefest.sdk.algorithm;

import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAlgorithm {

//    boolean mIsCollectGift;

    public static String getPathToEnemy(MapInfo mapInfo, Hero player) {
        Position currentPosition = mapInfo.getCurrentPosition(player);
        Position enemyPosition = mapInfo.getEnemyPosition(player);
        List<Position> restrictPosition = new ArrayList<Position>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBombList());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());

//        System.out.println(getPathToAllSpoils(mapInfo, player, true));
        return AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, enemyPosition);
    }

//    public static String getPathToBomb(MapInfo mapInfo, Hero player) {
//        Position currentPosition = mapInfo.getCurrentPosition(player);
//        List<Position> restrictPosition = new ArrayList<Position>();
//        restrictPosition.addAll(mapInfo.getWalls());
//        restrictPosition.addAll(mapInfo.getBalk());
//        restrictPosition.addAll(mapInfo.getTeleportGate());
//
//        List<Position> bombsPosition = mapInfo.getBombList();
//        if (!bombsPosition.isEmpty()) {
//            Position bombPosition = bombsPosition.get(1);
//            return AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, bombPosition);
//        }
//        return Dir.INVALID;
//    }

//    private static Map<Position, String> getPathToAllSpoils(MapInfo mapInfo, Hero player, boolean isNeedCheckEffectBomb) {
//        Position currentPosition = mapInfo.getCurrentPosition(player);
//        List<Position> restrictPosition = new ArrayList<>();
//        restrictPosition.addAll(mapInfo.getWalls());
//        restrictPosition.addAll(mapInfo.getBalk());
//        restrictPosition.addAll(mapInfo.getTeleportGate());
//        if (isNeedCheckEffectBomb) {
//            restrictPosition.addAll(mapInfo.getBombList());
//        }
//
//        ArrayList<Position> allSpoilsPosition = new ArrayList<>();
//        for (Spoil spoil : mapInfo.getSpoils()) {
//            allSpoilsPosition.add(spoil);
//        }
//        Map<Position, String> listSpoils = AStarSearch.getPathToAllTargets(mapInfo.mapMatrix, restrictPosition, currentPosition, allSpoilsPosition);
//        return listSpoils;
//    }

    public static String getTheShortestSpoilsPath(MapInfo mapInfo, Hero player) {
        Position currentPosition = mapInfo.getCurrentPosition(player);

        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        restrictPosition.addAll(mapInfo.getVirusesPosition());
        restrictPosition.addAll(mapInfo.getDhumanPosition());

        List<Position> listSpoilsPosition = new ArrayList<>();
        for (Spoil spoil : mapInfo.getSpoils()) {
            listSpoilsPosition.add(spoil);
        }

        if (!listSpoilsPosition.isEmpty()) {
            int minPath = BaseAlgorithm.manhattanDistance(currentPosition, listSpoilsPosition.get(0));
            int minPathIndex = 0;
            for (int i = 0; i < listSpoilsPosition.size(); i++) {
                if (BaseAlgorithm.manhattanDistance(currentPosition, listSpoilsPosition.get(i)) < minPath) {
                    minPath = BaseAlgorithm.manhattanDistance(currentPosition, listSpoilsPosition.get(i));
                    minPathIndex = i;
                }
            }
            return AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, listSpoilsPosition.get(minPathIndex));
        }

        return Dir.INVALID;
    }
}
