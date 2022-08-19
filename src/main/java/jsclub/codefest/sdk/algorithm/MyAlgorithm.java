package jsclub.codefest.sdk.algorithm;

import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.*;
import jsclub.codefest.sdk.util.GameUtil;
import jsclub.codefest.sdk.util.SocketUtil;

import java.util.*;

public class MyAlgorithm {

//    boolean mIsCollectGift;
    boolean isDanger;

    private String getPathToEnemy(MapInfo mapInfo, Hero player) {
        Position currentPosition = mapInfo.getCurrentPosition(player);
        Position enemyPosition = mapInfo.getEnemyPosition(player);
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBombList());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());

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

    private static Map<Position, String> getPathToAllFood(MapInfo mapInfo, Hero player, boolean isNeedCheckEffectBomb) {
        Position currentPosition = mapInfo.getCurrentPosition(player);
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        if (isNeedCheckEffectBomb) {
            restrictPosition.addAll(mapInfo.getBombList());
        }

        ArrayList<Position> allSpoilsPosition = new ArrayList<>();
        for (Spoil spoil : mapInfo.getSpoils()) {
            allSpoilsPosition.add(spoil);
        }
        Map<Position, String> listSpoils = AStarSearch.getPathToAllTargets(mapInfo.mapMatrix, restrictPosition, currentPosition, allSpoilsPosition);
        return listSpoils;
    }

    private static String getTheShortestPath(MapInfo mapInfo, Hero player, List<Position> restrictPosition, List<Position> listTargetPosition) {
        Position currentPosition = mapInfo.getCurrentPosition(player);

        if (!listTargetPosition.isEmpty()) {
            int minPath = BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(0));
            int minPathIndex = 0;
            int targetPath;
            for (int i = 0; i < listTargetPosition.size(); i++) {
                targetPath = BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(i));
                if (targetPath < minPath) {
                    minPath = targetPath;
                    minPathIndex = i;
                }
            }
            String path;
            path = AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, listTargetPosition.get(minPathIndex));
            return path;
        }
        return Dir.INVALID;
    }

    private String getEatPath(MapInfo mapInfo, Hero player, boolean forceEat) {
        Position currentPosition = mapInfo.getCurrentPosition(player);
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        if (!forceEat) {
            restrictPosition.addAll(mapInfo.getBombList());
        }

        Map<Position, String> pathToAllSpoil = getPathToAllFood(mapInfo, player, !forceEat);
        if (!pathToAllSpoil.isEmpty()) {
            String steps;
            for (Map.Entry<Position, String> path : pathToAllSpoil.entrySet()) {
                steps = AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, path.getKey());
                if (!this.isEmpty(steps)) {
                    return steps;
                }
            }
        }
        return Dir.INVALID;
    }

    public static String getEscapePath(MapInfo mapInfo, Hero player) {
        Position currentPosition = mapInfo.getCurrentPosition(player);

        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());

        List<Position> safePositionList = new ArrayList<>();
        safePositionList.addAll(mapInfo.getBlank());

        List<Position> bombsEffPosition = new ArrayList<>();
        bombsEffPosition.addAll(mapInfo.getBombList());

        List<Position> realBombsPosition = new ArrayList<>();
        realBombsPosition.addAll(mapInfo.getBombs());

        boolean isDanger = false;
        if (!bombsEffPosition.isEmpty()) {
            for (int i = 0; i < bombsEffPosition.size(); i++) {
                if (bombsEffPosition.get(i).getCol() == currentPosition.getCol() && bombsEffPosition.get(i).getRow() == currentPosition.getRow()) {
                    isDanger = true;
                    break;
                }
            }
            if (!safePositionList.isEmpty() && isDanger) {
                for (int i = 0; i < safePositionList.size(); i++) {
                    for (int j = 0; j < bombsEffPosition.size(); j++) {
                        if (safePositionList.get(i).getCol() == bombsEffPosition.get(j).getCol() && safePositionList.get(i).getRow() == bombsEffPosition.get(j).getRow()) {
                            safePositionList.remove(safePositionList.get(i));
                        }
                    }
                }

                for (int i = 0; i < safePositionList.size(); i++) {
                    for (int j = 0; j < realBombsPosition.size(); j++) {
                        if (safePositionList.get(i).getCol() == realBombsPosition.get(j).getCol() && safePositionList.get(i).getRow() == realBombsPosition.get(j).getRow()) {
                            safePositionList.remove(safePositionList.get(i));
                        }
                    }
                }
                restrictPosition.addAll(realBombsPosition);

                return getTheShortestPath(mapInfo, player, restrictPosition, safePositionList);
            }
        }
        return Dir.INVALID;
    }

//    public static String getTheShortestPathToTarget(MapInfo mapInfo, Hero player, boolean havePill) {
//        Position currentPosition = mapInfo.getCurrentPosition(player);
//
//        List<Position> restrictPosition = new ArrayList<>();
//        restrictPosition.addAll(mapInfo.getWalls());
//        restrictPosition.addAll(mapInfo.getBalk());
//        restrictPosition.addAll(mapInfo.getTeleportGate());
//        restrictPosition.addAll(mapInfo.getVirusesPosition());
//        restrictPosition.add(mapInfo.getEnemyPosition(player));
//        if (havePill == false) {
//            restrictPosition.addAll(mapInfo.getDhumanPosition());
//        }
//
//        List<Position> listTargetPosition = new ArrayList<>();
//        for (Spoil spoil : mapInfo.getSpoils()) {
//            listTargetPosition.add(spoil);
//        }
//        listTargetPosition.addAll(mapInfo.getHumanPosition());
//        if (havePill == true) {
//            listTargetPosition.addAll(mapInfo.getDhumanPosition());
//        }
//
//        if (!listTargetPosition.isEmpty()) {
//            int minPath = BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(0));
//            int minPathIndex = 0;
//            for (int i = 0; i < listTargetPosition.size(); i++) {
//                if (BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(i)) < minPath) {
//                    minPath = BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(i));
//                    minPathIndex = i;
//                }
//            }
//            return AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, listTargetPosition.get(minPathIndex));
//        }
//
//        return Dir.INVALID;
//    }

    private boolean isEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            return s.length() == 0;
        }
    }
}
