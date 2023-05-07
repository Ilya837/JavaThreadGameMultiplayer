package com.game.javathreadgamemultiplayer2;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientAtServer implements Runnable {

    Socket cs;
    MainServer ms;

    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;

    int Id;

    Gson gson = new Gson();

    Model m = new Model();

    public ClientAtServer(Socket cs, MainServer ms, int id) {
        this.cs = cs;
        this.ms = ms;
        Id = id;
        m =ms.m;

        try {
            os = cs.getOutputStream();
            dos = new DataOutputStream(os);

            String s = gson.toJson(Id);
            dos.writeUTF(s);

        } catch (IOException ex) {
            Logger.getLogger(ClientAtServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendModel(){
        try {
            Model mod = m.Tclone();

            String s = gson.toJson(mod);

            dos.writeUTF(s);
        } catch (IOException ex) {
            Logger.getLogger(ClientAtServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            is = cs.getInputStream();
            dis = new DataInputStream(is);

            System.out.println("Cilent thread started");

            while(true)
            {
                String s = dis.readUTF();
                System.out.println("Msg: " + s);

                Msg msg = gson.fromJson(s, Msg.class);

                switch (msg.action){
                    case ARROW ->{if(!m.pause) ms.shot(Id);}

                    case PAUSE -> ms.ChangePause(Id);

                    case DISCONNECT -> {
                        ms.rmClient(this); break;
                    }

                    case READY -> {
                        if(!ms.gameStart) {
                            m.ChangeReady();
                            m.PlayersId.add(Id);

                            if ((m.readyCount == ms.getClientCount() && m.readyCount > 0) || m.readyCount == 4) {

                                System.out.println("GameStart");

                                m.initArrow(m.readyCount);

                                ms.gameStart = true;
                                System.out.println();
                                ms.StartGameProcess();
                            }
                        }
                    }
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(ClientAtServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setModel(Model mod){
        m = mod;
    }
}
