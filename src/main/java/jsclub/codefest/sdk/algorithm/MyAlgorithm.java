package jsclub.codefest.sdk.algorithm;

import jsclub.codefest.sdk.model.Hero;
import jsclub.codefest.sdk.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class MyAlgorithm {

    public static void getPath(MapInfo mapInfo, Hero player) {
        boolean havePill = false;
        int playerPill = mapInfo.getPlayerPill(player);
        if (playerPill > 0) {
            havePill = true;
        }

        if (isDanger(mapInfo, player)) {
            player.move(bombsDodge(mapInfo, player, false));
        } else {
            if (getPathToSpoils(mapInfo, player).isEmpty() && saveHuman(mapInfo, player, havePill).isEmpty()) {
                List<Bomb> bombsList = mapInfo.getBombs();
                if (bombsList.size() == 0) {
                    player.move(getShortestAroundBalkPath(mapInfo, player));
                    player.move(Dir.DROP_BOMB);
                    player.move(bombsDodge(mapInfo, player, true));
                }
            } else {
                player.move(getPathToSpoils(mapInfo, player));
                player.move(saveHuman(mapInfo, player, havePill));
            }
        }
    }


    private static String getShortestAroundBalkPath(MapInfo mapInfo, Hero player) {
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getBombList());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        restrictPosition.addAll(mapInfo.getVirusesPosition());

        List<Position> aroundBalkPos = getAroundBalk(mapInfo);
        if (!aroundBalkPos.isEmpty()) {
            String path = getTheShortestPath(mapInfo, player, restrictPosition, aroundBalkPos);
            return path;
        }
        return Dir.INVALID;
    }

    private static List<Position> getAroundBalk(MapInfo mapInfo) {
        List<Position> balkPos = mapInfo.getBalk();
        List<Position> balkAroundPos = new ArrayList<>();
        balkAroundPos.clear();
        for (int i = 0; i < balkPos.size(); i++) {
            for (int j = 1; j <= 4; j++) {
                balkAroundPos.add(balkPos.get(i).nextPosition(j, 1));
            }
        }
        return balkAroundPos;
    }

    private static String saveHuman(MapInfo mapInfo, Hero player, boolean havePill) {
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        restrictPosition.addAll(mapInfo.getBombList());
        restrictPosition.addAll(mapInfo.getVirusesPosition());

        List<Position> listHumanPosition = new ArrayList<>();
        listHumanPosition.addAll(mapInfo.getNhumanPosition());
        if (havePill) {
            listHumanPosition.addAll(mapInfo.getDhumanPosition());
        }

        if (!listHumanPosition.isEmpty()) {
            return getTheShortestPath(mapInfo, player, restrictPosition, listHumanPosition);
        }
        return Dir.INVALID;
    }

    private static String getTheShortestPath(MapInfo mapInfo, Hero player, List<Position> restrictPosition, List<Position> listTargetPosition) {
        Position currentPosition = mapInfo.getCurrentPosition(player);

        if (!listTargetPosition.isEmpty()) {
            int minPath = 100;
            int minPathIndex = 0;
            int targetPath;
            for (int i = 0; i < listTargetPosition.size(); i++) {
                targetPath = AStarSearch.aStarSearch(mapInfo.mapMatrix, restrictPosition, currentPosition, listTargetPosition.get(i)).length();
                if (targetPath < minPath && targetPath != 0) {
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

    private static String getTheShortestPath2(MapInfo mapInfo, Hero player, List<Position> restrictPosition, List<Position> listTargetPosition) {
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

    private static String bombsDodge(MapInfo mapInfo, Hero player, boolean ownBomb) {
        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        restrictPosition.addAll(mapInfo.getVirusesPosition());

        List<Position> safePositionList = mapInfo.getBlank();
        List<Position> bombsEffPosition = mapInfo.getBombList();
        List<Bomb> realBombs = mapInfo.getBombs();

        if (!safePositionList.isEmpty()) {
            for (int i = 0; i < safePositionList.size(); i++) {
                for (int j = 0; j < bombsEffPosition.size(); j++) {
                    if (safePositionList.get(i).getCol() == bombsEffPosition.get(j).getCol() && safePositionList.get(i).getRow() == bombsEffPosition.get(j).getRow()) {
                        safePositionList.remove(safePositionList.get(i));
                    }
                }
            }
            for (int i = 0; i < safePositionList.size(); i++) {
                for (int j = 0; j < realBombs.size(); j++) {
                    if (safePositionList.get(i).getCol() == realBombs.get(j).getCol() && safePositionList.get(i).getRow() == realBombs.get(j).getRow()) {
                        safePositionList.remove(safePositionList.get(i));
                    }
                }
            }
            if (!ownBomb) {
                for (Position realBomb : realBombs) {
                    restrictPosition.add(realBomb);
                }
            }

            return getTheShortestPath(mapInfo, player, restrictPosition, safePositionList);
        }
        return Dir.INVALID;
    }

    private static boolean isDanger(MapInfo mapInfo, Hero player) {
        Position currentPosition = mapInfo.getCurrentPosition(player);
        List<Position> bombsEffPosition = new ArrayList<>();
        bombsEffPosition.addAll(mapInfo.getBombList());
        bombsEffPosition.addAll(mapInfo.getBombs());
        for (int i = 0; i < bombsEffPosition.size(); i++) {
            if (bombsEffPosition.get(i).getCol() == currentPosition.getCol() && bombsEffPosition.get(i).getRow() == currentPosition.getRow()) {
                return true;
            }
        }
        return false;
    }

    private static String getPathToSpoils(MapInfo mapInfo, Hero player) {

        List<Position> restrictPosition = new ArrayList<>();
        restrictPosition.addAll(mapInfo.getWalls());
        restrictPosition.addAll(mapInfo.getBalk());
        restrictPosition.addAll(mapInfo.getTeleportGate());
        restrictPosition.addAll(mapInfo.getBombList());
        restrictPosition.addAll(mapInfo.getVirusesPosition());

        List<Position> listTargetPosition = new ArrayList<>();
        listTargetPosition.addAll(mapInfo.getSpoils());

        if (!listTargetPosition.isEmpty()) {
            return getTheShortestPath2(mapInfo, player, restrictPosition, listTargetPosition);
        }

        return Dir.INVALID;
    }
}
