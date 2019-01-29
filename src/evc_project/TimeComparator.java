/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evc_project;

import java.util.Comparator;

/**
 *
 * @author Arturo Cardone - arturocardone35@gmail.com
 */
public class TimeComparator implements Comparator<SimulatedEvent>{
    
    @Override
    public int compare(SimulatedEvent event1, SimulatedEvent event2) 
    {
        /*if (event1.MyMSTimestamp < event2.MyMSTimestamp) 
            return 1; 
        else if (event1.MyMSTimestamp > event2.MyMSTimestamp) 
            return -1; 
        return 0; */
        return event1.MyMSTimestamp - event2.MyMSTimestamp;
    } 
    
}
