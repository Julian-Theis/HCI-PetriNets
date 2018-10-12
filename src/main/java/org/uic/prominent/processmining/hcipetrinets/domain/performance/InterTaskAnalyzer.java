package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.uic.prominent.processmining.hcipetrinets.clustering.Dictionary;
import org.uic.prominent.processmining.hcipetrinets.recommendations.InterTaskRecommendation;
import org.uic.prominent.processmining.hcipetrinets.recommendations.Recommendation;

public class InterTaskAnalyzer {

	public InterTaskLog optimalLog;
	public InterTaskLog userLog;
	public Dictionary dict;
	
	public double occurenceRateThreshold = 0.05;
	
	public InterTaskAnalyzer(InterTaskLog optimalLog, InterTaskLog userLog, Dictionary dict){
		this.optimalLog = optimalLog;
		this.userLog = userLog;
		this.dict = dict;
	}
	
	public void analyze(){
		optimalLog.createTraces();
		userLog.createTraces();
		
		List<InterTaskTrace> optTraces = optimalLog.getTranslatedTraces();
		List<InterTaskTrace> userTraces = userLog.getTranslatedTraces();
		
		for(InterTaskTrace trace : optTraces){
			trace.createSequenceString();
		}
		for(InterTaskTrace trace : userTraces){
			trace.createSequenceString();
			//System.out.println(trace.getSeqString());
		}
		
		//System.out.println("Analyze");
		InterTaskTrace optTrace = optTraces.get(0);
		String optSeq = optTrace.getSeqString();
		
		
		boolean done = true;
		while(done){
			boolean foundInThisRound = false;
			int maxTraceSize = 0;
			
			int c = 1;
			for(int i = 1; i <= optSeq.length(); i++){
				c++;
				foundInThisRound = false;
				String substring = optSeq.substring(0,i);
				maxTraceSize = 0;
				for(InterTaskTrace trace : userTraces){
					if(!trace.getSeqString().contains(substring)){
						if(substring.length() >= 1){
							//int startpos = trace.seq.indexOf(substring.substring(0, (substring.length() - 1)));	
							int startpos = trace.seq.indexOf(substring.substring(0, (substring.length() - 1)));	
							if(startpos == -1){
								startpos = 0;
							}
							//System.out.println(trace.seq);
							//System.out.println(substring.substring(0, (substring.length() - 1)));
							//System.out.println(startpos);
							try{
								trace.seq = trace.seq.replace(trace.seq.substring(startpos, startpos + substring.length() - 1), "#");
							}catch(Exception e){
								//System.out.println(e);
							}
						}
					}else{
						foundInThisRound = true;
					}
				}	

				if(!foundInThisRound){
					int startpos = optSeq.indexOf(substring.substring(0, (substring.length() - 1)));						
					optSeq = optSeq.replace(optSeq.substring(startpos, startpos + substring.length() - 1), "");
					//System.out.println("BREAK");
					break;
				}
				
				if(c >= optSeq.length() && optSeq.length() == 1){
					done = false;
					break;
				}
				if(c >= optSeq.length()){
					//System.out.println("Break to next one!");
					
					int startpos = optSeq.indexOf(substring.substring(0, substring.length()));
					optSeq = optSeq.replace(optSeq.substring(startpos, startpos + substring.length()), "");
					
					//System.out.println("Pattern " + substring);
					//System.out.println("Optimal Sequence " + optSeq);
				}
					
				//System.out.println("Trace Sequence " + userTraces.get(0).seq);
				//System.out.println("Pattern " + substring);
				//System.out.println("Optimal Sequence " + optSeq);
			}
		}
		
		for(InterTaskTrace trace : userTraces){
				trace.seq = trace.seq.substring(0, trace.seq.length()-1) + "#";
				//System.out.println(trace.seq);
		}	
		
		System.out.println("---------------");
		
		List<InterTaskRecommendation> recommendations = new ArrayList<InterTaskRecommendation>();
				
		
				
		
		for(InterTaskTrace trace : userTraces){
			String[] array = trace.getSeqString().split("#");
			for(String str : array){
				if(!str.equals("")){				
					String eventName = "";
					for (int i = 0; i < str.length(); i++){
					    char c = str.charAt(i);  
					    eventName += dict.retranslate(c+"") + " ";
					}
					long cost = trace.getCost(str);
					
					InterTaskRecommendation recommend = new InterTaskRecommendation(eventName, eventName, ((double) 1/userTraces.size()), cost);
					recommendations.add(recommend);
				}
			}
		}		
		
		HashMap<String, Long> costMap = new HashMap<String, Long>();
		for (InterTaskRecommendation c : recommendations) {
			if(costMap.get(c.eventName) == null){
				costMap.put(c.eventName, c.getSavingTime());
			}else{
				long value = costMap.get(c.eventName);
				costMap.put(c.eventName, (long) (value + c.getSavingTime()));
				//System.out.println((long) (value + c.getSavingTime()));
			}
		}
		for (InterTaskRecommendation c : recommendations) {
			c.setSavingTime(costMap.get(c.eventName));
		}
		
				
		
		
		
		HashMap<String, Integer> recommendationCount = new HashMap<String, Integer>();
		for (InterTaskRecommendation c : recommendations) {
			int value = recommendationCount.get(c.toString()) == null ? 0 : recommendationCount.get(c.toString());
			recommendationCount.put(c.toString(), value + 1);
		}
		
		List<InterTaskRecommendation> finalRecommendations = new ArrayList<InterTaskRecommendation>();
		for(String rec : recommendationCount.keySet()){
			InterTaskRecommendation r = new InterTaskRecommendation(rec);
			
			r.setSavingTime(r.getSavingTime()/recommendationCount.get(rec));
			//System.out.println(r.getSavingTime() + " ------ " + recommendationCount.get(rec) + " ---- " + r.getOccurenceRate());
			double rate = r.getOccurenceRate();
			rate = rate * recommendationCount.get(rec);
			
			if(rate > this.occurenceRateThreshold){
				r.setOccurenceRate(rate);
				finalRecommendations.add(r);
			}
		}
		
		System.out.println("***** InterTask Recommendations ******");
		for(InterTaskRecommendation r : finalRecommendations){
			r.printRecommendationString();
			System.out.println("--------------------------");
		}
		
		
	}
}
