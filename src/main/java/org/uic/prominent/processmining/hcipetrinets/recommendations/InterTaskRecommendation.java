package org.uic.prominent.processmining.hcipetrinets.recommendations;

import java.util.List;

public class InterTaskRecommendation {

	public long averageSavingTime;
	public double occurenceRate;
	public String eventName;
	public String userShouldNotDo;
	
	
	public InterTaskRecommendation(String event, String shouldNot, double occurenceRate, long savingTime){
		this.eventName = event;
		this.userShouldNotDo = shouldNot.toString();
		this.occurenceRate = occurenceRate;
		this.averageSavingTime = savingTime;
	}
	
	public InterTaskRecommendation(String input){
		String[] array = input.split("#####");
		this.eventName = array[0];
		this.userShouldNotDo = array[1];
		this.occurenceRate = Double.parseDouble(array[2]);
		this.averageSavingTime = Long.parseLong(array[3]);
	}
	
	public boolean filtered(){
		boolean filtered = true;

		return filtered;
	}
	
	public long getSavingTime(){
		return this.averageSavingTime;
	}
	
	public void setSavingTime(long savingtime){
		this.averageSavingTime = savingtime;
	}
	
	public double getOccurenceRate(){
		return this.occurenceRate;
	}
	
	public void setOccurenceRate(double occurenceRate){
		this.occurenceRate = occurenceRate;
	}
	
	public String toString(){
		return this.eventName + "#####" + this.userShouldNotDo + "#####" + this.occurenceRate + "#####" + this.averageSavingTime;
	}
	
	public void printRecommendationString(){
		System.out.println("Task: " + this.eventName);
		System.out.println("Occurence Rate: " + this.occurenceRate);
		System.out.println("Average Saving Time: " + this.averageSavingTime);
		System.out.println("User should not repetitvely do: " + this.userShouldNotDo);
	}
}
