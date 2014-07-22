package soapui.strawberry2.jgraph;

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

import soapui.strawberry2.StrawberryUtils;
import soapui.strawberry2.jgraph.Combinatorics;
import soapui.strawberry2.jgraph.ParameterEntry;
import soapui.strawberry2.jgraph.ProtocolAutomatonEdge;
import soapui.strawberry2.jgraph.ProtocolAutomatonVertex;

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
	
	public ProtocolAutomatonVertex getRoot() {
		return root;
	}

	public ProtocolAutomaton(ProtocolAutomatonVertex root) {
		super(new ClassBasedEdgeFactory<ProtocolAutomatonVertex, ProtocolAutomatonEdge>(
				ProtocolAutomatonEdge.class), true, true);
		
		this.root = root;
		//insert the root node that contains the instance pool
		this.addVertex(root);		
	}
	
//	public void automatonConstructionBaseStep(WsdlInterface wsdlInterface) {
//		this.automatonConstructionStep(wsdlInterface, this.root);
//	}
	
	public ProtocolAutomatonVertex automatonConstructionStep(ProtocolAutomatonVertex sourceVertex, OperationAndParameters operationAndParameters) {
	
		WsdlOperation wsdlOperation = operationAndParameters.getOperation();
		//l'operation ha uno o più parametri in input
		OpResponse opResponse;
		if (StrawberryUtils.numberOfInputs(wsdlOperation) > 0) {
			opResponse = StrawberryUtils.requestWithInputs(operationAndParameters);
		}
		//l'operation non ha parametri in input
		else {
			opResponse = StrawberryUtils.requestWithoutInputs(operationAndParameters);
		}

		ProtocolAutomatonVertex targetVertex = this.addNewVertex(opResponse, sourceVertex);
		if (targetVertex != null) {
			this.addEdge(sourceVertex, targetVertex, new ProtocolAutomatonEdge(wsdlOperation, opResponse.getParameterEntry()));
		}
		return targetVertex;
	}
	
	public void automatonResetStep(ProtocolAutomatonVertex sourceVertex, OperationAndParameters operationAndParameters) {
		
		if (operationAndParameters == null) return; 
		
		WsdlOperation wsdlOperation = operationAndParameters.getOperation();
		//l'operation ha uno o più parametri in input
		OpResponse opResponse;
		if (StrawberryUtils.numberOfInputs(wsdlOperation) > 0) {
			opResponse = StrawberryUtils.requestWithInputs(operationAndParameters);
		}
		//l'operation non ha parametri in input
		else {
			opResponse = StrawberryUtils.requestWithoutInputs(operationAndParameters);
		}
			
		Response response = opResponse.getResponse();	
		if (response != null && 
				!StrawberryUtils.isFaultResponse(response, wsdlOperation)) {
			this.addEdge(sourceVertex, this.root, new ProtocolAutomatonEdge(wsdlOperation, opResponse.getParameterEntry()));
		}
	}
	
	public void restart(ProtocolAutomatonVertex vertex) {
		//TODO durante l'operazione di 'restart' assumo che la sequenza di operazione la posso rieseguire correttamente
		//in generale si dovrebbe togliere l'arco se una operazione non va a buon fine quando sarebbe dovuta andarci
		for (OperationAndParameters op : vertex.getOperationAndParameters()) {
			OpResponse opResponse;
			if (op.getParameterEntries() != null) {
				opResponse = StrawberryUtils.requestWithInputs(op);
			}
			else {
				opResponse = StrawberryUtils.requestWithoutInputs(op);
			}
			
			//aggiorniamo la knowledge dello stato con evenutuali nuovi dati derivanti dalle nuove chiamate
			Response response = opResponse.getResponse();
			WsdlOperation wsdlOperation = op.getOperation();
			if (response != null && 
					!StrawberryUtils.isFaultResponse(response, wsdlOperation)) {
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
							vertex.addParameter(schemaType, xmlObject, false);
						}
					}
				}
			}
		}
	}
	
//	public void automatonConstructionStep(WsdlInterface wsdlInterface, ProtocolAutomatonVertex sourceVertex) {
//		
//		List<Operation> operations = wsdlInterface.getOperationList();
//		for (Operation operation : operations) {
//			WsdlOperation wsdlOperation = wsdlInterface.getOperationByName(operation.getName());
//			//è possibile chiamare l'operation da questo stato?
//			if (sourceVertex.canCallOperationFromKnowledge(wsdlOperation)) {
//				//l'operation ha uno o più parametri in input
//				ArrayList<OpResponse> opResponses;
//				if (StrawberryUtils.numberOfInputs(wsdlOperation) > 0) {
//					Generator<ParameterEntry> matchingParams = Combinatorics.combinParams(sourceVertex, wsdlOperation);
//					opResponses = StrawberryUtils.requestsWithInputs(matchingParams, wsdlOperation);
//				}
//				//l'operation non ha parametri in input
//				else {
//					opResponses = StrawberryUtils.requestWithoutInputs(wsdlOperation);
//				}
//				for (OpResponse opResponse : opResponses) {
//					ProtocolAutomatonVertex targetVertex = this.addNewVertex(opResponse, sourceVertex);
//					if (targetVertex != null) {
//						this.addEdge(sourceVertex, targetVertex, new ProtocolAutomatonEdge(wsdlOperation, opResponse.getParameterEntry()));
//					}
//				}
//			}
//		}
//		sourceVertex.setAsVisited();
//	}
	
	//se la risposta è positiva, possiamo aggiungere un nuovo stato (se non esiste già)
	private ProtocolAutomatonVertex addNewVertex (OpResponse opResponse, ProtocolAutomatonVertex sourceVertex) {

		Response response = opResponse.getResponse();
		WsdlOperation wsdlOperation = opResponse.getOperation();
		
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
							targetVertex.addParameter(schemaType, xmlObject, true);
							targetVertex.addOperationAndParameters(opResponse.getOperationAndParameters());
						}
					}
				}
				/*
				ProtocolAutomatonVertex temp = this.getVertex(targetVertex);
				if (temp == null) {
					this.addVertex(targetVertex);
					temp = targetVertex;
				}
				return temp;
				*/
				//TODO qui il codice con le condizioni di aggiunta di un nuovo stato, come è ora aggiungo sempre se incremento la knowledge
				if (!sourceVertex.equals(targetVertex)) {
					this.addVertex(targetVertex);
					return targetVertex;
				}
				else return sourceVertex;
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
