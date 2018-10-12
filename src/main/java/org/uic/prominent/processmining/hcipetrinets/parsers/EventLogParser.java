package org.uic.prominent.processmining.hcipetrinets.parsers;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Event;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.EventLog;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Trace;

import java.util.Iterator;
import java.util.List;

public class EventLogParser {
	
	public EventLog getEventLogFromFile(String fileName) {
		try {
			return eventLogFromFile(fileName);
		} catch (Exception e) {
			throw new EventLogParseException("Problem with parsing the event log");
		}
	}

	private EventLog eventLogFromFile(String fileName) throws Exception {
		EventLog eventLog = new EventLog();
		List<XTrace> xLog = XLogReader.openLog(fileName);

		for (List<XEvent> xTrace : xLog) {
			
		//	System.out.println(xTrace.toString());
			
			Trace trace = toTrace(xTrace);
			eventLog.addTrace(trace);
		}

		return eventLog;
	}

	private Trace toTrace(List<XEvent> xTrace) {
		Trace trace = new Trace();
		for (XEvent xEvent : xTrace) {
			XAttribute time = xEvent.getAttributes().get("time:timestamp");
			Event event = new Event(name(xEvent), time);
			trace.addEvent(event);
		}
		return trace;
	}

	private String name(XAttributable xAttributable) {
		return XConceptExtension.instance().extractName(xAttributable);
	}

	private class EventLogParseException extends RuntimeException {
		public EventLogParseException(String message) {
			super(message);
		}
	}

}
