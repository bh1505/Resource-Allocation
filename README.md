# Resource-Allocation
The goal of this lab is to simulate resource allocation using both an optimistic resource manager and the banker’s
algorithm of Dijkstra. The optimistic resource manager is simple: Satisfy a request if possible, if not make
the task wait; when a release occurs, try to satisfy pending requests in a FIFO manner.

The program takes one command line argument, the name of the file containing the input. After reading
(all) the input, the program performs two simulations: one with the optimistic manager and one with the
banker. Output is written to stdout (the screen). Input files are given. 

The input begins with two values T, the number of tasks, and R, the number of resource types, followed by
R additional values, the number of units present of each resource type. Then come multiple inputs,
each representing the next activity of a specific task. The possible activities are initiate, request, release,
and terminate. Time is measured in fixed units called cycles and, for simplicity, no fractional cycles are used.
The manager can process one activity (initiate, request, or release) for each task in one cycle. However, the
terminate activity does not require a cycle.

At the end of the run for each task, the time taken, the waiting time, and the percentage of time
spent waiting are printed. Also printed are the total time for all tasks, the total waiting time, and the overall percentage of
time spent waiting.
