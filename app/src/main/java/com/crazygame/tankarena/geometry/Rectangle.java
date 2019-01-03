package com.crazygame.tankarena.geometry;

public class Rectangle extends Polygon {
    public final float width, height;

    public Rectangle(float width, float height, float centerX, float centerY) {
        super(new float[]{
                centerX + width / 2f, centerY + height / 2f,
                centerX - width / 2f, centerY + height / 2f,
                centerX - width / 2f, centerY - height / 2f,
                centerX + width / 2f, centerY - height / 2f
        }, centerX, centerY);

        this.width = width;
        this.height = height;
    }
}
