package org.uic.prominent.processmining.hcipetrinets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.uic.prominent.processmining.hcipetrinets.astar.AStarInvisible;
import org.uic.prominent.processmining.hcipetrinets.astar.AStarSearch;
import org.uic.prominent.processmining.hcipetrinets.astar.Node;
import org.uic.prominent.processmining.hcipetrinets.domain.conformance.ConformanceParameters;
import org.uic.prominent.processmining.hcipetrinets.domain.eventlog.EventLog;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.IntraTaskPerformance;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.PerfMeasurement;
import org.uic.prominent.processmining.hcipetrinets.domain.performance.Performance;
import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.PetriNet;
import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Place;
import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Transition;

public class ConformanceChecker {
	private PetriNet petriNet;
	private EventLog eventLog;
	private AStarSearch aStar;
	private AStarInvisible aStarInvisible;

	private IntraTaskPerformance intraTaskPerf = new IntraTaskPerformance();

	List<ConformanceParameters> conformanceParams = new ArrayList<>();

	public ConformanceChecker(PetriNet petriNet, EventLog eventLog) {
		this.petriNet = petriNet;
		this.eventLog = eventLog;
		this.aStar = new AStarSearch(this.petriNet);
		this.aStarInvisible = new AStarInvisible(this.petriNet);
	}

	public double getFitness() {
		replayLog();
		return calculateFitness();
	}

	public double getSimpleBehavioralAppropriateness() {
		replayLog();
		return calculateSimpleBehavioralAppropriateness();
	}

	public double getSimpleStructuralAppropriateness() {
		double L = petriNet.countTransitions();
		double N = L + petriNet.countPlaces();
		return (L + 2) / N;
	}

	void replayLog() {

		Performance perf = new Performance();
		eventLog.forEach((trace, count) -> {
			// System.out.println("Trace");

			ConformanceParameters params = new ConformanceParameters(trace, count);

			List<PerfMeasurement> measurements = replay(params);
			perf.addMeasurements(measurements);

			conformanceParams.add(params);
		});

		perf.calculateStatistics();

	}

	private List<PerfMeasurement> replay(ConformanceParameters params) {
		addStartToken(params);
		List<PerfMeasurement> measurements = replayEvents(params);

		consumeEndToken(params);
		setRemainingTokens(params);

		return measurements;
	}

	private void setRemainingTokens(ConformanceParameters params) {
		int remainingTokens = petriNet.countRemainingTokens();
		params.setRemaining(remainingTokens);
		petriNet.cleanUpRemainingTokens();
	}

	private void consumeEndToken(ConformanceParameters params) {
		if (!petriNet.hasEndToken()) {
			petriNet.addEndToken();
			params.incrementMissing();
		}
		petriNet.removeEndToken();
		params.incrementConsumed();
	}

	private List<Transition> reachableByInvisible(Place target) {
		List<Transition> returnList = null;

		List<Node> invisiblePath = this.aStarInvisible.searchPath(petriNet.getPlacesWithToken().get(0).name(),
				target.name());
		boolean isReachable = false;
		if (invisiblePath.size() > 1)
			isReachable = true;

		if (isReachable) {
			returnList = new ArrayList<Transition>();

			for (Node n : invisiblePath) {
				if (n.isTransition) {
					returnList.add(this.petriNet.getTransition(n.value));
				}
			}
		}

		/*
		 * List<Place> current = petriNet.getPlacesWithToken();
		 * 
		 * boolean hasInvisible = true; boolean found = false;
		 * 
		 * while(hasInvisible && !found){ for(Place p : current){
		 * Iterator<Transition> iterator = p.getOutputs().iterator();
		 * while(iterator.hasNext()){ Transition t = iterator.next();
		 * if(!t.isVisible()){ if(t.getOutputs().contains(target)){
		 * current.add(target); found = true; }
		 * 
		 * } } } }
		 */
		return returnList;
	}

	private List<PerfMeasurement> replayEvents(ConformanceParameters params) {
		Map<Transition, Long> enabledTransitions = new HashMap<Transition, Long>();
		List<PerfMeasurement> measurements = new ArrayList<PerfMeasurement>();

		intraTaskPerf.traceCount();
		String startName = this.petriNet.start().name();

		params.trace().forEach(event -> {
			
			List<Place> lst = this.petriNet.getPlacesWithToken();
			boolean found = false;
			for (Place ls : lst) {
				if (ls.name().equals(startName)) {
					found = true;
					break;
				}
			}
			if (found) {
				intraTaskPerf.tokenInStart("START");
			}

			
			// System.out.println("------------------------------");

			// System.out.println(" ------- Incoming Event " + event.name());
			Transition transition = petriNet.getTransition(event.name());

			intraTaskPerf.addTransition(transition);

			if (!enabledTransitions.containsKey(transition)) {
				List<Transition> invisibleTransitions = reachableByInvisible(transition.getOutputs().iterator().next());

				if (invisibleTransitions != null) {
					// it is reachable by invisible path, so put token out
					// output!
					Place n = transition.getInputs().iterator().next();
					//System.out.println("Clean Up and Add");
					this.petriNet.cleanUpRemainingTokens();
					this.petriNet.addToken(n);
					//System.out.println(this.petriNet.countRemainingTokens());
					

					// Which transitions have been disabled
					Map<Transition, Long> shallowCopy = (Map<Transition, Long>) ((HashMap<Transition, Long>) enabledTransitions)
							.clone();
					for (Transition t : shallowCopy.keySet()) {
						if (!t.hasAllInputTokens()) {
							enabledTransitions.remove(t);
							// System.out.println("Not enabled anymore: " +
							// t.name());
						}
					}

					// Which transitions have been enabled now?
					List<Transition> newlyEnabled = petriNet.enabledTransitions();
					for (Transition t : newlyEnabled) {
						enabledTransitions.put(t, event.time());
						// System.out.println("Now enabled: " + t.name() + " at
						// time " + event.time());
					}

				} else {
					// System.out.println("NOT REACHABLE");
				}

			}

			if (enabledTransitions.containsKey(transition)) {
				// System.out.println("ENABLED");
				long firingTime = event.time() - enabledTransitions.get(transition).longValue();
				// System.out.println(event.time());
				// System.out.println(enabledTransitions.get(transition).longValue());
				measurements.add(new PerfMeasurement(event.name(), firingTime));
				// System.out.println("Firing Time for " + event.name() + ": " +
				// firingTime);
			} else {
				// System.out.println("NOT ENABLED");
			}
			enabledTransitions.remove(transition);
			enabledTransitions.put(transition, event.time());

			addEnabledTransition(params);

			
			createMissingTokensIfNeeded(transition, params);
			//System.out.println(this.petriNet.countRemainingTokens());
			
			//System.out.println("How many trans are enabled?" + enabledTransitions.size());
			consumeInputTokens(transition, params);
			//System.out.println(this.petriNet.countRemainingTokens());
			
			produceOutputTokens(transition, params);
			//System.out.println(this.petriNet.countRemainingTokens());
			
			//System.out.println("------------------------------");

			// Which transitions have been disabled
			Map<Transition, Long> shallowCopy = (Map<Transition, Long>) ((HashMap<Transition, Long>) enabledTransitions)
					.clone();
			for (Transition t : shallowCopy.keySet()) {
				if (!t.hasAllInputTokens()) {
					enabledTransitions.remove(t);
					// System.out.println("Not enabled anymore: " + t.name());
				}
			}

			// Which transitions have been enabled now?
			List<Transition> newlyEnabled = petriNet.enabledTransitions();
			for (Transition t : newlyEnabled) {
				enabledTransitions.put(t, event.time());
				// System.out.println("Now enabled: " + t.name() + " at time " +
				// event.time());
			}
		});

		return measurements;
	}

	public void analyzeIntraTaskPerformance() {
		intraTaskPerf.analyze();
	}

	public IntraTaskPerformance getITP() {
		return intraTaskPerf;
	}

	private void addEnabledTransition(ConformanceParameters params) {
		params.addEnabledTransition(petriNet.countEnabledTransitions());
	}

	private void createMissingTokensIfNeeded(Transition transition, ConformanceParameters params) {
		if (!transition.hasAllInputTokens()) {

			// how far do we have to go? Then, increment missing by how far we
			// had to go!
			List<Place> currentTokens = petriNet.getPlacesWithToken();
			Place currentToken = currentTokens.get(0);

			Place[] targetPlaces = transition.getInputs().toArray(new Place[transition.getInputs().size()]);
			Place targetPlace = targetPlaces[0];

			// System.out.println(currentToken.name() + " --- " +
			// targetPlace.name());

			/*
			 * int forwardPass = aStarF.search(currentToken.name(),
			 * transition.name()); int backwardPass =
			 * aStarB.search(currentToken.name(), transition.name());
			 * System.out.println("Forward " + forwardPass + " -- Backward " +
			 * backwardPass); int incrementBy = forwardPass; if(forwardPass < 1
			 * && backwardPass > 0){ incrementBy = backwardPass; }else
			 * if(forwardPass > backwardPass && backwardPass > 0){ incrementBy =
			 * backwardPass; }
			 */

			int incrementBy = aStar.search(currentToken.name(), targetPlace.name());
			//System.out.println("Increment By " + incrementBy);
			if(incrementBy > 0){
				petriNet.cleanUpRemainingTokens();
				// System.out.println("Create Missing Token " + transition.name() +
				// " incrementBy " + incrementBy );
				transition.createMissingToken();
				params.incrementMissing(incrementBy);
			}
		}

		// System.out.println(petriNet.countRemainingTokens());
	}

	private void produceOutputTokens(Transition transition, ConformanceParameters params) {
		transition.produceOutputTokens();
		params.incrementProduced();
	}

	private void consumeInputTokens(Transition transition, ConformanceParameters params) {
		transition.consumeInputTokens();
		params.incrementConsumed();
	}

	private void addStartToken(ConformanceParameters params) {
		petriNet.addStartToken();
		params.incrementProduced();
	}

	private double calculateFitness() {
		double missing = 0;
		double remaining = 0;
		double consumed = 0;
		double produced = 0;

		for (ConformanceParameters p : conformanceParams) {

			missing += p.count() * p.missing();
			remaining += p.count() * p.remaining();
			consumed += p.count() * p.consumed();
			produced += p.count() * p.produced();
			// System.out.println(p.count());
		}

		// System.out.println("Missing: " + missing);
		// System.out.println("remaining: " + remaining);
		// System.out.println("consumed: " + consumed);
		// System.out.println("produced: " + produced);

		return 0.5 * (1 - (missing / consumed)) + 0.5 * (1 - (remaining / produced));
	}

	private double calculateSimpleBehavioralAppropriateness() {
		int Tv = petriNet.countTransitions();
		double sum1 = 0;
		double sum2 = 0;

		for (ConformanceParameters p : conformanceParams) {
			int ni = p.count();
			double xi = p.getMeanEnabledTransitions();

			sum1 += ni * (Tv - xi);
			sum2 += ni;
		}

		return sum1 / ((Tv - 1) * sum2);
	}

}
