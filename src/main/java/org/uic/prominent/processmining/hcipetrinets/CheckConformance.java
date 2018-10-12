package org.uic.prominent.processmining.hcipetrinets;

import org.apache.commons.cli.*;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.EventLog;
import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.PetriNet;
import org.uic.prominent.processmining.hcipetrinets.parsers.EventLogParser;
import org.uic.prominent.processmining.hcipetrinets.parsers.PetriNetParser;

public class CheckConformance {
	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("p", "petrinet", true, "petrinet input filename");
		options.addOption("e", "eventlog", true, "eventlog input filename");


		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("p") && line.hasOption("e")) {
				String petriNetFilename = line.getOptionValue("p");
				String eventLogFilename = line.getOptionValue("e");

				PetriNet petriNet = new PetriNetParser().getPetriNetFromFile(petriNetFilename);
				EventLog eventLog = new EventLogParser().getEventLogFromFile(eventLogFilename);

				double fitness = new ConformanceChecker(petriNet, eventLog).getFitness();
				double sba = new ConformanceChecker(petriNet, eventLog).getSimpleBehavioralAppropriateness();
				double ssa = new ConformanceChecker(petriNet, eventLog).getSimpleStructuralAppropriateness();

				System.out.println("Fitness: " + fitness);
				System.out.println("Simple Behavioral Appropriateness: " + sba);
				System.out.println("Simple Structural Appropriateness: " + ssa);
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java CheckConformance", "Petri net conformance checker.",
						options, "", true);
			}
		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}
	}

}
