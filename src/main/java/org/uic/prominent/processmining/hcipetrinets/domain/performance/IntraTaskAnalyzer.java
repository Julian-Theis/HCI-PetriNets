package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.uic.prominent.processmining.hcipetrinets.recommendations.Recommendation;

public class IntraTaskAnalyzer {

	private IntraTaskPerformance userITP;
	private IntraTaskPerformance optITP;
	
	private long userReactivity;
	private long optReactivity;
	
	private double occurenceRateThreshold = 0.03;
	
	public IntraTaskAnalyzer(IntraTaskPerformance userITP, long userReactivity, IntraTaskPerformance optITP, long optReactivity){
		this.userITP = userITP;
		this.optITP = optITP;
		this.userReactivity = userReactivity;
		this.optReactivity = optReactivity;
	}
	
	public void analyze(){
		this.userITP.analyze();
		this.optITP.analyze();
		
		Set<String> keys = userITP.getIntraTaskNames();
		List<Recommendation> recommendations = new ArrayList<Recommendation>();
		
		for(String eventName : keys){
			List<IntraTask> uITs = userITP.getIntraTaskByName(eventName);
			List<IntraTask> oITs = optITP.getIntraTaskByName(eventName);
					
			for(IntraTask main : oITs){
				List<String> mainList = main.controllables;
				
				for(IntraTask user : uITs){
					
					List<String> userList = user.controllables;
					ArrayList differenceMainMinusUser = new ArrayList(mainList); //WHAT USER SHOULD DO !
					differenceMainMinusUser.removeAll(userList);
					
					ArrayList differenceUserMinusMain = new ArrayList(userList); //WHAT USER SHOULD NOT DO!
					differenceUserMinusMain.removeAll(mainList);
					
					long savingTime = (differenceUserMinusMain.size() - differenceMainMinusUser.size())* userReactivity;
					double totalOccurencePerTrace = this.userITP.finalEventCounts.get(eventName);
					double occurenceRate = 1 / (totalOccurencePerTrace * this.userITP.traces);
					
					Recommendation recommend = new Recommendation(eventName, differenceMainMinusUser, differenceUserMinusMain, occurenceRate, totalOccurencePerTrace, savingTime);
					recommendations.add(recommend);
				}
			}
		}
		

		HashMap<String, Integer> recommendationCount = new HashMap<String, Integer>();
		for (Recommendation c : recommendations) {
			int value = recommendationCount.get(c.toString()) == null ? 0 : recommendationCount.get(c.toString());
			recommendationCount.put(c.toString(), value + 1);
		}
		
		List<Recommendation> finalRecommendations = new ArrayList<Recommendation>();
		for(String rec : recommendationCount.keySet()){
			Recommendation r = new Recommendation(rec);
			double rate = r.getOccurenceRate();
			rate = rate * recommendationCount.get(rec);
			
			if(rate > this.occurenceRateThreshold && !r.filtered()){
				r.setOccurenceRate(rate);
				finalRecommendations.add(r);
			}
		}
		
		
		
		
		System.out.println("***** IntraTask Recommendations ******");
		for(Recommendation r : finalRecommendations){
			r.printRecommendationString();
			System.out.println("--------------------------");
		}

		
	}
}
