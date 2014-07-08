package soapui.strawberry.jgraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import soapui.strawberry.StrawberryUtils;
import soapui.strawberry.jgraph.OperationType;

import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.model.iface.Operation;

public class ProtocolAutomatonVertex {
	
	private ArrayList<ParameterEntry> parameters; 
	
	private boolean visited;
	
	public ArrayList<ParameterEntry> getParameters() {
		return parameters;
	}

	public ProtocolAutomatonVertex() {
		super();
		this.parameters = new ArrayList<ParameterEntry>();
		visited = false;
	}
	
	public ProtocolAutomatonVertex(ProtocolAutomatonVertex old) {
		super();
		this.parameters = new ArrayList<ParameterEntry>(old.getParameters());
		visited = false;
	}
	
	public void addParameter(SchemaType schemaType, XmlObject value) {
		ParameterEntry newParameter = new ParameterEntry(schemaType, value);
		if (!this.parameters.contains(newParameter))
			this.parameters.add(newParameter);
	}
	
	//restituisce tutti gli schema types delle entry dello stato
	public ArrayList<SchemaType> getSchemaTypes() {
		ArrayList<SchemaType> schemaTypes = new ArrayList<SchemaType>();
		for(ParameterEntry curr : parameters) {
			schemaTypes.add(curr.schemaType);
		}
		return schemaTypes;
	}
	
	//vedere se è possibile chiamare l'operation da questo stato
	public boolean canCallOperationFromKnowledge(WsdlOperation wsdlOperation) {
		ArrayList<SchemaType> schemaTypes = StrawberryUtils.getInputSchemaTypesByOperation(wsdlOperation);
		for (SchemaType curr : schemaTypes) {
			if (!existSchemaType(curr)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean existSchemaType (SchemaType schemaType) {
		for (ParameterEntry temp : parameters) {
			if (temp.schemaType.equals(schemaType)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object vertex) {
 
		if (!this.getParameters().containsAll(((ProtocolAutomatonVertex)vertex).getParameters())) {
			return false;
		}
		if (!((ProtocolAutomatonVertex)vertex).getParameters().containsAll(this.getParameters())) {
			return false;
		}
		return true;
	}
	
	public void setAsVisited() {
		this.visited = true;
	}
	
	public boolean isVisited() {
		return this.visited;
	}
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("< ");
		for (ParameterEntry paramEntry : parameters) {
			//stringBuilder.append(paramEntry.schemaType.getName().getLocalPart() + " - ");
			if (paramEntry.value.xmlText().length() > 50)
				stringBuilder.append(paramEntry.value.xmlText().substring(0, 70) + " : "+ paramEntry.schemaType.getName().getLocalPart() + " | ");
			else 
				stringBuilder.append(paramEntry.value.xmlText() + " : "+ paramEntry.schemaType.getName().getLocalPart() + " | ");

			//stringBuilder.append(paramEntry.value.getDomNode().getFirstChild().getFirstChild().toString().substring(0, 13) + " : "+ paramEntry.schemaType.getName().getLocalPart() + " | ");
		}
		stringBuilder.append(" >\n");
		return stringBuilder.toString();
	}

}
