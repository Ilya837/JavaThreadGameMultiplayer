package com.game.javathreadgamemultiplayer2;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ScorePane extends Pane {

    Label Name;
    Label ScoreL;
    Label ShotL;




    ScorePane(String name, int score,int shot, int x, int y){
        this.setLayoutX(x);
        this.setLayoutY(y);

        //this.setBorder(Border.stroke(Color.BLACK));


        Name = new Label(name);
        Name.setLayoutX(25);
        Name.setLayoutY(15);

        ScoreL = new Label("Очков : " + score);
        ScoreL.setLayoutY(35);
        ScoreL.setLayoutX(10);

        ShotL = new Label("Выстрелов : " + shot);
        ShotL.setLayoutY(55);
        ShotL.setLayoutX(10);

        this.getChildren().add(Name);
        this.getChildren().add(ScoreL);
        this.getChildren().add(ShotL);



        //Score.setText("" + score);
    }

    void update(int score, int shot){
        Platform.runLater(()->{
        ScoreL.setText("Очков : " + score);
        ShotL.setText("Выстрелов : " + shot);
        });
    }

}
