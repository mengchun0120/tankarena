package com.crazygame.tankarena.gameobj;

public class MapItem {
    public GameObject gameObject = null;
    public MapItem prev = null, next = null;

    public void reset() {
        gameObject = null;
        prev = next = null;
    }
}
