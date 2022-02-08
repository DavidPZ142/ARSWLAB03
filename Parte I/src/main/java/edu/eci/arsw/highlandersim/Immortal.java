package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;

    private AtomicInteger health;

    private int defaultDamageValue;

    private int push;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private Object lock;

    public boolean pause, running;

    public Immortal(String name, List<Immortal> immortalsPopulation, AtomicInteger health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, int push) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        running = true;
        pause = false;
        this.push = push;

    }

    public void run() {

        while (!ControlFrame.listo) {
            if(!pause){
                Immortal im;

                int myIndex = immortalsPopulation.indexOf(this);

                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                //avoid self-fight
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }

                im = immortalsPopulation.get(nextFighterIndex);

                this.fight(im);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                try{
                    synchronized(ControlFrame.pivote){
                        ControlFrame.pivote.wait();
                    }
                } catch (InterruptedException ex) { }
            }
        }

    }

    public void fight(Immortal i2) {
        Immortal x1,x2;
        if(this.push>i2.push){
            x1 = this;
            x2 = i2;
        }
        else{
            x1 = i2;
            x2 = this;
        }
        synchronized(x1){
            synchronized(x2){
                if (i2.getHealth().get() > 0) {
                    i2.changeHealth(i2.getHealth().get() - defaultDamageValue);
                    this.health.addAndGet(defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health = new AtomicInteger(v);
    }

    public AtomicInteger getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    public void setlock(Object b){
        lock = b;
    }

    public void setPause(boolean pause){
        this.pause = pause;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public void bloquear(){
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void desbloquear(){
        synchronized(lock){
            lock.notify();
        }
        pause = false;
    }

    public void borrar(){
        if(!running){
            for(Immortal im: immortalsPopulation){
                im = null;
            }
            immortalsPopulation.clear();
        }
    }


}