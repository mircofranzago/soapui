package soapui.strawberry.jgraph;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;

import com.eviware.soapui.impl.wsdl.WsdlOperation;

public class ProtocolAutomatonEdge extends DefaultEdge {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3117073044832869257L;
	
	WsdlOperation operation;
	ArrayList<ParameterEntry> parameters;
	
	public ProtocolAutomatonEdge(WsdlOperation operation,
			ArrayList<ParameterEntry> parameters) {
		super();
		this.operation = operation;
		this.parameters = parameters;
	}
	
	public ProtocolAutomatonEdge(WsdlOperation operation) {
		super();
		this.operation = operation;
	}
	
	public String toString() {
		if (operation != null) {
			return operation.getName();
		}
		else return "null";
	}
	
}
