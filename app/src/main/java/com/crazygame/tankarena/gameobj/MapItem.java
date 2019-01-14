package com.crazygame.tankarena.gameobj;

public class MapItem {
    public GameObject gameObject;
    public MapItem prev, next;
    private static int count = 0;
    public final String idStr;

    public MapItem(GameObject gameObject) {
        idStr = "m" + (count++);
        this.gameObject = gameObject;
        prev = next = null;
    }

    public void reset(GameObject gameObject) {
        this.gameObject = gameObject;
        prev = next = null;
    }
}
