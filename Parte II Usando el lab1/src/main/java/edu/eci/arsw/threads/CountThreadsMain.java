/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

import java.lang.Thread;
import edu.eci.arsw.threads.CountThread;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
    	
    	CountThread co1 = new CountThread(0, 99);
    	CountThread co2 = new CountThread(100, 199);
    	CountThread co3 = new CountThread(200, 299);
    	
    	//co1.start();
    	//co2.start();
    	//co3.start();
    	
    	co1.run();
    	co2.run();
    	co3.run();
    	
    }
    
}