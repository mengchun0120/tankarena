package com.crazygame.tankarena.gameobj;

public class MapItemPool {
    private final int maxSize;
    private int count = 0;
    private MapItem firstAvailable;

    public MapItemPool(int maxSize) {
        firstAvailable = null;
        this.maxSize = maxSize;
    }

    public MapItem alloc(GameObject gameObject) {
        if(firstAvailable != null) {
            MapItem item = firstAvailable;
            firstAvailable = firstAvailable.next;
            item.reset(gameObject);
            --count;
            return item;
        }

        return new MapItem(gameObject);
    }

    public void free(MapItem item) {
        item.reset(null);

        if(count >= maxSize) {
            return;
        }

        item.next = firstAvailable;
        firstAvailable = item;
        ++count;
    }
}
