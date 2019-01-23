package com.crazygame.tankarena.gameobj;

public class Pool {
    public static final ObjectPool<MapItem> mapItemPool =
            new ObjectPool<>(MapItem.class, 2000);
    public static final ObjectPool<MapItemToDelete> mapItemToDeletePool =
            new ObjectPool<>(MapItemToDelete.class, 200);
    public static final ObjectPool<Bullet> bulletPool =
            new ObjectPool<>(Bullet.class, 400);
    public static final ObjectPool<Explosion> explosionPool =
            new ObjectPool<>(Explosion.class, 400);
}
