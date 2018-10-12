package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.List;

import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Event;

public class InterTaskTrace {
	
	public String seq;
	public String recommendPattern = "";
	public List<InterTaskRec> trace;
	
	public InterTaskTrace(){
		this.trace = new ArrayList<InterTaskRec>();
	}
	
	public String getRecommendPattern(){
		return recommendPattern;
	}
	
	public void setRecommendPattern(String pattern){
		this.recommendPattern += pattern;
	}
	
	public void createSequenceString(){
		this.seq = "";
		for(InterTaskRec rec : trace){
			seq += rec.getName();
		}
	}
	
	public long getCost(String str){
		long time = 0L;

		for (int i = 0; i < str.length(); i++){
		    char c = str.charAt(i);  
		    
		    long tt = 0L;
		    int cnt = 0;
		    for(InterTaskRec rec : trace){
				if(rec.getName().equals(c+"")){
					tt += rec.getTime();
					cnt++;
				}
			}
		    
		    long calc = (long) tt / (int) cnt;
		    
		    
		    time += calc;
		    
		    if(Math.abs((double) time/20000) > 3)
		    	time = 20000L;
		    //System.out.println(time);
		}
		return time;
	}
	
	
	public String getSeqString(){
		return seq;
	}
	
	public InterTaskTrace(ArrayList<InterTaskRec> trace){
		this.trace = trace;
	}
	
	public void addEvent(InterTaskRec ev){
		this.trace.add(ev);
	}
	
	public List<InterTaskRec> getTrace(){
		return this.trace;
	}
	
	public void createSeq(){
		this.seq = "";
		for(InterTaskRec rec : trace){
			seq += rec.getName();
		}
	}

}
