package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.uic.prominent.processmining.hcipetrinets.clustering.Dictionary;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Event;

public class InterTaskLog {

	public List<ArrayList<InterTaskRec>> log;
	public List<ArrayList<InterTaskRec>> translatedLog;
	public ArrayList<InterTaskRec> currentTrace;
	
	public List<InterTaskTrace> translatedTraces;
	public List<InterTaskTrace> logTraces;

	public Dictionary dict;
	
	public long prev_time;

	public InterTaskLog() {
		log = new ArrayList<ArrayList<InterTaskRec>>();
		currentTrace = new ArrayList<InterTaskRec>();
		prev_time = 0L;
	}
	
	public void createTraces(){
		translatedTraces = new ArrayList<InterTaskTrace>();
		logTraces = new ArrayList<InterTaskTrace>();
		for(ArrayList<InterTaskRec> trace : translatedLog){
			InterTaskTrace ittrace = new InterTaskTrace(trace);
			translatedTraces.add(ittrace);
		}
		
		for(ArrayList<InterTaskRec> trace : log){
			InterTaskTrace ittrace = new InterTaskTrace(trace);
			logTraces.add(ittrace);
		}
	}
	
	public List<InterTaskTrace> getTranslatedTraces(){
		return translatedTraces;
	}
	
	public List<InterTaskTrace> getTraces(){
		return logTraces;
	}
	

	public void newTrace() {
		currentTrace = new ArrayList<InterTaskRec>();
		prev_time = 0L;
	}

	public void addTrace() {
		log.add(this.currentTrace);
	}

	public void addEvent(Event ev) {
		currentTrace.add(new InterTaskRec(ev.name(), (ev.time() - this.prev_time)));
		this.prev_time = ev.time();
	}

	public List<ArrayList<InterTaskRec>> getLog() {
		return log;
	}

	public void setDict(Dictionary dict) {
		this.dict = dict;
	}

	public void translateLog() {
		translatedLog = new ArrayList<ArrayList<InterTaskRec>>();
		for (ArrayList<InterTaskRec> trace : log) {
			ArrayList<InterTaskRec> transTrace = new ArrayList<InterTaskRec>();

			for (InterTaskRec e : trace) {
				transTrace.add(new InterTaskRec(this.dict.translate(e.getName()), e.getTime()));
			}
			translatedLog.add(transTrace);
		}
	}

	public void translateToFile() {
		try {
			FileWriter writer = new FileWriter("sto1.csv");

			for(ArrayList<InterTaskRec> trace : translatedLog){
				//String collect = trace.stream().collect(Collectors.joining(","));
				//System.out.println(collect);
				String seq = "";
				for(InterTaskRec rec : trace){
					seq += rec.getName() + "(" + rec.getTime() + "),";
				}
				//System.out.println(seq);
				//writer.write(collect);
			}
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
