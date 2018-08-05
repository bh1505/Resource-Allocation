package lab3;

import java.util.Queue;
import java.util.LinkedList;

public class Process {

	protected Queue <String> schedule = new LinkedList <String> (); /*order of activities*/
	//array of initiate jobs
	protected int initOrder = 0;
	protected int [][] initiate = new int [3][4];
	//array of process's initial claims for each resource
	protected int [] claims = new int [4];
	protected int claimOrder = 0;
	//array of request jobs
	protected int reqOrder = 0;
	protected int [][] request = new int [3][4];
	//array of release jobs
	protected int releaseOrder = 0;
	protected int [][] release = new int [3][4];
	//terminate order
	protected int [] terminate = new int [4];
	protected boolean isAbort = false; 
	protected boolean isBlocked = false;
	protected boolean isTerm = false;
	protected boolean wasPassed = false; /*helps determine safe/unsafe state*/
	protected int time = 0; /*time taken*/
	protected int wait = 0; /*wait time*/
	protected int curRes []; /*how many resources owned for each type currently*/

	
}
