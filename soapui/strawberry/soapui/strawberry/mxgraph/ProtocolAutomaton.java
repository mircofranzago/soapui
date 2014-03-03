package soapui.strawberry.mxgraph;

import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.mxgraph.view.mxGraph;

public class ProtocolAutomaton extends mxGraph {

	private int ID = 0; 
	
	public ProtocolAutomaton(ProtocolAutomatonVertex protocolAutomatonVertex) {
		super();
		
		Object parent = this.getDefaultParent();
		this.getModel().beginUpdate();
		
		//insert the root node that contains the instance pool
		this.insertVertex(parent, Integer.toString(ID), protocolAutomatonVertex, 100, 100, 80, 30);
		
		this.getModel().endUpdate();
	}
	
	public void automatonConstructionBaseStep(WsdlInterface iface) {
		
	}

}
