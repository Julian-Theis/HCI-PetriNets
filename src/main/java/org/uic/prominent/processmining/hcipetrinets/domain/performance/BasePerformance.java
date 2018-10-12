package org.uic.prominent.processmining.hcipetrinets.domain.performance;

import java.util.ArrayList;
import java.util.List;

import org.uic.prominent.processmining.hcipetrinets.domain.conformance.ConformanceParameters;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.Event;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.EventLog;

public class BasePerformance {

	private EventLog eventLog;

	public BasePerformance(EventLog eventLog) {
		this.eventLog = eventLog;
	}
	
	public InterTaskLog createInterTaskLog(){
		InterTaskLog itLog = new InterTaskLog();
		eventLog.forEach((trace, count) -> {
			//int hash = System.identityHashCode(trace);
			//System.out.println(hash);
			trace.forEach((action) -> {
				//InterTaskTrace ittrace = new InterTaskTrace(itLog);
				if (!action.name().contains("key") && !action.name().contains("mouse")) {
					//System.out.print(action.name());
					itLog.addEvent(action);
				} 
			});
			//System.out.println();
			itLog.addTrace();
			itLog.newTrace();
			
		});
		
		return itLog;
	}

	public long getReactivity() {
		MeasurementController mctrl = new MeasurementController();
		eventLog.forEach((trace, count) -> {
			trace.forEach((action) -> {
				if (action.name().contains("key") || action.name().contains("mouse")) {
					long time = action.time();

					if (mctrl.getLastAction()) {
						// System.out.println(differ);
						mctrl.addMeasurement(time - mctrl.getLastTime());
					}

					mctrl.setLastAction(true);
					mctrl.setLastTime(time);
				} else {
					mctrl.setLastAction(false);
				}
			});
		});
		return mctrl.getAverage();
	}

	public double getKeysPerTrace() {
		KeyController kctrl = new KeyController();
		eventLog.forEach((trace, cnt) -> {
			String str = trace.toString();
			String findStr = "key";
			int lastIndex = 0;
			int count = 0;
			while (lastIndex != -1) {
				lastIndex = str.indexOf(findStr, lastIndex);
				if (lastIndex != -1) {
					count++;
					lastIndex += findStr.length();
				}
			}
			kctrl.addCount(count);
		});
		return kctrl.getAverage();
	}
	
	public double getMousePrecision() {
		MouseController mctrl = new MouseController();
		eventLog.forEach((trace, cnt) -> {
			MousePrecision prec = null;
			boolean lastEventWasMouse = false;
			
			String str = trace.toString();
			String[] array = str.split(", ");
			for(String s : array){
				if(s.contains("mouse to")){
					String first = s.split(",")[0];
					int x = Integer.parseInt(first.charAt(first.length()-1) + "");
					int y = Integer.parseInt(s.split(",")[1].charAt(0) + "");
					
					if(!lastEventWasMouse){
						prec = new MousePrecision(x, y);
					}else{
						prec.add(x, y);
					}
					lastEventWasMouse = true;
				}else{
					if(lastEventWasMouse){
						prec.calculate();
						mctrl.add(prec.getActual(), prec.getShortest());
					}
					lastEventWasMouse = false;
				}
			}
			
			

		});
		return mctrl.getPrecision();
	}

	public class MeasurementController {
		boolean lastAction = false;
		Long lastTime = null;
		List<Long> times = new ArrayList<Long>();

		public MeasurementController() {
		}

		public void addMeasurement(Long time) {
			times.add(time);
		}

		public boolean getLastAction() {
			return lastAction;
		}

		public void setLastAction(boolean t) {
			lastAction = t;
		}

		public Long getLastTime() {
			return lastTime;
		}

		public void setLastTime(long t) {
			lastTime = t;
		}

		public Long getAverage() {
			int sum = 0;
			for (int i = 0; i < times.size(); i++) {
				sum += times.get(i);
			}
			return (long) (sum / times.size());
		}

	}

	public class KeyController {
		List<Integer> counts = new ArrayList<Integer>();

		public KeyController() {
		}

		public void addCount(Integer cnt) {
			counts.add(cnt);
		}

		public Double getAverage() {
			int sum = 0;
			for (int i = 0; i < counts.size(); i++) {
				sum += counts.get(i);
			}
			return (double) ((double) sum / counts.size());
		}
	}

	public class MousePrecision{
		int startx;
		int starty;
		int endx;
		int endy;
		List<Integer> xs;
		List<Integer> ys;
		
		int shortest;
		int actual;
		
		boolean longEnough = false;

		public MousePrecision(int x, int y) {
			this.xs = new ArrayList<Integer>();
			this.ys = new ArrayList<Integer>();
			this.startx = x;
			this.starty = y;
		}
		
		public void add(int x, int y){
			this.xs.add(x);
			this.ys.add(y);
		}
		
		public void calculate(){
			
			if(xs.size() > 1){
				longEnough = true;
				actual = xs.size();
				endx = xs.get(xs.size()-1);
				endy = ys.get(ys.size()-1);
				shortest = Math.abs(startx - endx) + Math.abs(starty - endy);
				//System.out.println(shortest + " " + actual);
			}
		}
		
		public boolean isLongEnough(){
			return longEnough;
		}
		
		public int getActual(){
			return actual;
		}
		
		public int getShortest(){
			return shortest;
		}
		
		
	}

	public class MouseController {
		List<Integer> actuals = new ArrayList<Integer>();
		List<Integer> shortests = new ArrayList<Integer>();
		
		public MouseController() {
		}

		public void add(Integer actual, Integer shortest) {
			actuals.add(actual);
			shortests.add(shortest);
		}

		public Double getPrecision() {
			int sumActuals = 0;
			int sumShortests = 0;
			for (int i = 0; i < actuals.size(); i++) {
				sumActuals += actuals.get(i);
				sumShortests += shortests.get(i);
			}
			return (double) ((double) sumShortests / sumActuals);
		}
	}
}
