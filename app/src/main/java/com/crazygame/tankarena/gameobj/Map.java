package com.crazygame.tankarena.gameobj;

import android.content.Context;

import com.crazygame.tankarena.GameView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Map {
    private final float blockBreath = 100f;
    private final int numBlocksX = 14;
    private final int numBlocksY;
    public final float width = numBlocksX * blockBreath;
    public final float leftBound = -width/2f;
    public final float rightBound = width/2f;
    public final float height;
    private final MapItem[][] items;
    private GameView gameView;

    public Map(GameView gameView, int resourceId) {
        this.gameView = gameView;

        try {
            InputStream inputStream = gameView.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            numBlocksY = Integer.parseInt(tokenizer.nextToken());
            height = numBlocksY * blockBreath;

            items = new MapItem[numBlocksY][numBlocksX];

        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
