package com.crazygame.tankarena.gameobj;

public class MapItem {
    public GameObject gameObject;
    public MapItem next, prev;

    public MapItem(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void reset(GameObject gameObject) {
        this.gameObject = gameObject;
        prev = next = null;
    }
}
