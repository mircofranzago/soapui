package soapui.strawberry.mxgraph;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.xmlbeans.SchemaType;
import org.w3c.dom.Document;

public class ProtocolAutomatonVertex implements Serializable  {

	private static final long serialVersionUID = -68076045116653932L;
	
	private ArrayList<ParameterEntry> parameters; 
	
	public ProtocolAutomatonVertex() {
		super();
		this.parameters = new ArrayList<ParameterEntry>();
	}
	
	public void addParameter(SchemaType schemaType, Document value) {
		this.parameters.add(new ParameterEntry(schemaType, value));
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		//TODO
//		return false;
//	}

}
