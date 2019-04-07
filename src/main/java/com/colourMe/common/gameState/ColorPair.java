package com.colourMe.common.gameState;

import javafx.scene.paint.Color;

public class ColorPair {
    public static ColorPair[] COLOR_PAIRS = {
            new ColorPair(Color.BLUE, -16776961),
            new ColorPair(Color.RED, -65536),
            new ColorPair(Color.GREEN, -16744448),
            new ColorPair(Color.BLACK, -16777216)
    };

    public Color COLOR;
    public int COLOR_CODE;

    public ColorPair(Color color, int colorCode) {
        this.COLOR = color;
        this.COLOR_CODE = colorCode;
    }
}


