package org.uic.prominent.processmining.hcipetrinets.domain.conformance;


import java.util.ArrayList;
import java.util.List;

import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Trace;


public class ConformanceParameters {
	private Trace trace;
	private int count; // How often is this trace present in the log?
	private int missing;
	private int remaining;
	private int consumed;
	private int produced;
	private List<Integer> enabledTransitions = new ArrayList<>();

	public ConformanceParameters(Trace trace, int count) {
		this.trace = trace;
		this.count = count;
	}

	public Trace trace() {
		return trace;
	}

	public int count() {
		return count;
	}

	public int missing() {
		return missing;
	}

	public int remaining() {
		return remaining;
	}

	public int consumed() {
		return consumed;
	}

	public int produced() {
		return produced;
	}

	public void incrementMissing() {
		missing++;
	}
	
	public void incrementMissing(int incrementBy) {
		missing += incrementBy;
	}

	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	public void incrementConsumed() {
		consumed++;
	}

	public void incrementProduced() {
		produced++;
	}

	public void addEnabledTransition(Integer count) {
		enabledTransitions.add(count);
	}

	public double getMeanEnabledTransitions() {
		double sum = 0;
		for (Integer c : enabledTransitions) {
			sum += c;
		}
		return sum / enabledTransitions.size();
	}

	@Override
	public String toString() {
		return "{" +
				"n=" + count +
				", m=" + missing +
				", r=" + remaining +
				", c=" + consumed +
				", p=" + produced +
				", mean=" + getMeanEnabledTransitions() +
				'}';
	}

}
