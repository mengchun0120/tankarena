package com.crazygame.tankarena.gameobj;

public class MapItem {
    public GameObject gameObject;
    public MapItem next;

    public MapItem(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void reset(GameObject gameObject) {
        this.gameObject = gameObject;
        next = null;
    }
}
