package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.List;

public class Performance {

	private List<PerfMeasurement> measurements;

	public Performance() {
		this.measurements = new ArrayList<PerfMeasurement>();
	}

	public void addMeasurements(List<PerfMeasurement> measurements) {
		this.measurements.addAll(measurements);
	}

	public void calculateStatistics() {
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Calculating Performance Statistics");
		List<String> transitions = new ArrayList<String>();
		for (PerfMeasurement perf : measurements) {
			//System.out.println(perf.toString());

			if (!transitions.contains(perf.name())) {
				transitions.add(perf.name());
			}
		}

		for (String t : transitions) {
			List<Long> times = new ArrayList<Long>();
			for (PerfMeasurement perf : measurements) {
				if (perf.name().equals(t)) {
					times.add(perf.time());
				}
			}
			long mean = calculateAverage(times);
			
			//System.out.println(t + " mean " + mean);
			//System.out.println(t + " stddev " + calculateSd(times, mean));
		}
	}

	private Long calculateAverage(List<Long> marks) {
		Long sum = 0L;
		if (!marks.isEmpty()) {
			for (Long mark : marks) {
				sum += mark;
			}
			return (sum.longValue() / marks.size());
		}
		return sum;
	}
	
	public long calculateSd(List<Long> marks, Long mean)
	{
	    long temp = 0;
	    for (int i = 0; i < marks.size(); i++)
	    {
	        Long val = marks.get(i);

	        // Step 2:
	        Long squrDiffToMean = (long) (Math.pow(val - mean, 2));

	        // Step 3:
	        temp += squrDiffToMean;
	    }

	    // Step 4:
	    long meanOfDiffs = (long) temp / (long) (marks.size());

	    // Step 5:
	    return (long) Math.sqrt(meanOfDiffs);
	}
}
