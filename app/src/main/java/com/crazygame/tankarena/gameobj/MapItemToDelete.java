package com.crazygame.tankarena.gameobj;

public class MapItemToDelete {
    public MapItem item;
    public int row, col;
    public MapItemToDelete next;

    public MapItemToDelete(MapItem item, int row, int col) {
        this.item = item;
        this.row = row;
        this.col = col;
        next = null;
    }

    public void reset() {
        item = null;
        row = col = -1;
        next = null;
    }
}
