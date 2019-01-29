#EVC Growth Rate Simulation

The vector clock is a fundamental tool to characterize causality
in distributed executions. Each process needs to maintain
a vector of size n, where n is the total number of processes in the
system, to represent the local vector clock. Unfortunately, this does
not scale well to large systems. Several works in the literature
attempted to reduce the size of vector clocks [12, 16, 21, 22], but
they had to make some compromises in accuracy or alter the system
model, and in the worst-case, were as lengthy as vector clocks. To
address this problem, we propose the encoding of the vector clock
using prime numbers to use a single number to represent vector
time.
