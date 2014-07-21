package soapui.strawberry2.jgraph;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;
import org.paukov.combinatorics.ICombinatoricsVector;

import soapui.strawberry2.jgraph.ParameterEntry;

import com.eviware.soapui.impl.wsdl.WsdlOperation;

public class ProtocolAutomatonEdge extends DefaultEdge {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3117073044832869257L;
	
	WsdlOperation operation;
	ICombinatoricsVector<ParameterEntry> parameterEntries;
	
	public ProtocolAutomatonEdge(WsdlOperation operation,
			ICombinatoricsVector<ParameterEntry> parameterEntries) {
		super();
		this.operation = operation;
		this.parameterEntries = parameterEntries;
	}
	
	public String toString() {
		if (operation != null) {
			return operation.getName();
		}
		else return "null";
	}
	
}
