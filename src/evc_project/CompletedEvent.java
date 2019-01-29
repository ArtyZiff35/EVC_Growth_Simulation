
package evc_project;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Arturo Cardone - arturocardone35@gmail.com
 */
public class CompletedEvent {
    
    BigDecimal MyLogValue;
    int[] MyVectorClock;
    int MyProcessID;
    BigInteger MyEVC;
    int myRealTimestamp;
    
    public CompletedEvent(BigDecimal logValue, int[] vectorClock, int processID, BigInteger EVC, int realTime)
    {
        MyLogValue = new BigDecimal(logValue.toString());
        
        MyVectorClock = new int[vectorClock.length];
        for(int i=0; i<vectorClock.length; i++)
            MyVectorClock[i] = vectorClock[i];
        
        MyProcessID = processID;
        
        MyEVC = EVC;
        
        myRealTimestamp = realTime;
        
    }
    
    
    
}
