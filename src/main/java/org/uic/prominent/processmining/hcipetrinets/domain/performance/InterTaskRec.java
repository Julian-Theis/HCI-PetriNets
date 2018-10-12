package org.uic.prominent.processmining.hcipetrinets.domain.performance;

public class InterTaskRec {

	public String name;
	public long time;
	
	public InterTaskRec(String name, long time){
		this.name = name;
		this.time = time;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name;
	}
	
	public long getTime(){
		return time;
	}
}
