package org.uic.prominent.processmining.hcipetrinets.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.Pnml;
import org.uic.prominent.processmining.hcipetrinets.domain.petrinet.PetriNet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PetriNetParser {
	private PetriNet petriNet;
	private Document doc;

	public PetriNet getPetriNetFromFile(String fileName) {
		
		File f = new File (fileName);
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(f);
		}catch(Exception e){
			System.out.println(e);
		}
		
		
		

		PnmlImportUtils pnmlImportUtils = new PnmlImportUtils();

		InputStream input = null;
		try {
			input = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Pnml pnml = null;
		try {
			pnml = pnmlImportUtils.importPnmlFromStream(input, f.getName(), f.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		PetrinetGraph net = PetrinetFactory.newInhibitorNet(pnml.getLabel() + " (imported from " + f.getName() + ")");
		Marking marking = new Marking();
		pnml.convertToNet(net, marking, new GraphLayoutConnection(net));

		petriNet = new PetriNet();

		Iterator<Place> iter = marking.iterator();
		Map<String, Integer> initMarking = new HashMap<String, Integer>();
		Map<String, Integer> finalMarking = finalMarking();
		
		
		while(iter.hasNext()){
			Place p = iter.next();
			initMarking.put(p.getLabel(), initMarkingOfPlace(p.getLabel()));
		}
		
		for (Place place : net.getPlaces()) {
			org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Place p = null;
			if(initMarking.containsKey(place.getLabel())){
				p = new org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Place(place.getLabel(), initMarking.get(place.getLabel()));
			}else{
				p = new org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Place(place.getLabel(), 0);
			}
			
			if(finalMarking.containsKey(place.getLabel())){
				p.setFinalTokens(finalMarking.get(place.getLabel()));
			}
			petriNet.addPlace(p);
		}

		for (Transition transition : net.getTransitions()) {
			//boolean visible = true;
			//if(transition.isInvisible())
			//	visible = false;
			
			boolean visible = transitionVisible(transition);
			
			org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Transition t = new org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Transition(transition.getLabel(), visible);
			petriNet.addTransition(t);
		}

		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getEdges()) {
			String fromName = edge.getSource().getLabel();
			String toName = edge.getTarget().getLabel();

			org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Transition fromTransition = petriNet.getTransition(fromName);
			org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Place toPlace = petriNet.getPlace(toName);

			fromTransition.to(toPlace);
			toPlace.from(fromTransition);

			org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Place fromPlace = petriNet.getPlace(fromName);
			org.uic.prominent.processmining.hcipetrinets.domain.petrinet.Transition toTransition = petriNet.getTransition(toName);

			fromPlace.to(toTransition);
			toTransition.from(fromPlace);
		}		

		return petriNet;
	}
	
	private int initMarkingOfPlace(String place){
		int markings = 0;
		NodeList places = doc.getElementsByTagName("place");
		for (int i = 0; i < places.getLength(); i++) {
			Node item = places.item(i);
			
			if (item.getAttributes().getNamedItem("id").toString().equals("id=\"" + place + "\"")){
				if(item.getChildNodes().getLength() > 0){
					try{
						markings = Integer.parseInt(item.getChildNodes().item(0).getChildNodes().item(0).getFirstChild().getNodeValue());
					}catch(Exception e){
						markings = 1;
					}
				}
				break;
			}
		}
		return markings;
	}
	
	private Map<String, Integer> finalMarking(){
		Map<String, Integer> finalMarking = new HashMap<String, Integer>();
		Node finalMarkings = doc.getElementsByTagName("finalmarkings").item(0);

		if(finalMarkings.hasChildNodes()){
			Node markings = finalMarkings.getFirstChild();
			for (int i = 0; i < markings.getChildNodes().getLength(); i++) {
				Node marking = markings.getChildNodes().item(i);
				String place = marking.getAttributes().getNamedItem("idref").getNodeValue();
				
				int value = 0;
				try{
					value = Integer.parseInt(marking.getFirstChild().getFirstChild().getNodeValue());
				}catch(Exception e){
					value = 1;
				}
				finalMarking.put(place, value);
			}
		}
		return finalMarking;
	}
	
	
	
	private boolean transitionVisible(Transition t){
		boolean visible = true;
		
		String label = t.getLabel();
		
		boolean isT = false;
		if(label.length() >= 1){
			isT = label.substring(0, 1).equals("t");
		}
		
		boolean isNum;
		try{
			Integer.parseInt(label.substring(1));
			isNum = true;
		}catch(Exception e){
			isNum = false;
		}
		
		if(isT || isNum)
			visible = false;
		
		return visible;
	}
}
