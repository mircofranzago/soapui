package soapui.strawberry2.jgraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import soapui.strawberry2.StrawberryUtils;
import soapui.strawberry2.jgraph.OperationType;
import soapui.strawberry2.jgraph.ParameterEntry;
import soapui.strawberry2.jgraph.ProtocolAutomatonVertex;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.model.iface.Operation;

public class ProtocolAutomatonVertex {
	
	public WsdlInterface wsdlInterface;
	private ArrayList<ParameterEntry> parameters; //knowledge
	private ArrayList<OperationAndParameters> operationAndParameters; //sequenza di operazioni che hanno condotto fino a questo stato
	private boolean visited;
	private List<OperationAndParametersToTest> OpToTest; //operazioni ancora da testare

	public ProtocolAutomatonVertex(WsdlInterface wsdlInterface) {
		super();
		this.wsdlInterface = wsdlInterface;
		this.parameters = new ArrayList<ParameterEntry>();
		this.operationAndParameters = new ArrayList<OperationAndParameters>();
		this.visited = false;
	}
	
	public ProtocolAutomatonVertex(ProtocolAutomatonVertex old) {
		super();
		this.wsdlInterface = old.wsdlInterface;
		this.parameters = new ArrayList<ParameterEntry>(old.getParameters());
		this.operationAndParameters = new ArrayList<OperationAndParameters>(old.getOperationAndParameters());
		this.visited = false;
	}
	
	public ArrayList<ParameterEntry> getParameters() {
		return parameters;
	}
	
	public void addParameter(SchemaType schemaType, XmlObject value) {
		ParameterEntry newParameter = new ParameterEntry(schemaType, value);
		if (!this.parameters.contains(newParameter)) {
			this.parameters.add(newParameter);
		}
		
		this.refreshOpToTest();
	}
	
	//aggiorno lo stato con una lista di tutte le operazioni che è possibile chiamare SINTATTICAMENTE
	//data la knowledge dello stato stesso
	public void refreshOpToTest () {	
		this.OpToTest = new ArrayList<OperationAndParametersToTest>();
		List<Operation> operations = this.wsdlInterface.getOperationList();
		for (Operation operation : operations) {
			WsdlOperation wsdlOperation = wsdlInterface.getOperationByName(operation.getName());
			//è possibile chiamare l'operation da questo stato?
			if (this.canCallOperationFromKnowledge(wsdlOperation)) {
				if (StrawberryUtils.numberOfInputs(wsdlOperation) > 0) {
					Generator<ParameterEntry> matchingParams = Combinatorics.combinParams(this, wsdlOperation);
					for (ICombinatoricsVector<ParameterEntry> vector : matchingParams) {
						if (StrawberryUtils.canCallOperation(vector, wsdlOperation)) {
							this.OpToTest.add(new OperationAndParametersToTest(new OperationAndParameters(wsdlOperation, vector)));
						}
					}
				}
				//l'operation non ha parametri in input
				else {
					this.OpToTest.add(new OperationAndParametersToTest(new OperationAndParameters(wsdlOperation, null)));
				}
			}
		}
	}
	
	public OperationAndParameters getResetOperation () {
		for (OperationAndParametersToTest opToTest : this.OpToTest) {
			OperationAndParameters op = opToTest.operationAndParameters;
			if (op.getOperation().getName().equals("destroySession")) {
				opToTest.tested = true;
				return op;
			}
		}
		return null;
	}
	
	public OperationAndParameters getNonTestedOp () {
		for (OperationAndParametersToTest operationAndParametersToTest : this.OpToTest) {
			if (!operationAndParametersToTest.tested &&
					!(operationAndParametersToTest.operationAndParameters.getOperation().getName().equals("destroySession"))) {
				operationAndParametersToTest.tested = true;
				return operationAndParametersToTest.operationAndParameters;
			}
		}
		return null;
	}
	
	//restituisce tutti gli schema types delle entry dello stato
	public ArrayList<SchemaType> getSchemaTypes() {
		ArrayList<SchemaType> schemaTypes = new ArrayList<SchemaType>();
		for(ParameterEntry curr : parameters) {
			schemaTypes.add(curr.schemaType);
		}
		return schemaTypes;
	}
	
	public ArrayList<OperationAndParameters> getOperationAndParameters() {
		return operationAndParameters;
	}

	public void addOperationAndParameters(OperationAndParameters operationAndParameter) {
		this.operationAndParameters.add(operationAndParameter);
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

class OperationAndParametersToTest {
	
	public OperationAndParameters operationAndParameters;
	public boolean tested;
	
	public OperationAndParametersToTest(
			OperationAndParameters operationAndParameters) {
		super();
		this.operationAndParameters = operationAndParameters;
		this.tested = false;
	}
}
