package soapui.strawberry.jgraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.paukov.combinatorics.Generator;
import org.w3c.dom.Node;

import soapui.strawberry.StrawberryUtils;

import com.eviware.soapui.model.iface.MessagePart;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.MessagePart.ContentPart;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;

public class ProtocolAutomaton extends AbstractBaseGraph<ProtocolAutomatonVertex, ProtocolAutomatonEdge>
								implements DirectedGraph<ProtocolAutomatonVertex, ProtocolAutomatonEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2801741081358753349L;
	
	private ProtocolAutomatonVertex root;
	
	public ProtocolAutomaton(ProtocolAutomatonVertex root) {
		super(new ClassBasedEdgeFactory<ProtocolAutomatonVertex, ProtocolAutomatonEdge>(
				ProtocolAutomatonEdge.class), true, true);
		
		this.root = root;
		//insert the root node that contains the instance pool
		this.addVertex(root);		
	}
	
	public void automatonConstructionBaseStep(WsdlInterface wsdlInterface) {
		this.automatonConstructionStep(wsdlInterface, this.root);
	}
	
	public void automatonConstructionStep(WsdlInterface wsdlInterface, ProtocolAutomatonVertex sourceVertex) {
		
		List<Operation> operations = wsdlInterface.getOperationList();
		for (Operation operation : operations) {
			WsdlOperation wsdlOperation = wsdlInterface.getOperationByName(operation.getName());
			//è possibile chiamare l'operation da questo stato?
			if (sourceVertex.canCallOperationFromKnowledge(wsdlOperation)) {
				//l'operation ha uno o più parametri in input
				ArrayList<Response> responses;
				if (StrawberryUtils.numberOfInputs(wsdlOperation) > 0) {
					Generator<ParameterEntry> matchingParams = Combinatorics.combinParams(sourceVertex, wsdlOperation);
					responses = StrawberryUtils.requestsWithInputs(matchingParams, wsdlOperation);
				}
				//l'operation non ha parametri in input
				else {
					responses = StrawberryUtils.requestWithoutInputs(wsdlOperation);
				}
				for (Response response : responses) {
					ProtocolAutomatonVertex targetVertex = this.addNewVertex(response, wsdlOperation, sourceVertex);
					if (targetVertex != null) {
						this.addEdge(sourceVertex, targetVertex, new ProtocolAutomatonEdge(wsdlOperation));
					}
				}
			}
		}
		sourceVertex.setAsVisited();
	}
	
	//se la risposta è positiva, possiamo aggiungere un nuovo stato (se non esiste già)
	private ProtocolAutomatonVertex addNewVertex (Response response, WsdlOperation wsdlOperation, ProtocolAutomatonVertex sourceVertex) {

		if (response != null) {
			if (!StrawberryUtils.isFaultResponse(response, wsdlOperation)) {
				System.err.println("non fault: " + wsdlOperation.getName());
				//la richiesta è andata a buon fine, possiamo aggiungere lo stato e la transizione
				ProtocolAutomatonVertex targetVertex = new ProtocolAutomatonVertex(sourceVertex);
				//TODO assumiamo per ora che sia solo "additivo" (tutte le operazioni arricchiscono la knowledge base
				MessagePart[] messageParts = wsdlOperation.getDefaultResponseParts();
				//scorro gli output dell'operazione
				for (int i = 0; i < messageParts.length; i++) {
					if (messageParts[i] instanceof ContentPart) {
						SchemaType schemaType = ((ContentPart) messageParts[i]).getSchemaType();
						String outputPartName = ((ContentPart) messageParts[i]).getName();
						XmlObject xmlObject = StrawberryUtils.getNodeFromResponse(response, outputPartName);
						if (xmlObject != null) {
							//aggiungo alla knowledge base attuale
							targetVertex.addParameter(schemaType, xmlObject);
						}
					}
				}
				ProtocolAutomatonVertex temp = this.getVertex(targetVertex);
				if (temp == null) {
					this.addVertex(targetVertex);
					temp = targetVertex;
				}
				return temp;
			}
		}
		return null;
	}
	
	public boolean existVertex (ProtocolAutomatonVertex vertex) {
		Set<ProtocolAutomatonVertex> set = this.vertexSet();
		for (ProtocolAutomatonVertex currVertex : set) {
			if (currVertex.equals(vertex)) {
				return true;
			}
		}
		return false;
	}
	
	public ProtocolAutomatonVertex getVertex (ProtocolAutomatonVertex vertex) {
		Set<ProtocolAutomatonVertex> set = this.vertexSet();
		Iterator<ProtocolAutomatonVertex> iterator = set.iterator();
		while (iterator.hasNext()) {
			ProtocolAutomatonVertex curr = iterator.next();
			if (curr.equals(vertex))
				return curr; 
		}
		return null;
	}
	
	public ProtocolAutomatonVertex getNextNonVisited () {
		Set<ProtocolAutomatonVertex> set = this.vertexSet();
		for (ProtocolAutomatonVertex vertex : set) {
			if (!vertex.isVisited()) {
				return vertex;
			}
		}
		return null;
	}

}
