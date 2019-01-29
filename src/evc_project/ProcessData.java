
package evc_project;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Arturo Cardone - arturocardone35@gmail.com
 */
public class ProcessData {
    
    PriorityQueue<SimulatedEvent> timeQueue;    //Queue that sorts events basing on their ms timestamp
    int MyPrimeNumber;
    BigInteger MyEVC;
    int[] MyVectorClock;
    BigDecimal MyLog;
    
    public ProcessData(int primeNum, int totNumProcesses)
    {
        //Queue of events for this process
        timeQueue = new PriorityQueue<SimulatedEvent>(100, new TimeComparator()); 
        //My prime number
        MyPrimeNumber = primeNum;
        //Initializing my EVC to 1
        MyEVC = BigInteger.valueOf(1);
        //Initializing the Vector Clock to 0
        MyVectorClock = new int[totNumProcesses];
        for(int i=0; i<totNumProcesses; i++)
            MyVectorClock[i] = 0;
        //Initializing the log
        MyLog = new BigDecimal(0);
    }
    
    public void addToEventQueue(SimulatedEvent event)
    {
        timeQueue.add(event);
        //System.out.println(MyPrimeNumber + ") My EVC is " + MyEVC);
    }
    
    public SimulatedEvent getNextEvent()
    {
        return timeQueue.poll();
    }
    
    public boolean isReceiveDue(int timestamp)
    {
        SimulatedEvent nextEvent = timeQueue.peek();
        if(nextEvent!=null && nextEvent.IsReceive==true && nextEvent.MyMSTimestamp<=timestamp)
            return true;
        return false;
    }
    
}
