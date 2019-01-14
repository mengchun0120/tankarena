package com.crazygame.tankarena.gameobj;

public class MapItemToDeletePool {
    private MapItemToDelete firstAvailable;
    private int maxSize, count;

    public MapItemToDeletePool(int maxSize) {
        this.maxSize = maxSize;
        count = 0;
        firstAvailable = null;
    }

    public MapItemToDelete alloc(MapItem item, int row, int col) {
        if(firstAvailable != null) {
            MapItemToDelete itemToDelete = firstAvailable;
            firstAvailable = firstAvailable.next;
            itemToDelete.item = item;
            itemToDelete.row = row;
            itemToDelete.col = col;
            itemToDelete.next = null;
            --count;
            return itemToDelete;
        }

        return new MapItemToDelete(item, row, col);
    }

    public void free(MapItemToDelete itemToDelete) {
        itemToDelete.reset();

        if(count >= maxSize) {
            return;
        }

        itemToDelete.next = firstAvailable;
        firstAvailable = itemToDelete;
        ++count;
    }
}
