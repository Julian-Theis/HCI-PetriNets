package org.uic.prominent.processmining.hcipetrinets;

import java.util.ArrayList;
import java.util.List;

import org.uic.prominent.processmining.hcipetrinets.clustering.Dictionary;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Event;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.EventLog;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.BasePerformance;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.InterTaskAnalyzer;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.InterTaskLog;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.InterTaskRec;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.IntraTaskAnalyzer;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.IntraTaskPerformance;
import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.PetriNet;
import org.uic.prominent.processmining.hcipetrinets.parsers.EventLogParser;
import org.uic.prominent.processmining.hcipetrinets.parsers.PNMLPreprocess;
import org.uic.prominent.processmining.hcipetrinets.parsers.PetriNetParser;

/**
 * Hello world!
 * 
 * https://github.com/ErkoRisthein/conformance-checker/tree/master/src/main/java/ee/ut/cs/modeling/checker
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	//String petriNetFilename = "test.pnml";
		//String eventLogFilename = "test_extra.xes";
    	//String petriNetFilename = "sc1_hl_userbehavior.csv_2_model.pnml";
    	
    	String originalPetriNetFilename = "u3_825_hl.csv_2_model.pnml";
    	PNMLPreprocess preprocess = new PNMLPreprocess(originalPetriNetFilename);
    	
    	String petriNetFilename = "modified.pnml";
    	String eventLogFilename = "u3_825_hl.csv.xes";
    	String optLogFilename = "paper_opt.csv.xes";

		PetriNet petriNet = new PetriNetParser().getPetriNetFromFile(petriNetFilename);
		
		EventLog eventLog = new EventLogParser().getEventLogFromFile(eventLogFilename);
		EventLog optLog = new EventLogParser().getEventLogFromFile(optLogFilename);
		
		
		BasePerformance basePerformance = new BasePerformance(eventLog);
		long userReactivity = basePerformance.getReactivity();
		System.out.println("User Reactivity: "  + userReactivity);
		System.out.println("User Mouse Precision: "  + basePerformance.getMousePrecision());
		//System.out.println("User Key Precision: "  + basePerformance.);
		ConformanceChecker cc = new ConformanceChecker(petriNet, eventLog);
		System.out.println("User Trace Fitness: " + cc.getFitness());
		IntraTaskPerformance userITP = cc.getITP();
		
		BasePerformance optPerformance = new BasePerformance(optLog);
		long optReactivity = optPerformance.getReactivity();
		System.out.println("Optimal Reactivity: "  + optReactivity);
		System.out.println("Optimal Mouse Precision: "  + optPerformance.getMousePrecision());
		
		ConformanceChecker optcc = new ConformanceChecker(petriNet, optLog);
		System.out.println("Optimal Fitness: " + optcc.getFitness());
		IntraTaskPerformance optITP = optcc.getITP();
		
		IntraTaskAnalyzer ita = new IntraTaskAnalyzer(userITP, userReactivity, optITP, optReactivity);
		ita.analyze();
		
		
		
		Dictionary dict = new Dictionary();
		
		InterTaskLog itlog = basePerformance.createInterTaskLog();
		List<ArrayList<InterTaskRec>> traces = itlog.getLog();
		
		for(ArrayList<InterTaskRec> trace : traces){
			dict.loadSequence(trace);
		}
		InterTaskLog optItlog = optPerformance.createInterTaskLog();
		traces = optItlog.getLog();
		
		for(ArrayList<InterTaskRec> trace : traces){
			dict.loadSequence(trace);
		}
		
		
		itlog.setDict(dict);
		optItlog.setDict(dict);
		
		

		itlog.translateLog();
		optItlog.translateLog();
	
		
		InterTaskAnalyzer interTaskAnalyzer = new InterTaskAnalyzer(optItlog, itlog, dict);
		interTaskAnalyzer.analyze();
		
		//itlog.translateToFile();
		//System.out.println("************************** Optimal");
		//optItlog.translateToFile();
		
		
		
		
		
		
		
		
		
		
		//System.out.println(basePerformance.getReactivity());
		//System.out.println(basePerformance.getKeysPerTrace());
		//System.out.println(basePerformance.getMousePrecision());
		
		
		//double fitness = cc.getFitness();
		//System.out.println("Fitness: " + fitness);
		
		
		
		
		
		
		//double sba = new ConformanceChecker(petriNet, eventLog).getSimpleBehavioralAppropriateness();
		//double ssa = new ConformanceChecker(petriNet, eventLog).getSimpleStructuralAppropriateness();
		
		
		//System.out.println("Simple Behavioral Appropriateness: " + sba);
		//System.out.println("Simple Structural Appropriateness: " + ssa);
		
		
		
		/*
		double fitness = new ConformanceChecker(petriNet, eventLog).getFitness();
		double sba = new ConformanceChecker(petriNet, eventLog).getSimpleBehavioralAppropriateness();
		double ssa = new ConformanceChecker(petriNet, eventLog).getSimpleStructuralAppropriateness();

		System.out.println("Fitness: " + fitness);
		System.out.println("Simple Behavioral Appropriateness: " + sba);
		System.out.println("Simple Structural Appropriateness: " + ssa);
		*/
    }
}
