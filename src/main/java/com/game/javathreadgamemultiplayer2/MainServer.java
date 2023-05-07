package com.game.javathreadgamemultiplayer2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainServer {

    Model m = new Model();
    boolean gameStart = false;



    int port = 3124;
    InetAddress ip = null;

    ExecutorService service = Executors.newCachedThreadPool();

    ArrayList<ClientAtServer> allClients = new ArrayList<>();

    Thread[] circleMove = new Thread[2];



    Thread bcastThread;

    void CircleMove(int speed, int circleId,int n){


        circleMove[n] =new Thread(() -> {

            boolean down = false;


            while (!m.stop) {

                if (m.pause) {
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println("Circle");

                if (down) {
                    m.CircleY[circleId] += speed;
                } else {
                    m.CircleY[circleId] -= speed;
                }


                if (m.CircleY[circleId] > 500) {
                    down = false;
                }

                if (m.CircleY[circleId] < 0) {
                    down = true;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }

            }
        });


    }


    void StartGameProcess() {



       CircleMove(2, 0,0);
        CircleMove(3, 1,1);


        bcastThread =new Thread(() -> {
            while (!m.stop) {
                try {
                    synchronized (this) {
                        if (m.pause) this.wait();
                    }
                    //System.out.println("bcast");
                    bcast();
                    Thread.sleep(20);
                } catch (InterruptedException e) {

                }
            }

        });

        bcastThread.start();
        circleMove[0].start();
        circleMove[1].start();
    }

    void stopGame(){
        m.stop = true;
        bcastThread.stop();
        circleMove[0].stop();
        circleMove[1].stop();
        bcast();

        int playersCount = m.playersCount;
        m = new Model();
        m.playersCount = playersCount;
        gameStart = false;

        for(ClientAtServer client : allClients) client.setModel(m);
    }

    double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }
    synchronized boolean  CheckShot(double pointX, double pointY,int Id){
        if(!m.stop) {
            if (distance(pointX, pointY, m.CircleX[1], m.CircleY[1]) <= m.CircleR[1]) {
                m.Score[Id] += 2;
                if (m.Score[Id] >= m.WinScore){ m.stop = true; stopGame();}
                return true;
            }

            if (distance(pointX, pointY, m.CircleX[0], m.CircleY[0]) <= m.CircleR[0]) {
                m.Score[Id] += 1;
                if (m.Score[Id] >= m.WinScore){ m.stop = true; stopGame();}
                return true;
            }
        }
        return false;
    }

    synchronized void  ChangePause(int id) throws InterruptedException {
        if(id == m.IdPause || !m.pause){

            if(!m.pause) {
                m.IdPause = id;
                m.ChangePause();
                bcast();
            }
            else{
                m.ChangePause();
                notifyAll();
            }


        }
    }

    protected  void shot(int id){
        if(m.PlayersId.contains(id)) {
            int index = m.PlayersId.indexOf(id);
            new Thread(() -> {
                if (!m.isFly[index]) {
                    m.isFly[index] = true;
                    int speed = 2;

                    m.ShotCount[index] += 1;

                    while (!m.stop) {

                        if(m.pause) {
                            try {
                                synchronized (this) {
                                    wait();
                                }
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        m.ArrowX[index] += speed;

                        if (m.ArrowX[index] > 470) break;

                        if (CheckShot(m.ArrowX[index] + 30, m.ArrowY[index] + 15, index)) break;

                        try {
                            //bcast();
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            m.ShotCount[index] -= 1;
                            break;
                        }
                    }

                    m.ArrowX[index] = 35;

                    m.isFly[index] = false;
                }
            }).start();
        }

    }

    public void ServerStart(){
        ServerSocket ss;
        try {
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 0, ip);
            System.out.append("Server start\n");

            int nextId = 0;
            while(true)
            {
                Socket cs;
                cs = ss.accept();
                System.out.append("Client connect \n");

                ClientAtServer c = new ClientAtServer(cs,this, nextId++);


                allClients.add(c);
                service.submit(c);
                if(m.pause) bcast();
            }

        } catch (IOException ex) {
        }
    }

    void bcast(){
        for(ClientAtServer client : allClients) client.sendModel();
    }
    public int getClientCount(){
        return allClients.size();
    }

    public void rmClient(ClientAtServer client) throws InterruptedException {
        if(client.Id == m.IdPause && m.pause) ChangePause(client.Id);

        allClients.remove(client);

        AtomicBoolean stop = new AtomicBoolean(true);

        allClients.forEach(c -> {if(m.PlayersId.contains( c.Id)) stop.set(false);});

        if(stop.get()) stopGame();

    }
    public static void main(String[] args) {
        new MainServer().ServerStart();
    }
}
