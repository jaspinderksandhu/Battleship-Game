package cu.su.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerEntry implements Serializable {
    private int score;
    private int turnTime;
    private Map<Integer, List<CellEntry>> cellEntryMap;
    private Map<Integer, Boolean> shotList;

    public PlayerEntry(Object cellEntries) {
        score = 0;
        turnTime = 0;
        this.cellEntryMap = (Map<Integer, List<CellEntry>>) cellEntries;
        shotList = new HashMap<>();
    }

    public int getPlayTip() {
        if (cellEntryMap != null) {
            for (List<CellEntry> list : cellEntryMap.values()) {
                if (list != null) {
                    for (CellEntry cellEntry : list) {
                        return cellEntry.getIndex();
                    }
                }
            }
        }
        return -1;
    }

    public Map<Integer, Boolean> getShotList() {
        return shotList;
    }

    public Map<Integer, List<CellEntry>> getCellEntryMap() {
        return cellEntryMap;
    }

    public void setCellEntryMap(Map<Integer, List<CellEntry>> cellEntryMap) {
        this.cellEntryMap = cellEntryMap;
    }

    public boolean shot(int index) {

        boolean isShip = destoryShip(index);
        shotList.put(index, isShip);
        return isShip;
    }

    public boolean tryShot(int index) {
        return shotList.containsKey(index);
    }

    public boolean destoryShip(int index) {
        for (Map.Entry<Integer, List<CellEntry>> entry : cellEntryMap.entrySet()) {
            for (CellEntry cellEntry : entry.getValue()) {
                if (cellEntry.getIndex() == index) {
                    entry.getValue().remove(cellEntry);
                    if (entry.getValue().size() == 0) {
                        cellEntryMap.remove(entry.getKey());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public int getShipNumber() {
        return cellEntryMap.size();
    }


    public void updateTurnTime(int turnTime) {
        this.turnTime += turnTime;
    }


    public void calcScore(int wholeTime) {
        score = (int) (shotList.size() * 10 * (1 - turnTime / (double) wholeTime));
    }

    public int getScore() {
        return score;
    }
}
