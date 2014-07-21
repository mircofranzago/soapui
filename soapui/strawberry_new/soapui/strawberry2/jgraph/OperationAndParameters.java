package soapui.strawberry2.jgraph;

import org.paukov.combinatorics.ICombinatoricsVector;

import com.eviware.soapui.impl.wsdl.WsdlOperation;

public class OperationAndParameters {
	private WsdlOperation operation;
	private ICombinatoricsVector<ParameterEntry> parameterEntries;
	
	public OperationAndParameters(
			WsdlOperation operation,
			ICombinatoricsVector<ParameterEntry> parameterEntries) {
		super();
		this.parameterEntries = parameterEntries;
		this.operation = operation;
	}

	public ICombinatoricsVector<ParameterEntry> getParameterEntries() {
		return parameterEntries;
	}

	public WsdlOperation getOperation() {
		return operation;
	}
	
	
}
