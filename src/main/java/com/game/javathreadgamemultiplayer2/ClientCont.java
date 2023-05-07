package com.game.javathreadgamemultiplayer2;

import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientCont implements IObserver {



    Model m = new Model();
    Gson gson = new Gson();

    Socket socketAtClient;


    int port = 3124;
    InetAddress ip = null;

    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    ArrayList<Arrow> ArrowList = new ArrayList<>();
    @FXML
    Pane MainPane;

    @FXML
    Button ConnectB;

    @FXML
    Button ReadyB;

    @FXML
    Button ShotB;

    @FXML
    Button PauseB;

    @FXML
    Pane PlayersPane;

    @FXML
    Circle bigCircle;

    @FXML
    Circle littleCircle;

    @FXML
    Polygon Arrow;

    @FXML
    Label hitCount;

    @FXML
    Label arrowCount;

    @FXML
    Label PlayerNum;


    boolean firstRef = true;
    ArrayList<ScorePane> scorePanes = new ArrayList<>();

    int Id;

    public ClientCont(){

        m.addObserver(this);

    }


    void CloseWindow() {
        if(dos != null) {
            Msg msg = new Msg(Actions.DISCONNECT);
            send(msg);
        }

    }

    void send(Msg msg){
        try {
            String s_msg = gson.toJson(msg);
            dos.writeUTF(s_msg);
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void StopGame(){
        int win = 0;
        for(int i =0; i<4;i++){
            if(m.Score[i] >= m.WinScore){
                win = i;
                break;
            }
        }

        firstRef = true;


        int finalWin = win;
        Platform.runLater(()->{
            if(m.PlayersId.indexOf(Id) == finalWin)
                PlayerNum.setText("You win");
            else{
                PlayerNum.setText("win Player" + (finalWin+1));
            }

            ArrowList.forEach(arrow -> {
                MainPane.getChildren().remove(arrow);

            });

            ArrowList.clear();

            scorePanes.forEach(sp -> {
                PlayersPane.getChildren().remove(sp);

            });

            scorePanes.clear();

            ShotB.setDisable(true);
            PauseB.setDisable(true);
            ReadyB.setDisable(false);

            bigCircle.setLayoutY(250.0);
            littleCircle.setLayoutY(250.0);

        });


    }
    @Override
    synchronized public void ref() {

        if(firstRef){

            firstRef = false;
            Platform.runLater(() -> {
                for (int i = 0; i < m.playersCount; i++) {
                    Arrow ar = new Arrow(m.ArrowX[i],m.ArrowY[i]);
                    ArrowList.add(ar);
                    MainPane.getChildren().add(ar);


                    ScorePane sp = new ScorePane("Player " + (i+1), 0,0, 10, m.ArrowY[i] - 30);
                    scorePanes.add(sp);
                    PlayersPane.getChildren().add(sp);

                }

                if(m.PlayersId.contains(Id)){

                    ShotB.setDisable(false);
                    PauseB.setDisable(false);
                    PlayerNum.setText("You Player " + (m.PlayersId.indexOf(Id) + 1));
                }
                else{
                    ReadyB.setDisable(true);
                    PlayerNum.setText("You not a player");
                }

            });

        } else {
            if(m.stop) StopGame();
            else
                for (int i = 0; i < m.playersCount; i++) {
                    ArrowList.get(i).setLayoutX(m.ArrowX[i]);
                    scorePanes.get(i).update(m.Score[i], m.ShotCount[i]);
                }

        }

        if(!m.stop) {
            Platform.runLater(() -> {
                bigCircle.setLayoutY(m.CircleY[0]);
                littleCircle.setLayoutY(m.CircleY[1]);

            });
        }
    }

    @FXML
    void onClickPauseButton(){
        Msg msg = new Msg(Actions.PAUSE);
        send(msg);
    }

    @FXML
    void onClickReadyButton(){
        ReadyB.setDisable(true);
        Msg msg = new Msg(Actions.READY);
        send(msg);
    }

    @FXML
    void OnClickShotButton(){
        Msg msg = new Msg(Actions.ARROW);
        send(msg);
    }

    @FXML
    void onClickConnectButton(){
        ConnectB.setDisable(true);
        try {
            ip = InetAddress.getLocalHost();

            socketAtClient = new Socket(ip, port);


            os = socketAtClient.getOutputStream();
            dos = new DataOutputStream(os);
            new Thread(
                    ()->
                    {
                        try {
                            is = socketAtClient.getInputStream();
                            dis = new DataInputStream(is);

                            boolean firstconnect = true;


                            while (true) {
                                String s = dis.readUTF();
                                System.out.println("Res: " + s);
                                try {
                                    if(firstconnect){
                                        firstconnect = false;
                                        Id = gson.fromJson(s, int.class);
                                    }
                                    else {
                                        Model mod = gson.fromJson(s, Model.class);
                                        m.set(mod);
                                    }
                                }catch (JsonSyntaxException ex){};
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
            ).start();

            Platform.runLater(() ->{
                ReadyB.setDisable(false);
            });


        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
