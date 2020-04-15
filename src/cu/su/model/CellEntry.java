package cu.su.model;

import java.io.Serializable;

public class CellEntry implements Serializable {
    private int index;
    private int shipIndex;

    public CellEntry(int index, int shipIndex) {
        this.index = index;
        this.shipIndex = shipIndex;
    }

    @Override
    public String toString() {
        return String.format("index: %d and shipIndex: %d", index, shipIndex);
    }

    public int getIndex() {
        return index;
    }

    public int getShipIndex() {
        return shipIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CellEntry)) {
            return false;
        }
        return index == ((CellEntry) obj).index && shipIndex == ((CellEntry) obj).shipIndex;
    }
}
