/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    public Object pivote ;
    private AtomicInteger a = new AtomicInteger(0);
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */

    
    public List<Integer> checkHost(String ipaddress, int n) throws InterruptedException{
        pivote = new Object();
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        int ocurrencesCount=0;
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int div = skds.getRegisteredServersCount()/n;
        int ini = 0;
        int fin = 0;
        ArrayList<HostBlackListThread> lth= new ArrayList<HostBlackListThread>();
        
        for(int i=0;i<n;i++) {
        	ini = div*i;
        	fin = ini + div;
        	HostBlackListThread th = new HostBlackListThread(ini, fin, ipaddress,pivote,BLACK_LIST_ALARM_COUNT);
        	th.start();
        	lth.add(th);
            try{
                th.join(10);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        

        
        for(HostBlackListThread hbth:lth) {
            a.addAndGet(1);
            hbth.join();
        	ocurrencesCount = ocurrencesCount + hbth.ocurrences();
        	blackListOcurrences.addAll(hbth.getBlackListOcurrences());
        	if (ocurrencesCount >= 5  ){
                //System.out.println("Ya no es seguro XD");
                HostBlackListThread.setParar();
                break;

            }
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);

        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{a,skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
}