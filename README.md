# EVC Growth Rate Simulation

The vector clock is a fundamental tool to characterize causality
in distributed executions. Each process needs to maintain
a vector of size n, where n is the total number of processes in the
system, to represent the local vector clock. Unfortunately, this does
not scale well to large systems. Several works in the literature
attempted to reduce the size of vector clocks, but
they had to make some compromises in accuracy or alter the system
model, and in the worst-case, were as lengthy as vector clocks. To
address this problem, we propose the encoding of the vector clock
using prime numbers to use a single number to represent vector
time.

A goal of the project is to identify how fast the EVC grows, as a function of the number of events executed by a process, and the number of events executed by all the processes collectively. n is an input parameter. With n processes and assuming 32-bit integer, how many events it takes for the size of the EVC to occupy a number equal to 32n bits long. Once it equals 32n bits long, we can do a system-wide EVC reset.
