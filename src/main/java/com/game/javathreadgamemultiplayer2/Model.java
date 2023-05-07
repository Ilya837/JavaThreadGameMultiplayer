package com.game.javathreadgamemultiplayer2;

import java.util.ArrayList;

public class Model {

    int[] CircleX = new int[2];
    int[] CircleY = new int[2];

    int[] CircleR = new int[2];

    int[] ArrowX = new int[4];
    int[] ArrowY = new int[4];

    int[] Score = new int[4];

    int[] ShotCount = new int[4];

    ArrayList<Integer> PlayersId = new ArrayList<Integer>();

    String[] names = new String[4];

    int readyCount = 0;
    int playersCount = 0;

    int WinScore = 1;

    int IdPause;
    boolean pause = false;

    boolean stop = false;

    boolean[] isFly = new boolean[4];

    public Model(){
        CircleX[0] = 360;
        CircleX[1] = 450;

        CircleY[0] = 250;
        CircleY[1] = 250;

        CircleR[0] = 30;
        CircleR[1] = 15;

        WinScore = 1;

        for(int i = 0; i< 4;i++){
            Score[i] = 0;
            ShotCount[i] = 0;
            names[i] = "Player " + (i+1);
        }


    }

    ArrayList<IObserver> allObservers = new ArrayList<>();

    void ref()
    {
        for (IObserver o : allObservers) {
            o.ref();
        }
    }

    Model Tclone(){
        Model clone = new Model();
        for(int i = 0; i< 2;i++) {
            clone.CircleX[i] = CircleX[i];
            clone.CircleY[i] = CircleY[i];
            clone.CircleR[i] = CircleR[i];
        }

        for(int i = 0; i< 4; i++){
            clone.ArrowX[i] = ArrowX[i];
            clone.ArrowY[i] = ArrowY[i];
            clone.Score[i] = Score[i];
            clone.ShotCount[i] = ShotCount[i];
            clone.names[i] = names[i];
            clone.readyCount = readyCount;
            clone.pause = pause;
            clone.isFly[i] = isFly[i];
            clone.PlayersId = new ArrayList<>(PlayersId);
        }
        clone.pause = pause;
        clone.stop = stop;
        clone.playersCount = playersCount;
        clone.WinScore = WinScore;

        return clone;
    }

    public  void addObserver(IObserver o)
    {
        allObservers.add(o);
    }

    public void initArrow(int playersCount){

        int space =(500 - 25 * playersCount) / (playersCount + 1);
        this.playersCount = playersCount;
        for(int i = 0 ; i< playersCount;i++){
            ArrowX[i] = 35;
            ArrowY[i] = space * (i+1) + i * 25;

        }
    }

    void ChangePause(){
        pause = !pause;
        ref();
    }

    void ChangeReady(){
        readyCount++;

    }

    synchronized void  set(Model mod){
        for(int i = 0; i< 2;i++) {
            CircleX[i] = mod.CircleX[i];
            CircleY[i] = mod.CircleY[i];
            CircleR[i] = mod.CircleR[i];
        }

        for(int i = 0; i< 4; i++){
            ArrowX[i] = mod.ArrowX[i];
            ArrowY[i] = mod.ArrowY[i];
            Score[i] = mod.Score[i];
            ShotCount[i] = mod.ShotCount[i];
        }

        playersCount = mod.playersCount;
        PlayersId = new ArrayList<>(mod.PlayersId);
        stop = mod.stop;
        pause = mod.pause;
        WinScore = mod.WinScore;

        ref();
    }
}
