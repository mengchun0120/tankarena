package com.crazygame.tankarena.gameobj;

public class MapRegion {
    public final static MapRegion[] regionPool = {
            new MapRegion(), new MapRegion()
    };

    public int startRow;
    public int endRow;
    public int startCol;
    public int endCol;

    public void update(Map map, float left, float right, float top, float bottom) {
        startRow = map.crampRow(map.getRow(bottom));
        endRow = map.crampRow(map.getRow(top));
        startCol = map.crampCol(map.getCol(left));
        endCol = map.crampCol(map.getCol(right));
    }

    public boolean covered(int row, int col) {
        return row >= startRow && row <= endRow && col >= startCol && col <= endCol;
    }
}
