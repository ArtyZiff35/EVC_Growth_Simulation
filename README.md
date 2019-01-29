# EVC Growth Rate Simulation

The following is an implementation of the paper by prof. Ajay D Kshemkalyani aimed at simulating the Encoded Vector Clock Growth rate.

The vector clock is a fundamental tool to characterize causality
in distributed executions. Each process needs to maintain
a vector of size n, where n is the total number of processes in the
system, to represent the local vector clock. Unfortunately, this does
not scale well to large systems. Several works in the literature
attempted to reduce the size of vector clocks, but
they had to make some compromises in accuracy or alter the system
model, and in the worst-case, were as lengthy as vector clocks. To
address this problem, we propose the encoding of the vector clock (EVC)
using prime numbers to use a single number to represent vector
time.

A goal of the project is to identify how fast the EVC grows, as a function of the number of events executed by a process, and the number of events executed by all the processes collectively. n is an input parameter. With n processes and assuming 32-bit integer, how many events it takes for the size of the EVC to occupy a number equal to 32n bits long. Once it equals 32n bits long, we can do a system-wide EVC reset.

## Implementation
One way to measure this is to simulate asynchronous message-passing among n processes. Generate the first n prime numbers in Primes[1,n] and assign one to each process being simulated. Each process is simulated by a thread and the events of each process (internal, send, and receive events) are also simulated. A process generates internal and send events with a certain probability (a controllable parameter, which can also disallow internal events) and at a certain frequency (rate), say 1 event/ms. The events get queued in the process queue Q_i along with the simulation time timestamp, which is processed by the simulating thread. If it is a send event, its destination P_j is chosen at random from among the other n-1 processes. A corresponding receive event, (along with the sender’s EVC timestamp) and along with the simulation time timestamp is enqueued in Q_j and processed (perform EVC operations for a receive event) by the thread simulating P_j. The simulation time timestamp of a receive event, as chosen by the sender, can be set to the sum of the send event simulation time timestamp plus a uniformly chosen value between 0ms and 10ms.

The queue Q_i determines the schedule of events occurring at process P_i. The thread simulating process P_i dequeues events in simulation timestamp order, and simulates the EVC value update for that event. (That is, if an internal or send event, it does a multiply by the prime number associated with that process; if a receive event, it calculates the LCM, and then multiply, etc. etc. as per the EVC rules).
A Round Robin execution is implemented among the threads.

In addition, each thread P_i maintains a count of the number of simulated events it has processed after dequeueing from the local queue Q_i. For debugging purposes, it is useful to maintain 3 arrays: Send_simulated[1,n], Receive_simulated[1,n], and Internal_simulated[1,n] to count the corresponding number of send/receive/internal events dequeued and processed from each Q_i. In addition to the required EVC, it is useful to maintain the (traditional) vector clock VC of the latest event simulated by each thread.

# Documentation
A complete documentation explaining the results obtained can be found in my documentation paper: https://github.com/ArtyZiff35/EVC_Growth_Simulation/blob/master/paper/EVC_Growth_Rate_Simulation_Paper_ArturoCardone.pdf
