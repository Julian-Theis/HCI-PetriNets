package org.uic.prominent.processmining.hcipetrinets.recommendations;

import java.util.List;

public class Recommendation {

	public long averageSavingTime;
	public double occurenceRate;
	public String eventName;
	public String userShouldDo;
	public String userShouldNotDo;
	public double totalOccurencePerTrace;
	
	
	public Recommendation(String event, List<String> should, List<String> shouldNot, double occurenceRate, double totalOccurencePerTrace, long savingTime){
		this.eventName = event;
		this.userShouldDo = should.toString();
		this.userShouldNotDo = shouldNot.toString();
		this.occurenceRate = occurenceRate;
		this.totalOccurencePerTrace = totalOccurencePerTrace;
		this.averageSavingTime = savingTime;
	}
	
	public Recommendation(String input){
		String[] array = input.split("#####");
		this.eventName = array[0];
		this.userShouldDo = array[1];
		this.userShouldNotDo = array[2];
		this.occurenceRate = Double.parseDouble(array[3]);
		this.totalOccurencePerTrace = Double.parseDouble(array[4]);
		this.averageSavingTime = Long.parseLong(array[5]);
	}
	
	public boolean filtered(){
		boolean filtered = false;
		
		
		
		if(userShouldDo.length() == 2 || userShouldNotDo.length() == 2){
			filtered = true;
		}
		
		
		
		
		if(this.averageSavingTime < 0L){
			filtered = true;
		}
		
		
		
		
		String[] arrayDo = userShouldDo.split(", ");
		String[] arrayDont = userShouldNotDo.split(", ");
		boolean onlyMouse = true;
		for(String s : arrayDo){
			if(!s.contains("mouse to")){
				onlyMouse = false;
			}
		}
		for(String s : arrayDont){
			if(!s.contains("mouse to")){
				onlyMouse = false;
			}
		}
		if(onlyMouse){
			filtered = true;
		}
		

		return filtered;
	}
	
	public double getOccurenceRate(){
		return this.occurenceRate;
	}
	
	public void setOccurenceRate(double occurenceRate){
		this.occurenceRate = occurenceRate;
	}
	
	public String toString(){
		return this.eventName + "#####" + this.userShouldDo + "#####" + this.userShouldNotDo + "#####" + this.occurenceRate + "#####" + this.totalOccurencePerTrace + "#####" + this.averageSavingTime;
	}
	
	public void printRecommendationString(){
		System.out.println("Task: " + this.eventName);
		System.out.println("Occurence Rate: " + this.occurenceRate);
		System.out.println("Total Occurence Per Trace: " + this.totalOccurencePerTrace);
		System.out.println("Average Saving Time: " + this.averageSavingTime);
		System.out.println("User should not do: " + this.userShouldNotDo);
		System.out.println("User should do instead: " + this.userShouldDo);
	}
}
