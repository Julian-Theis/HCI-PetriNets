package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Transition;

public class IntraTaskPerformance {

	public List<String> finalList;
	public List<String> intermediate;

	public List<IntraTask> intraTasks;

	public IntraTask imed;

	public int traces = 0;

	public Map<IntraTask, String> intraTaskToFinal;
	public Map<String, Double> finalEventCounts;

	public IntraTaskPerformance() {
		intraTasks = new ArrayList<IntraTask>();
	}

	public void traceCount() {
		traces++;
	}

	public void tokenInStart(String currentToken) {
		imed = new IntraTask(currentToken);
		intraTasks.add(imed);
	}

	public void addTransition(Transition t) {
		if (t.isControllable()) {
			imed.add(t.name());
		} else {
			imed.setFinalEvent(t.name());
			imed = new IntraTask(t.name());
			intraTasks.add(imed);
		}

	}

	public void analyze() {
		intraTaskToFinal = new HashMap<IntraTask, String>();

		for (IntraTask it : intraTasks) {
			intraTaskToFinal.put(it, it.finalEvent);
		}

		finalEventCounts = new HashMap<String, Double>();

		for (String c : intraTaskToFinal.values()) {
			double value = finalEventCounts.get(c) == null ? 0 : finalEventCounts.get(c);
			finalEventCounts.put(c, value + 1);
		}

		for (String key : finalEventCounts.keySet()) {
			double value = finalEventCounts.get(key) / traces;
			finalEventCounts.put(key, value);
		}

		System.out.println(finalEventCounts);
	}

	public Set<String> getIntraTaskNames() {
		return finalEventCounts.keySet();
	}

	public List<IntraTask> getIntraTaskByName(String name) {
		List<IntraTask> its = new ArrayList<IntraTask>();

		for (IntraTask it : intraTaskToFinal.keySet()) {
			if (it.finalEvent != null) {
				if (it.finalEvent.equals(name)) {
					its.add(it);
				}
			}
		}

		return its;
	}
}
