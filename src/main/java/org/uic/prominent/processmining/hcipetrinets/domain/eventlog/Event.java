package org.uic.prominent.processmining.hcipetrinets.domain.eventlog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.model.XAttribute;

public final class Event {
	private final String name;
	private long time;

	public Event(String name, XAttribute tt) {
		this.name = name;
		this.time = 0L;
		
		String timeString = tt.toString();
		
		// Check if .000 is missing. Add it if true
			int count = StringUtils.countMatches(timeString, ".");
			if(count == 0){
				String[] timeStringSplit = timeString.split("\\-"); 
				timeString = timeStringSplit[0] + "-" + timeStringSplit[1] + "-" + timeStringSplit[2] + ".000-" + timeStringSplit[3];
			}	
		//---->
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
		try {
		    Date d = f.parse(timeString);
		    this.time = d.getTime();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public String name() {
		return name;
	}
	
	public long time() {
		return time;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Event event = (Event) o;
		return Objects.equals(name, event.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name;
	}
}
