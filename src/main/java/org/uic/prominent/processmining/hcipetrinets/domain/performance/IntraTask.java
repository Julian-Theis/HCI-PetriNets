package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.List;

public class IntraTask {

	String initialEvent;
	List<String> controllables;
	String finalEvent;
	
	
	public IntraTask(String initialEvent){
		this.initialEvent = initialEvent;
		controllables = new ArrayList<String>();
	}
	
	public void add(String transName){
		this.controllables.add(transName);
	}
	
	public void setFinalEvent(String transName){
		this.finalEvent = transName;
	}
	
	public String toString(){
		String str = "";
		for(String s : controllables){
			str += s + ";";
		}
		return str += finalEvent;
	}
}
