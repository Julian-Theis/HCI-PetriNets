package org.uic.prominent.processmining.hcipetrinets.clustering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Event;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.InterTaskRec;

public class Dictionary {

	public Map<String, String> dictionary;
	
	private String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public Dictionary(){
		dictionary = new HashMap<String, String>();
	}
	
	public void loadSequence(List<InterTaskRec> trace){
		
		for(InterTaskRec ev : trace){
			if(!dictionary.containsKey(ev.getName())){
				String id = letters.substring(dictionary.size(), dictionary.size()+1);
				this.dictionary.put(ev.getName(), id);
			}
		}
	}
	
	public String translate(String ev){
		return dictionary.get(ev);
	}
	
	public String retranslate(String ev){
		for(String key : dictionary.keySet()){
			String value = dictionary.get(key);
			if(value.equals(ev)){
				return key; 
			}
		}
		return null;
	}
}
