package com.game.javathreadgamemultiplayer2;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

public class Arrow extends Polygon {

    Arrow(float x, float y){
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.getPoints().addAll(0.0,13.0,20.0,13.0,20.0,5.0,30.0,15.0,20.0,25.0,20.0,17.0,0.0,17.0);
        this.setFill(Color.BLACK);
    }
}
