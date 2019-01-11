package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.data.Constants;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Bullet extends GameObject {
    public final static BulletTemplate template = new BulletTemplate();

    public final int side;
    public final float[] direction = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    public Bullet(int side, float x, float y, float directionX, float directionY) {
        this.side = side;
        position[0] = x;
        position[1] = y;
        direction[0] = directionX;
        direction[1] = directionY;
    }

    public void update(Map map, float timeDelta) {
        final float moveDistance = template.speed[side] * timeDelta;
        final int oldBottomRow = map.crampRow(map.getRow(bottomBound()));
        final int oldTopRow = map.crampRow(map.getRow(topBound()));
        final int oldLeftCol = map.crampCol(map.getCol(leftBound()));
        final int oldRightCol = map.crampCol(map.getCol(rightBound()));

        position[0] += moveDistance * direction[0];
        position[1] += moveDistance * direction[1];

        final int newBottomRow = map.crampRow(map.getRow(bottomBound()));
        final int newTopRow = map.crampRow(map.getRow(topBound()));
        final int newLeftCol = map.crampCol(map.getCol(leftBound()));
        final int newRightCol = map.crampCol(map.getCol(rightBound()));

        map.move(this, oldBottomRow, oldTopRow, oldLeftCol, oldRightCol, newBottomRow,
                newTopRow, newLeftCol, newRightCol);

        if(outofBound(map) || checkCollision(map)) {
            flag |= FLAG_DELETED;
        }

        flag |= FLAG_UPDATED;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        template.draw(simpleShaderProgram, side, position);
        flag |= FLAG_DRAWN;
    }

    @Override
    public float leftBound() {
        return position[0] - template.radius;
    }

    @Override
    public float rightBound() {
        return position[0] + template.radius;
    }

    @Override
    public float topBound() {
        return position[1] + template.radius;
    }

    @Override
    public float bottomBound() {
        return position[1] - template.radius;
    }

    @Override
    public float leftCollisionBound() {
        return position[0] - template.collisionRadius;
    }

    @Override
    public float rightCollisionBound() {
        return position[0] + template.collisionRadius;
    }

    @Override
    public float topCollisionBound() {
        return position[1] + template.collisionRadius;
    }

    @Override
    public float bottomCollisionBound() {
        return position[1] - template.collisionRadius;
    }

    private boolean outofBound(Map map) {
        return position[0] <= -template.radius || position[0] >= map.width + template.radius ||
               position[1] <= -template.radius || position[1] >= map.height + template.radius;
    }

    private boolean checkCollision(Map map) {
        boolean collide = false;
        int bottomRow = map.crampRow(map.getRow(bottomCollisionBound()));
        int topRow = map.crampRow(map.getRow(topCollisionBound()));
        int leftCol = map.crampCol(map.getCol(leftCollisionBound()));
        int rightCol = map.crampCol(map.getCol(rightCollisionBound()));

        map.clearFlags(bottomRow, topRow, leftCol, rightCol, ~GameObject.FLAG_CHECKED);
        for(int row = bottomRow; row <= topRow; ++row) {
            for(int col = leftCol; col <= rightCol; ++col) {
                for(MapItem item = map.items[row][col]; item != null; item = item.next) {
                    GameObject obj = item.gameObject;

                    if((obj.flag & FLAG_CHECKED) != 0 || obj == this) {
                        continue;
                    }

                    if((obj instanceof Tank) || (obj instanceof Tile)) {
                        if(collideWith(obj)) {
                            collide = true;
                        }
                    }

                    obj.flag |= GameObject.FLAG_CHECKED;
                }
            }
        }

        return collide;
    }

    private boolean collideWith(GameObject obj) {
        return (obj.rightCollisionBound() - leftCollisionBound()) > 0 &&
               (rightCollisionBound() - obj.leftCollisionBound()) > 0 &&
               (obj.topCollisionBound() - bottomCollisionBound()) > 0 &&
               (topCollisionBound() - obj.bottomCollisionBound()) > 0;
    }
}
