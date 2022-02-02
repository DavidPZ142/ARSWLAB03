/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

import java.util.ArrayList;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread{
    private Integer inicio;
    private Integer fin;

    public CountThread(Integer start, Integer end){
        this.inicio = start;
        this.fin = end;
    }
    public void run(){

        for(int i = inicio ; i<= fin ; i++ ){


            System.out.println(i);
        }
    }


}
