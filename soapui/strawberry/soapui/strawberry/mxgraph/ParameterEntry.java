package soapui.strawberry.mxgraph;

import org.apache.xmlbeans.SchemaType;
import org.w3c.dom.Document;

public class ParameterEntry {
	SchemaType schemaType;
	Document value;
	
	public ParameterEntry(SchemaType schemaType, Document value) {
		this.schemaType = schemaType;
		this.value = value;
	}
	
}
