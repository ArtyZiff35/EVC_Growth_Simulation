
package evc_project;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arturo Cardone - arturocardone35@gmail.com
 */
public class main {
    
    static int MANTISSA_SIZE = 32;          //m = size of the mantissa
    static int percentageRounding = 30;     //Percentage to cut for rounding at last
    static int v_parameter = 30;            //v = events per process
    static double ROUNDING = 0.002;
    static int max_events;
    static int send_simulated[], receive_simulated[], internal_simulated[];
    static int totNumEventsExecuted, globalEVCsize;
    static int msTimer;
    static PrintWriter writer;
    static ArrayList<CompletedEvent> completedList;

    public static void main(String[] args) {
       
        int numProcesses;
        int percentageInternalEvents;
        int frequencyRateEvents;
                
        //Checking the number of arguments
        if(args.length != 3)
        {
            System.out.println("Wrong number of arguments");
            return;
        }
        
        //Parsing the arguments
        if(!isNumber(args[0]) || !isNumber(args[1]) || !isNumber(args[2]))
        {
            System.out.println("Arguments are not valid numbers");
            return;
        }
        numProcesses = Integer.parseInt(args[0]);
        percentageInternalEvents = Integer.parseInt(args[1]);
        frequencyRateEvents = Integer.parseInt(args[2]);
        
        //Preparing file to which we write our chart data
        try {
            Date date= new Date();
            long time = date.getTime();
            writer = new PrintWriter("results-" +time+".dat", "UTF-8");
            writer.println("#Execution with " + numProcesses + " processes and " + v_parameter + " v parameter and Mantissa size " + MANTISSA_SIZE + " and rounding " + ROUNDING);
            System.out.println("#Execution with " + numProcesses + " processes and " + v_parameter + " v parameter and Mantissa size " + MANTISSA_SIZE + " and rounding " + ROUNDING);
            writer.println("#v_parameter\t#FN_Rate\t#FP_Rate\n");
        } catch (Exception ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //Setting global variables and Starting the simulation
        //Uncomment for other type of simulation
        
        /*
        for(percentageInternalEvents=0; percentageInternalEvents<100; percentageInternalEvents++)
        {
            send_simulated = new int[numProcesses];
            receive_simulated = new int[numProcesses];
            internal_simulated = new int[numProcesses];
            totNumEventsExecuted = 0;
            globalEVCsize = 0;
            msTimer = 0;
            max_events = v_parameter * numProcesses;
            completedList = new ArrayList<CompletedEvent>();
            simulationRoundRobin(numProcesses, percentageInternalEvents);  
            printData(percentageInternalEvents,totNumEventsExecuted);
            System.out.println("Executed with percentage="+percentageInternalEvents);
        }*/
              
        
        for(v_parameter=5; v_parameter<60; v_parameter=v_parameter+5)
        {
            System.out.println("Execution with v_parameter " + v_parameter);
            send_simulated = new int[numProcesses];
            receive_simulated = new int[numProcesses];
            internal_simulated = new int[numProcesses];
            totNumEventsExecuted = 0;
            globalEVCsize = 0;
            msTimer = 0;
            max_events = v_parameter * numProcesses;
            completedList = new ArrayList<CompletedEvent>();
            simulationRoundRobin(numProcesses, percentageInternalEvents);            

            writeResults(numProcesses);
        }
        
            
        
        writer.close();

    }
    
    
    public static void printData(int percentageInternalEvents, int totNumEventsExecuted)
    {
        writer.println(percentageInternalEvents + "\t" + totNumEventsExecuted);
    }
    
    
   public static void writeResults(int numProcesses)
    {
        boolean eTOf, fTOe;
        boolean eMINUSf_belongsN, fMINUSe_belongsN;
        int truePositive=0, falseNegative=0, trueNegative=0, falsePositive=0;
        float FN_Rate = 0, FP_Rate = 0; 
        int roundingSize = MANTISSA_SIZE - (int)((float)(percentageRounding*MANTISSA_SIZE)/(float)100);
        //System.out.println("Rounding precision is " + roundingSize + " out of original " + MANTISSA_SIZE);
        
        int eTOfcounter = 0, fTOecounter=0;
        int NOTeTOfcounter = 0, NOTfTOecounter=0;
                
        for(int i=0; i<completedList.size(); i++)
        {
            CompletedEvent e = completedList.get(i);
            for(int j=i+1; j<completedList.size(); j++)
            {
                CompletedEvent f = completedList.get(j);
                if(e != f)
                {
                    //Checking causality between e and f
                    //if(e.MyEVC.compareTo(f.MyEVC) < 0  && f.MyEVC.mod(e.MyEVC)==BigInteger.valueOf(0))
                    //if(f.MyEVC.compareTo(e.MyEVC) < 0  && e.MyEVC.mod(f.MyEVC)==BigInteger.valueOf(0))
                    if(e.MyVectorClock[e.MyProcessID] <= f.MyVectorClock[e.MyProcessID])
                    {
                        eTOf = true;
                        eTOfcounter++;
                    }
                    else 
                    {
                        eTOf = false;
                        NOTeTOfcounter++;
                    }
                    if(f.MyVectorClock[f.MyProcessID] <= e.MyVectorClock[f.MyProcessID])
                    {
                        fTOe = true;
                        fTOecounter++;
                    }
                    else
                    {
                        fTOe = false;
                        NOTfTOecounter++;
                    }
                    
                    //Checking if the inverse of the log is in N
                    final int SCALE = 10;
                    //Calculating inverse log of the two differences
                    BigDecimal two = BigDecimal.valueOf(2);
                    BigDecimal eMINUSf = e.MyLogValue.subtract(f.MyLogValue).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
                    BigDecimal fMINUSe = f.MyLogValue.subtract(e.MyLogValue).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
                    BigDecimal invertedLog_eMINUSf = BigFunctions.exp( BigFunctions.ln(two, SCALE).multiply(eMINUSf),SCALE).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
                    BigDecimal invertedLog_fMINUSe = BigFunctions.exp( BigFunctions.ln(two, SCALE).multiply(fMINUSe),SCALE).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
                    //Cropping out the last part of the decimal
                    invertedLog_eMINUSf = invertedLog_eMINUSf.setScale(roundingSize, BigDecimal.ROUND_HALF_UP);
                    BigDecimal fractionalPart = invertedLog_eMINUSf.remainder( BigDecimal.ONE );  //Fractional part of first inverse log
                    //System.out.println(fractionalPart.doubleValue());
                    //if(fractionalPart.doubleValue()==0)
                    if(fractionalPart.compareTo(new BigDecimal(0.00000000000002))<0)
                        eMINUSf_belongsN = true;
                    else 
                        eMINUSf_belongsN = false;
                    ////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    //invertedLog_fMINUSe = invertedLog_fMINUSe.setScale(roundingSize, BigDecimal.ROUND_HALF_UP);
                    //fractionalPart = invertedLog_fMINUSe.remainder( BigDecimal.ONE ).setScale(roundingSize, BigDecimal.ROUND_HALF_UP);    
                    BigDecimal approximatedInt = invertedLog_fMINUSe.setScale(0, RoundingMode.HALF_UP);     //Finding nearest integer
                    BigDecimal difference = (approximatedInt.subtract(invertedLog_fMINUSe)).abs();          //Abs value of Rounded-Original
                    if(difference.compareTo(new BigDecimal(ROUNDING))<0)
                        fMINUSe_belongsN = true;
                    else
                        fMINUSe_belongsN = false;
                    
                    ///////////////////////////////
                    //Now calculating true positive...
                    if( (eTOf && fMINUSe_belongsN) )
                        truePositive++;
                    else if( (eTOf && !fMINUSe_belongsN)  )
                        falseNegative++;
                    else if( (!eTOf && !fMINUSe_belongsN)  )
                        trueNegative++;
                    else if( !eTOf &&   (fMINUSe_belongsN ) )
                        falsePositive++;
                }
            }
            
            //System.out.println(i +" / " + completedList.size());
        }
        
        //Calculating the rates
        FN_Rate = (float)falseNegative/((float)(falseNegative + truePositive));
        FP_Rate = (float)falsePositive/((float)(falsePositive + trueNegative));
        //System.out.println("TP: " + truePositive + " FN: " + falseNegative + " TN: " + trueNegative + " FP: " +falsePositive);
        //System.out.println("FNRATE=" + (FN_Rate) + " FP_RATE="+FP_Rate);
        //System.out.println("We had e->f " +eTOfcounter + "(opposite "+NOTeTOfcounter+")   and f->e " + fTOecounter +"opposite " + NOTfTOecounter);
        writer.println(v_parameter + "\t" + FN_Rate + "\t" + FP_Rate);
    }
    
    public static void simulationRoundRobin(int numProcesses, int percentageInternalEvents)
    {
        List<Integer> primeNumList;
        ProcessData processes[];
        Random randSeed = new Random();
        SimulatedEvent nextEvent;
        
        //Generating prime numbers
        primeNumList = sieveOfEratosthenes(numProcesses);
        //Preparing processes' data structures
        processes = new ProcessData[numProcesses];
        for(int i=0; i<numProcesses; i++)
        {
            //Setting the process' prime number
            processes[i] = new ProcessData(primeNumList.get(i), numProcesses);
        }
        
        //Round Robin execution of the simulation
        while(true)
        {
            //Incrementing global ms timestamp
            msTimer++;      
            for(int i=0; i<numProcesses; i++)
            {
                //Generating a new event and adding it to the queue (only if no receive are queued for this specific time instant)
                if(!processes[i].isReceiveDue(msTimer))
                {
                    nextEvent = generateEvent(randSeed, percentageInternalEvents, numProcesses, i);
                    processes[i].addToEventQueue(nextEvent);
                }
                //Letting the simulation of the process
                singleProcessExecution(processes, i);
                //Checking for termination condition (EVC overflow - EVC of size 32n)
                if(processes[i].MyEVC.bitCount() >=  (32*numProcesses))
                {
                    //System.out.println("Stopped due to max EVC bit count");
                    //printStop(numProcesses);
                    //return;
                }
                if(totNumEventsExecuted >= max_events)
                {
                    System.out.println("MAX EVENTS REACHED " + max_events);
                    return;
                }
                //System.out.println(totNumEventsExecuted + " out of " + max_events);
                //Updating the size of the global EVC
                if(processes[i].MyEVC.bitCount() > globalEVCsize)
                    globalEVCsize = processes[i].MyEVC.bitCount();
                
            }
        }
        
    }
    
    
    //Simulation of the execution of a single process: an event is extracted from the queue and is executed
    public static void singleProcessExecution(ProcessData[] processes, int currentIndex)
    {
        SimulatedEvent nextEvent;
        Random randomSeed = new Random();
        
        //Getting next event to execute
        nextEvent = processes[currentIndex].getNextEvent();
        totNumEventsExecuted++;
        //Understanding which kind of event this is
        if(nextEvent.IsInternal)
        {
            internal_simulated[currentIndex]++;
            //EVC
            //Updating the EVC
            processes[currentIndex].MyEVC = processes[currentIndex].MyEVC.multiply(BigInteger.valueOf(processes[currentIndex].MyPrimeNumber));
            //------------------------------------------
            //VECTOR CLOCK
            //Updating the Vector Clock
            processes[currentIndex].MyVectorClock[currentIndex]++;
            //------------------------------------------
            //LOG
            //Updating the log
            processes[currentIndex].MyLog = processes[currentIndex].MyLog.add(log2(new BigDecimal(processes[currentIndex].MyPrimeNumber)).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP));
            processes[currentIndex].MyLog = new BigDecimal(processes[currentIndex].MyLog.doubleValue()).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
        }
        else if(nextEvent.IsSend)
        {
            send_simulated[currentIndex]++;
            //EVC
            //Updating the EVC
            processes[currentIndex].MyEVC = processes[currentIndex].MyEVC.multiply(BigInteger.valueOf(processes[currentIndex].MyPrimeNumber));
            //------------------------------------------
            //VECTOR CLOCK
            //Updating the Vector Clock
            processes[currentIndex].MyVectorClock[currentIndex]++;
            //------------------------------------------
            //LOG
            //Updating the log
            processes[currentIndex].MyLog = processes[currentIndex].MyLog.add(log2(new BigDecimal(processes[currentIndex].MyPrimeNumber)).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP));
            processes[currentIndex].MyLog = new BigDecimal(processes[currentIndex].MyLog.doubleValue()).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
            
            //Adding the correspondent receive event by piggybacking the current EVC and Vector Clock
            SimulatedEvent recEvent = new SimulatedEvent(false,true,false,(msTimer+getRandomDelay(randomSeed)));
            recEvent.setPiggybackedEVC(processes[currentIndex].MyEVC);
            recEvent.setPiggybackedVectorClock(processes[currentIndex].MyVectorClock);
            recEvent.setPiggybackedLog(processes[currentIndex].MyLog);
            processes[nextEvent.MySendDestination].addToEventQueue(recEvent);
        }
        else if(nextEvent.IsReceive)
        {
            receive_simulated[currentIndex]++;
            //EVC
            //Calculating the LCM
            BigInteger EVC1,EVC2, resultEVC;
            EVC1 = new BigInteger(processes[currentIndex].MyEVC.toString());
            EVC2 = new BigInteger(nextEvent.piggybackedEVC.toString());
            resultEVC = EVC1.multiply(EVC2);
            resultEVC = resultEVC.divide(GCD(EVC1,EVC2));
            //Updating the local EVC
            processes[currentIndex].MyEVC = resultEVC.multiply(BigInteger.valueOf(processes[currentIndex].MyPrimeNumber));
            //------------------------------------------
            //VECTOR CLOCK
            //Updating the vector clock with the max values
            for(int i=0; i<processes.length; i++)
            {
                processes[currentIndex].MyVectorClock[i] = Math.max(processes[currentIndex].MyVectorClock[i], nextEvent.piggybackedVectorClock[i]);
            }
            //Updating the Vector Clock
            processes[currentIndex].MyVectorClock[currentIndex]++;
            //------------------------------------------
            //LOG
            //Updating the log value
            BigDecimal log1 = processes[currentIndex].MyLog;
            BigDecimal log2 = nextEvent.piggybackedLog;
            BigDecimal two = BigDecimal.valueOf(2);
            final int SCALE = 10;
            //Calculating inverse log
            BigDecimal invertedLog1 = BigFunctions.exp( BigFunctions.ln(two, SCALE).multiply(log1),SCALE).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
            BigDecimal invertedLog2 = BigFunctions.exp( BigFunctions.ln(two, SCALE).multiply(log2),SCALE).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
            //Calculating GCD and s+i-log(GCD) 
            BigDecimal GCDresult = log2int(GCD(invertedLog1.toBigInteger(), invertedLog2.toBigInteger())).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
            BigDecimal logResult = log1.add(log2).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
            logResult = logResult.subtract(GCDresult).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);
            processes[currentIndex].MyLog = logResult;
            //Updating the local log
            processes[currentIndex].MyLog = processes[currentIndex].MyLog.add(log2(new BigDecimal(processes[currentIndex].MyPrimeNumber)).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP));
            processes[currentIndex].MyLog = new BigDecimal(processes[currentIndex].MyLog.doubleValue()).setScale(MANTISSA_SIZE, BigDecimal.ROUND_HALF_UP);            
            
        }
        
        completedList.add(new CompletedEvent(processes[currentIndex].MyLog, processes[currentIndex].MyVectorClock, currentIndex, processes[currentIndex].MyEVC, msTimer));
        
    }
    
    public static int getRandomDelay(Random seed)
    {
        int min = 0;
        int max = 10;
        int randomNumber = seed.nextInt(max + 1 - min) + min;
        return randomNumber;
    }
    
    public static BigInteger pow(BigInteger base, BigInteger exponent) 
    {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) 
        {
          if (exponent.testBit(0)) result = result.multiply(base);
          base = base.multiply(base);
          exponent = exponent.shiftRight(1);
        }
        return result;
    }
    
    //This function generates a new Internal or Send Event
    public static SimulatedEvent generateEvent(Random randSeed, int percentageInternalEvents, int numProcesses, int myIndex)
    {
        int Result = randSeed.nextInt(101);
        SimulatedEvent resultEvent;
        
        if(Result <= percentageInternalEvents)
        {
            //Internal event
            resultEvent = new SimulatedEvent(false, false, true, msTimer);
        }
        else
        {
            //Send event
            int dest = myIndex;
            while(dest == myIndex)
                dest = randSeed.nextInt(numProcesses-1);
            resultEvent = new SimulatedEvent(true, false, false, msTimer);
            resultEvent.setSendDestination(dest);
        }
        
        return resultEvent;
    }
    
    //This function uses the Sieve of Eratosthenes method to generate prime numbers in O(nlogn)
    public static List<Integer> sieveOfEratosthenes(int n) 
    {
        int status = 1, num = 3, count, j;
        List<Integer> primeNumbers = new LinkedList<>();
        
        primeNumbers.add(2);
        for (count = 2; count <=n;)
        {
           for (j = 2; j <= Math.sqrt(num); j++)
           {
              if (num%j == 0)
              {
                 status = 0;
                 break;
              }
           }
           if (status != 0)
           {
              primeNumbers.add(num);
              count++;
           }
           status = 1;
           num++;
        }      
        return primeNumbers;
    }
    
    //This function checks if a string is a number
    public static boolean isNumber(String input)
    {
        try  
        {  
          int num = Integer.parseInt(input);  
        }  
        catch(Exception e)  
        {  
          return false;  
        }  
        return true; 
    }
    
    //Euclidean algorithm for the GCD
    public static BigInteger GCD(BigInteger p, BigInteger q) 
    {
        if (q.equals(BigInteger.valueOf(0))) {
            return p;
        }
        return GCD(q, p.mod(q));
    }
    
    //Function that prints out once the termination condition has been reached
    public static void printStop(int numProcesses)
    {
        System.out.println("Termination condition reached (OVERFLOW at 32*" + numProcesses + "bits)");
        //Printing count of send events
        System.out.println("\nSEND Events:\n");
        for(int i=0; i<numProcesses; i++)
            System.out.println(i+") Send: " + send_simulated[i]);
        //Printing count of receive events
        System.out.println("\nRECEIVE Events:\n");
        for(int i=0; i<numProcesses; i++)
            System.out.println(i+") Receive: " + receive_simulated[i]);
        //Printing count of internal events
        System.out.println("\nINTERNAL Events:\n");
        for(int i=0; i<numProcesses; i++)
            System.out.println(i+") Internal: " + internal_simulated[i]);
        
        System.out.println("\nTotal number of events executed: " + totNumEventsExecuted + "\n");
    }
    
    
    static int getNumDigits(int n)
    {
        if (n == 0) return 1;
        int l;
        n=Math.abs(n);
        for (l=0;n>0;++l)
            n/=10;
        return l;           
    }
    
    static BigDecimal log2(BigDecimal value)
    {
        double tmpValue = Math.log(value.doubleValue())/Math.log(2);
        return new BigDecimal(tmpValue);
    }
    
    static BigDecimal log2int(BigInteger value)
    {
        double tmpValue = Math.log(value.doubleValue())/Math.log(2);
        return new BigDecimal(tmpValue);
    }
}
