package org.uic.prominent.processmining.hcipetrinets.domain.petrinet;

import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections15.IteratorUtils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;

public class PetriNet implements Cloneable {
	private Map<String, Place> places = new HashMap<>();
	private Map<String, Transition> transitions = new HashMap<>();

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void addPlace(Place... places) {
		this.places.putAll(stream(places).collect(toMap(Place::name, place -> place)));
	}

	public void addTransition(Transition... transitions) {
		this.transitions.putAll(stream(transitions).collect(toMap(Transition::name, transition -> transition)));
	}

	public Place start() {
		return places.values().stream().filter(Place::hasZeroInputs).findFirst().get();
	}
	
	public List<Place> initialMarking() {
		Iterator<Place> iter = places.values().stream().filter(Place::hasZeroInputs).iterator();
		List<Place> myList = IteratorUtils.toList(iter);  
		return myList;
	}


	public Place end() {
		return places.values().stream().filter(Place::hasZeroOutputs).findFirst().get();
	}
	
	public List<Place> finalMarking() {
		Iterator<Place> iter = places.values().stream().filter(Place::hasZeroOutputs).iterator();
		List<Place> myList = IteratorUtils.toList(iter);  
		return myList;
	}

	public int countRemainingTokens() {
		return places.values().stream().mapToInt(Place::getTokenCount).sum();
	}

	public void cleanUpRemainingTokens() {
		places.values().forEach(Place::removeAllTokens);
	}

	public void removeToken(Place p) {
		p.removeToken();
	}

	public List<Place> getPlaces() {
		return places.values().stream().collect(Collectors.toList());
	}

	public List<Transition> getTransitions() {
		return transitions.values().stream().collect(Collectors.toList());
	}

	/** JULIAN **/
	public List<Transition> enabledTransitions() {
		List<Place> x = places.values().stream().filter(Place::hasTokens).collect(Collectors.toList());

		List<Transition> enabledTransitions = new ArrayList<Transition>();

		for (Place p : x) {
			Iterator<Transition> outputs = p.getOutputs().iterator();
			while (outputs.hasNext()) {
				Transition trans = outputs.next();
				if (trans.hasAllInputTokens()) {
					enabledTransitions.add(trans);
				}
			}
		}
		return enabledTransitions;
	}

	/** JULIAN **/
	public List<Place> getPlacesWithToken() {
		return places.values().stream().filter(Place::hasTokens).collect(Collectors.toList());
	}

	/** JULIAN **/
	public void measureDistance(Place a, Transition t) {

	}

	public int countEnabledTransitions() {
		return places.values().stream().filter(Place::hasTokens).mapToInt(Place::getOutputCount).sum();
	}

	public int countTransitions() {
		return transitions.size();
	}

	public int countPlaces() {
		return places.size();
	}
	
	

	public void addStartToken() {
		start().addToken();
	}

	public void addToken(Place p) {
		p.addToken();
	}

	public boolean hasEndToken() {
		return end().hasTokens();
	}
	
	public boolean finalMarkingReached() {
		boolean reached = true;
		Iterator<Place> finalMarkingPlaces = places.values().stream().filter(Place::hasFinalMarking).iterator();
		while(finalMarkingPlaces.hasNext()){
			Place place = finalMarkingPlaces.next();
			if(place.getFinalMarking() > place.getTokenCount()){
				reached = false;
				break;
			}
		}
		return reached;
	}

	public void removeEndToken() {
		end().removeToken();
	}

	public void addEndToken() {
		end().addToken();
	}

	public Transition getTransition(String name) {
		return transitions.get(name) != null ? transitions.get(name) : Transition.NULL;
	}

	public Place getPlace(String name) {
		return places.get(name) != null ? places.get(name) : Place.NULL;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("places", places).add("transitions", transitions).toString();
	}

	public void addStartTokens() {
		 Iterator<Place> initMarking = places.values().stream().filter(Place::hasInitMarking).iterator();
		 while(initMarking.hasNext()){
			 Place p = initMarking.next();
			 p.addTokens(p.initTokens());
		 }
		
		
	}
}
