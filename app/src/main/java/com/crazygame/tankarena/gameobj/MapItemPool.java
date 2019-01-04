package com.crazygame.tankarena.gameobj;

public class MapItemPool {
    private final int maxSize;
    private int count = 0;
    private MapItem firstAvailable;

    public MapItemPool(int maxSize) {
        this.maxSize = maxSize;
    }

    public MapItem alloc(GameObject gameObject) {
        if(firstAvailable != null) {
            MapItem item = firstAvailable;
            item.reset(gameObject);
            firstAvailable = firstAvailable.next;
            --count;
            return item;
        }

        return new MapItem(gameObject);
    }

    public void free(MapItem item) {
        if(count >= maxSize) {
            return;
        }

        item.reset(null);
        item.next = firstAvailable;
        firstAvailable = item;
        ++count;
    }
}
