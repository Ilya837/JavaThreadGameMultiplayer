package com.game.javathreadgamemultiplayer2;

public class Msg {
    Actions action;

    public Msg(Actions a){action = a;}


    @Override
    public String toString() {
        return "Msg{" + "Action=" + action + '}';
    }

}
