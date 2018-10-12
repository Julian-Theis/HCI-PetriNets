package org.uic.prominent.processmining.hcipetrinets.domain.eventlog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class EventLog {
	public Map<Trace, Integer> traceCounts = new HashMap<>();
	

	public void addTrace(Trace trace) {
		traceCounts.put(trace, incrementCount(trace));
	}

	public Integer getCount(Trace trace) {
		return traceCounts.get(trace);
	}
	
	public Integer getNumTraces(){
		Iterator<Trace> itertraces = traceCounts.keySet().iterator();
		while(itertraces.hasNext()){
			Trace trace = itertraces.next();
		}
		
		return traceCounts.size();
	}

	public void forEach(BiConsumer<Trace, Integer> action) {
		traceCounts.forEach(action);
	}

	private Integer incrementCount(Trace trace) {
		Integer count = traceCounts.get(trace);
		count = (count == null) ? 0 : count;
		return ++count;
	}

	@Override
	public String toString() {
		return traceCounts.toString();
	}
}
