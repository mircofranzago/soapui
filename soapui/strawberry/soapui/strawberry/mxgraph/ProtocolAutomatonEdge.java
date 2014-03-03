package soapui.strawberry.mxgraph;

import java.util.ArrayList;

import com.eviware.soapui.impl.wsdl.WsdlOperation;

public class ProtocolAutomatonEdge {
	
	WsdlOperation operation;
	ArrayList<ParameterEntry> parameters;
	
	public ProtocolAutomatonEdge(WsdlOperation operation,
			ArrayList<ParameterEntry> parameters) {
		this.operation = operation;
		this.parameters = parameters;
	}
	
}
