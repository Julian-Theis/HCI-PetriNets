package org.uic.prominent.processmining.hcipetrinets.domain.petrinet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.addAll;

public class Transition {
	public static final Transition NULL = new Transition("null");

	private final String name;
	private Set<Place> inputs = new HashSet<>();
	private Set<Place> outputs = new HashSet<>();
	private boolean visible;

	public Transition(String name) {
		this.name = name;
		this.visible = true;
	}
	
	public Transition(String name, boolean visible) {
		this.name = name;
		this.visible = visible;
	}
	
	public boolean isVisible(){
		return this.visible;
	}

	public String name() {
		return name;
	}

	public Transition from(Place... places) {
		addAll(inputs, places);
		return this;
	}

	public Transition to(Place... places) {
		addAll(outputs, places);
		return this;
	}
	
	public Set<Place> getOutputs(){
		return outputs;
	}
	
	public Set<Place> getInputs(){
		return inputs;
	}

	public void consumeInputTokens() {
		inputs.forEach(Place::removeToken);
	}

	public void produceOutputTokens() {
		outputs.forEach(Place::addToken);
	}

	public void createMissingToken() {
		inputs.stream().filter(input -> !input.hasTokens()).forEach(Place::addToken);
	}

	public boolean hasAllInputTokens() {
		return inputs.stream().allMatch(Place::hasTokens);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transition that = (Transition) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean isControllable() {
		boolean isControllable = false;
		
		if(this.name.contains("mouse ") || this.name.contains("key ")) {
			isControllable = true;
		}
		return isControllable;
	}
	
	
}
