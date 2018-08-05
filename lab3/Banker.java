package lab3;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Banker {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String arg = args[0];
		File input = new File (arg);
		if ( !input.exists() ) {
			System.err.print("File does not exist.");
			System.exit(1);
		}
		
		Scanner scan = new Scanner (input);
		
		//store num of tasks and resource types for FIFO 
		int numTasks = scan.nextInt();
		int numResTypes = scan.nextInt();
		//number of resources available for each resource type (FIFO)
		int arrRes [] = new int [numResTypes];
		for (int i = 0; i < numResTypes; i++) {
			arrRes [i] = scan.nextInt();
		}
		//repeat for banker's algo
		int numTasksAlgo = numTasks;
		int numResTypesAlgo = numResTypes;
		int arrResAlgo [] = new int [numResTypesAlgo];
		for (int i = 0; i < numResTypes; i++) {
			arrResAlgo [i] = arrRes [i];
		}
		
		//list of the processes (FIFO)
		ArrayList <Process> bank = new ArrayList <Process> ();
		//list of the processes (BANKER'S)
		ArrayList <Process> algo = new ArrayList <Process> ();
		//original order of tasks, for printing the final output (BANKER's)
		ArrayList <Process> algoOG = new ArrayList <Process> ();
		//original order of tasks, for printing the final output (FIFOs)
		ArrayList <Process> og = new ArrayList <Process> ();
		//helps look at blocked tasks firstly
		ArrayList <Process> blockedBank = new ArrayList <Process> ();
		
		//populate the lists and the order of operations for each process
		for (int i = 0; i < numTasks; i++) {
			Process p = new Process();
			Process p1 = new Process ();
			p.curRes = new int [numResTypes];
			bank.add(p);
			og.add(p);
			p1.curRes = new int [numResTypesAlgo];
			algo.add(p1);
			algoOG.add(p1);
		}
		//load the schedule for each task
		while (scan.hasNext()) {
			String cur = scan.next();
			if (cur.equals("initiate")) {
				int ind = scan.nextInt();
				Process p = bank.get(ind-1);
				Process p1 = algo.get(ind-1);
				p.schedule.add(cur);
				p1.schedule.add(cur);
				p.initiate [p.initOrder][0] = ind;
				p1.initiate [p1.initOrder][0] = ind;
				for (int i = 1; i < 4; i++) {
					int a = scan.nextInt();
					p.initiate [p.initOrder][i] = a;
					p1.initiate [p1.initOrder][i] = a;
				}
				p.initOrder++;
				p1.initOrder++;
			} else if (cur.equals("request")) {
				int ind = scan.nextInt();
				Process p = bank.get(ind-1);
				Process p1 = algo.get(ind-1);
				p.schedule.add(cur);
				p1.schedule.add(cur);
				p.request [p.reqOrder][0] = ind;
				p1.request [p1.reqOrder][0] = ind;
				for (int i = 1; i < 4; i++) {
					int b = scan.nextInt();
					p.request [p.reqOrder][i] = b;
					p1.request [p1.reqOrder][i] = b;
				}
				p.reqOrder ++;
				p1.reqOrder ++;
			} else if (cur.equals("release")) {
				int ind = scan.nextInt();
				Process p = bank.get(ind-1);
				Process p1 = algo.get(ind-1);
				p.schedule.add(cur);
				p1.schedule.add(cur);
				p.release [p.releaseOrder][0] = ind;
				p1.release [p1.releaseOrder][0] = ind;
				for (int i = 1; i < 4; i++) {
					int c = scan.nextInt ();
					p.release [p.releaseOrder][i] = c;
					p1.release [p1.releaseOrder][i] = c;
				}
				p.releaseOrder ++;
				p1.releaseOrder ++;
			} else if (cur.equals("terminate")) {
				int ind = scan.nextInt();
				Process p = bank.get(ind-1);
				Process p1 = algo.get(ind-1);
				p.schedule.add(cur);
				p1.schedule.add(cur);
				p.terminate [0] = ind;
				p1.terminate [0] = ind;
				for (int i = 1; i < 4; i++) {
					int d = scan.nextInt();
					p.terminate [i] = d;
					p1.terminate [i] = d;
				}
			} 
		}
		scan.close();
		//zero req, release, and init orders
		for (Process p : bank) {
			p.reqOrder = 0;
			p.releaseOrder = 0;
			p.initOrder = 0;
		}
		for (Process p : algo) {
			p.reqOrder = 0;
			p.releaseOrder = 0;
			p.initOrder = 0;
		}
		
		//cycle through until all tasks have completed (FIFO)
		int cycle = 0;
		int terms = 0; /*number of tasks that have currently completed*/
		//keeps track of resources being released at end of cycle
		int release [] = new int [numResTypes];
		while (terms < bank.size()) {
			for (Process p : bank) {
				if (p.schedule.isEmpty() || p.isAbort) continue;
				if (p.schedule.peek().equals("initiate")) {
					p.schedule.poll();
					p.time++;
				} else if (p.schedule.peek().equals("request")) {
					//if request cannot be granted
					int resType = p.request[p.reqOrder][2] - 1;
					if (p.request[p.reqOrder][3] > arrRes [resType]) {
						//block the task
						p.isBlocked = true;
						blockedBank.add(p);
						p.wait ++;
						p.time ++;
							
					} else {
						//make sure there is no delay
						if (p.request[p.reqOrder][1] == 0) {
							//complete request
							if (p.isBlocked) p.isBlocked = false;
							p.curRes [resType] += p.request[p.reqOrder][3];
							arrRes [resType] -= p.request[p.reqOrder][3];
							p.reqOrder ++;
							p.time++;
							p.schedule.poll();
						} else {
							p.request[p.reqOrder][1] -= 1;
							p.time++;
						}
						
					}
				} else if (p.schedule.peek().equals("release"))  {
					if (p.release[p.releaseOrder][1] == 0) {
						//release resources
						int resType = p.release[p.releaseOrder][2] - 1;
						release [resType] += p.release[p.releaseOrder][3];
						p.curRes [resType] -= release [resType];
						p.time ++;
						p.schedule.poll();
						p.releaseOrder ++;
					} else {
						p.release[p.releaseOrder][1] -= 1;
						p.time++;
					}
					
				} else if (p.schedule.peek().equals("terminate")) {
					if (p.terminate[1] == 0) {
						p.isTerm = true;
						p.schedule.poll();
						terms++;
					} else {
						p.terminate[1] -= 1;
						p.time++;
					}
					
				}
			}//for
			cycle++;
			//release resources at the end of cycle
			for (int i = 0; i < release.length; i++) {
				if (release[i] > 0) {
					arrRes[i] += release[i];
					release[i] = 0;
				}
			}
			
			//look at blocked processes first by placing them in the front of list
			if (!blockedBank.isEmpty()) {
				for (Process p : bank) {
					if (!blockedBank.contains(p)) {
						blockedBank.add(p);
					}
				}
				//bank = blockedBank;
				for (int i = 0; i < bank.size(); i++) {
					bank.set(i, blockedBank.get(i));
				}
				blockedBank.clear();
			}
			//check for deadlock, abort necessary processes 
			if (isBlocked(og) && terms < bank.size()) {
				//abort lowest task
				Process first = og.get(0);
				first.isAbort = true;
				first.isBlocked = false;
				//release resources
				for (int i = 0; i < first.curRes.length; i++) {
					arrRes [i] += first.curRes[i];
					first.curRes [i] = 0;
				}
				terms++;
				//check if there still is deadlock
				boolean again = true;
				for (Process p : bank) {
					if (!p.isAbort && !p.isTerm) {
						int resType = p.request[p.reqOrder][2] - 1;
						if (p.schedule.peek().equals("request") &&
								p.request[p.reqOrder][3] <= arrRes [resType]) {
							again = false;
						}
					}
				}
				//if still deadlocked after aborting lowest task, abort next lowest task
				if (again) {
					for (Process p : bank) {
						if (!p.isAbort && !p.isTerm) {
							if (isLowest(og, p) && p.schedule.peek().equals("request")) {
								int resType = p.request[p.reqOrder][2] - 1;
								p.isAbort = true;
								p.isBlocked = false;
								arrRes [resType] += p.curRes [resType];
								p.curRes [resType] = 0;
								terms++;
								break;
							}
						}
					}
				}
			}
		}
				
		//BANKER's Algorithm Implementation
		
		int cycleAlgo = 0;
		int termsAlgo = 0;
		int releaseAlgo [] = new int [numResTypesAlgo];
		while (termsAlgo < algo.size()) {
			for (Process p : algo) {
				if (p.schedule.isEmpty() || p.isAbort) continue;
				if (p.schedule.peek().equals("initiate")) {
					p.claims[p.claimOrder] = p.initiate[p.initOrder][3];
					//check if claim exceeds available resources
					if (p.claims[p.claimOrder] > arrResAlgo[p.claimOrder]) {
						//abort task and print informative message
						p.isAbort = true;
						p.isBlocked = false;
						termsAlgo++;
						System.out.printf("Banker aborts task %d before run begins:\n", 
								p.initiate[0][0]);
						System.out.printf("\tclaim for resource %d (%d) exceeds "
								+ "number of units present (%d)\n", p.claimOrder + 1,
								p.claims[p.claimOrder], arrResAlgo[p.claimOrder]);
						continue;
					}
					p.schedule.poll();
					p.claimOrder++;
					p.initOrder++;
					p.time++;
				} else if (p.schedule.peek().equals("request")) {
					int resType = p.request[p.reqOrder][2] - 1;
					//check for delay
					if (p.request[p.reqOrder][1] == 0) {
						p.curRes [resType] += p.request[p.reqOrder][3];
						//check if request exceeds initial claim
						if (p.curRes[resType] > p.initiate[resType][3]) {
							p.curRes [resType] -= p.request[p.reqOrder][3];
							p.isAbort = true;
							p.isBlocked = false;
							//release all its resources
							int res = 0;
							for (int i = 0; i < p.curRes.length; i++) {
								releaseAlgo [i] += p.curRes[i];
								res += p.curRes[i];
								p.curRes [i] = 0;
							}
							System.out.printf("During cycle %d-%d of Banker's algorithms:\n"
									,cycleAlgo, cycleAlgo+1);
							System.out.printf("\tTask %d's request exceeds its claim; aborted; "
									+ "%d units available next cycle\n", p.request[0][0], res);
							termsAlgo++;
							continue;
						}
						arrResAlgo [resType] -= p.request[p.reqOrder][3];
						p.time++;
						//after the request, check if this is a safe state
						boolean go = isSafe (algo, arrResAlgo);
						if (go) {
							if (p.isBlocked) p.isBlocked = false;
							p.reqOrder ++;
							p.schedule.poll();
						} else {
							//if unsafe, undo request and block task
							p.curRes [resType] -= p.request[p.reqOrder][3];
							arrResAlgo [resType] += p.request[p.reqOrder][3];
							p.isBlocked = true;
							blockedBank.add(p);
							p.wait ++;
						}
							
					} else {
						p.request[p.reqOrder][1] -= 1;
						p.time++;
					}
				} else if (p.schedule.peek().equals("release"))  {
					if (p.release[p.releaseOrder][1] == 0) {
						int resType = p.release[p.releaseOrder][2] - 1;
						releaseAlgo [resType] += p.release[p.releaseOrder][3];
						p.curRes [resType] -= releaseAlgo [resType];
						p.time ++;
						p.schedule.poll();
						p.releaseOrder ++;
					} else {
						p.release[p.releaseOrder][1] -= 1;
						p.time++;
					}
					
				} else if (p.schedule.peek().equals("terminate")) {
					if (p.terminate[1] == 0) {
						p.isTerm = true;
						p.schedule.poll();
						termsAlgo++;
					} else {
						p.terminate[1] -= 1;
						p.time++;
					}
					
				}
			}//for
			cycleAlgo++;
			//release resources at the end of cycle
			for (int i = 0; i < releaseAlgo.length; i++) {
				if (releaseAlgo[i] > 0) {
					arrResAlgo[i] += releaseAlgo[i];
					releaseAlgo[i] = 0;
				}
			}

			//look at blocked processes first by placing them in the front of list
			if (!blockedBank.isEmpty()) {
				for (Process p : algo) {
					if (!blockedBank.contains(p)) {
						blockedBank.add(p);
					}
				}
				//bank = blockedBank;
				for (int i = 0; i < algo.size(); i++) {
					algo.set(i, blockedBank.get(i));
				}
				blockedBank.clear();
			}
		}
		
		//print out results
		System.out.println("\t\tFIFO\t\t\t\tBANKER'S");
		int task = 1;
		//totals for FIFO
		int totalTime = 0;
		int totalWait = 0;
		//totals for BANKER'S
		int totalTimeAlgo = 0;
		int totalWaitAlgo = 0;
		for (int i = 0; i < bank.size(); i++) {
			Process p = og.get(i);
			Process p1 = algoOG.get(i);
			//FIFO task times
			System.out.printf("\tTask %d    ", task);
			if (p.isAbort) System.out.print("aborted");
			else {
				System.out.print(p.time + "\t" + p.wait + "   ");
				totalTime += p.time;
				totalWait += p.wait;
				double rat = (double) p.wait / (double) p.time * 100;
				DecimalFormat format = new DecimalFormat("#");
				System.out.print(format.format(Math.round(rat))+ "%");
			}
			//BANKER's Task times
			System.out.printf("\t\tTask %d    ", task);
			if (p1.isAbort) System.out.println("aborted");
			else {
				System.out.print(p1.time + "\t" + p1.wait + "   ");
				totalTimeAlgo += p1.time;
				totalWaitAlgo += p1.wait;
				double rat = (double) p1.wait / (double) p1.time * 100;
				DecimalFormat format = new DecimalFormat("#");
				System.out.println(format.format(Math.round(rat))+ "%");
			}
			task++;
		}
		//FIFO total
		System.out.print("\tTotal     ");
		System.out.print(totalTime + "\t" + totalWait + "   ");
		double cat = (double) totalWait / (double) totalTime * 100;
		DecimalFormat form1 = new DecimalFormat("#");
		System.out.print(form1.format(Math.round(cat))+ "%");
		//Bankers total
		System.out.print("\t\tTotal     ");
		System.out.print(totalTimeAlgo + "\t" + totalWaitAlgo + "   ");
		double dog = (double) totalWaitAlgo / (double) totalTimeAlgo * 100;
		DecimalFormat form2 = new DecimalFormat("#");
		System.out.println(form2.format(Math.round(dog))+ "%");
		
	}
	//Accepts the current state of the bank and the number of 
	//available resources for each type of resource as parameters.
	//Checks if the current request ensures a safe state.
	//Returns true if safe state, false otherwise.
	private static boolean isSafe (ArrayList <Process> bank, int [] arrRes){
		//create tmp array so resource values are not altered upon return 
		int [] arr = new int [arrRes.length];
		for (int i = 0; i < arr.length; i++) {
			arr [i] = arrRes [i];
		}
		boolean canTerm = true;
		//loop until all processes can arbitrarily terminate or if unsafe state
		while (canTerm) {
			canTerm = false;
			for (Process p : bank) {
				if (p.wasPassed || p.isAbort || p.isTerm 
						|| p.schedule.peek().equals("terminate")) continue;
				//check if any process can fulfill all its initial claims
				int test = 0;
				for (int i = 0; i < arr.length; i++) {
					if (p.claims[i] - p.curRes[i] <= arr [i]) {
						test ++;
						//if all initial claims of the process can be satisfied
						if (test == arr.length) {
							for (int j = 0; j < arr.length; j++) {
								arr[j] += p.curRes[j];
							}
							//makes sure terminated process is skipped 
							p.wasPassed = true;
							//keep looping to see if all processes can terminate
							canTerm = true;
							break;
						}
					}
				}
				test = 0;
				
					
			}
		
			//if a process could not terminate, this is an unsafe state 
			if (!canTerm) {
				for (Process p : bank) {
					p.wasPassed = false;
				}
				return false;
			}
			//check if all processes could terminate, thus state is safe
			boolean safe = true;
			for (Process p : bank) {
				if (p.wasPassed || p.isAbort || p.isTerm 
						|| p.schedule.peek().equals("terminate")) continue;
				else {
					safe = false;
					break;
				}
			}
			//break out of loop if all processes could determine, state is safe
			if (safe) {
				for (Process p : bank) {
					p.wasPassed = false;
				}
				break;
			}
			
		}
		return true;
	}
	
	//Checks for deadlock.
	//Returns true if all remaining processes are blocked, false otherwise.
	private static boolean isBlocked (ArrayList <Process> bank) {
		boolean all = true;
		for (Process p : bank) {
			if (!p.isBlocked) {
				if (p.isAbort || p.schedule.isEmpty()) all = true;
				else return false;
			}
		}
		return all;
	}
	//Checks if process parameter is the lowest task remaining.
	private static boolean isLowest (ArrayList <Process> bank, Process p) {
		int index = bank.indexOf(p);
		for (Process cur : bank) {
			if (cur.equals(p)) continue;
			else if (cur.isAbort) continue;
			else if (cur.schedule.isEmpty()) continue;
			else {
				if (bank.indexOf(cur) < index) return false;
			}
		}
		return true;
	}
	
}
