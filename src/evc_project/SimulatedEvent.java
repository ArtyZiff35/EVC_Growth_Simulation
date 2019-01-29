
package evc_project;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Arturo Cardone - arturocardone35@gmail.com
 */
public class SimulatedEvent {
    
    boolean IsSend, IsReceive, IsInternal;
    int MySendDestination;
    int MyMSTimestamp;
    BigInteger piggybackedEVC;
    int[] piggybackedVectorClock;
    BigDecimal piggybackedLog;
    
    public SimulatedEvent(boolean isSend, boolean isReceive, boolean isInternal, int msTimestamp)
    {
        IsSend = isSend;
        IsReceive = isReceive;
        IsInternal = isInternal;
        MyMSTimestamp = msTimestamp;
    }
    
    public void setSendDestination(int dest)
    {
        MySendDestination = dest;
    }
    
    public void setPiggybackedEVC(BigInteger piggyEVC)
    {
        piggybackedEVC = new BigInteger(piggyEVC.toString());
    }
    
    public void setPiggybackedVectorClock(int[] vectorClock)
    {
        piggybackedVectorClock = new int[vectorClock.length];
        for(int i=0; i<vectorClock.length; i++)
            piggybackedVectorClock[i] = vectorClock[i];
    }
    
    public void setPiggybackedLog(BigDecimal log)
    {
        piggybackedLog = log;
    }
    
}
