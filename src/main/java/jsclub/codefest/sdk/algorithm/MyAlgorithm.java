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
            for (int i = 0; i < listTargetPosition.size(); i++) {
                if (BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(i)) < minPath) {
                    listTargetPosition.remove(currentPosition);
                    System.out.println(listTargetPosition.get(i).getCol() + "-" + listTargetPosition.get(i).getRow() + " " + BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(i)) + " PATH | " + currentPosition.getCol() + "-" + currentPosition.getRow() + " PLAYER POSITION");
                    minPath = BaseAlgorithm.manhattanDistance(currentPosition, listTargetPosition.get(i));
                    minPathIndex = i;
                }
            }
            return AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, listTargetPosition.get(minPathIndex));
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

//    private String getBombPlacePath(MapInfo mapInfo, Hero player, String enemyPath) {
//        Position currentPosition = mapInfo.getCurrentPosition(player);
//        List<Position> restrictPosition = new ArrayList<>();
//        restrictPosition.addAll(mapInfo.getWalls());
//        restrictPosition.addAll(mapInfo.getBalk());
//        restrictPosition.addAll(mapInfo.getTeleportGate());
//        Position enemyPosition = mapInfo.getEnemyPosition(player);
//        List<Position> listBalk = new ArrayList<>();
//        if (!this.isEmpty(enemyPath) && enemyPath.length() < 6) {
//            listBalk.add(enemyPosition);
//            cloneBommer.addRestrictedNodes(cloneBommer.getBoxs());
//        } else {
//            listBox.addAll(cloneBommer.boxsGifts);
//        }
//    }

    public static String getEscapePath(MapInfo mapInfo, Hero player) {
        Position currentPosition = mapInfo.getCurrentPosition(player);
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());

        List<Position> safePositionList = new ArrayList<>();
        safePositionList.addAll(mapInfo.getBlank());

        List<Position> bombsPosition = new ArrayList<>();
        bombsPosition.addAll(mapInfo.getBombList());

        boolean isDanger = false;
        if (!bombsPosition.isEmpty()) {
            for (int i = 0; i < bombsPosition.size(); i++) {
                if (bombsPosition.get(i).getCol() == currentPosition.getCol() && bombsPosition.get(i).getRow() == currentPosition.getRow()) {
                    System.out.println("Danger!");
                    isDanger = true;
                    break;
                }
            }
//            if (!safePositionList.isEmpty() && isDanger) {
            if (isDanger) {
                safePositionList.removeAll(mapInfo.getBombList());
//                safePositionList.remove(currentPosition);
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
