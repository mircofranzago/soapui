package soapui.strawberry2;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import net.sf.saxon.type.AnySimpleType;

import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;

import soapui.strawberry2.StrawberryUtils;
import soapui.strawberry2.jgraph.OperationAndParameters;
import soapui.strawberry2.jgraph.ProtocolAutomaton;
import soapui.strawberry2.jgraph.ProtocolAutomatonVertex;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.StandaloneSoapUICore;
import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.settings.XmlBeansSettingsImpl;
import com.eviware.soapui.impl.wsdl.WsdlContentPart;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.panels.operation.WsdlOperationPanelBuilder;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlUtils;
import com.eviware.soapui.model.iface.MessagePart.ContentPart;
import com.eviware.soapui.model.iface.MessagePart.PartType;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.support.xml.XmlUtils;

public class Main {

	@SuppressWarnings({ "rawtypes", "unused" })
	public static void main(String[] args) {

		try {	
			SoapUI.setSoapUICore(new StandaloneSoapUICore(true));
			
			// create new project
			WsdlProject wsdlProject = new WsdlProject();

			WsdlInterface wsdlInterface = WsdlInterfaceFactory
					.importWsdl(wsdlProject,
							"http://localhost:8080/ECService/services/ECServicePort?wsdl",
							true)[0];
			
			//instance pool creation
			ProtocolAutomatonVertex instancePool = new ProtocolAutomatonVertex(wsdlInterface);
			WsdlOperation instancePoolOperation1 = wsdlInterface.getOperationByName("openSession");
			
			String instancePoolString1 = "<username> username </username>";
			instancePool.addParameter(((ContentPart)instancePoolOperation1.getDefaultRequestParts()[0]).getSchemaType(), 
										XmlUtils.createXmlObject(instancePoolString1));
			
			String instancePoolString2 = "<password> password </password>";
			instancePool.addParameter(((ContentPart)instancePoolOperation1.getDefaultRequestParts()[1]).getSchemaType(), 
					XmlUtils.createXmlObject(instancePoolString2));
		
			ProtocolAutomaton protocolAutomaton = new ProtocolAutomaton(instancePool);
			
			Stack<ProtocolAutomatonVertex> stack = new Stack<ProtocolAutomatonVertex>();
			stack.push(protocolAutomaton.getRoot());
			int i = 0;
			while (i<=50 || !stack.isEmpty()) {
				System.out.println(protocolAutomaton.toString());
				
				ProtocolAutomatonVertex vertex = stack.peek();
				OperationAndParameters operation = vertex.getNonTestedOp();
				if (operation != null) {
					System.err.println(operation.getOperation().getName());
					ProtocolAutomatonVertex tempVertex = protocolAutomaton.automatonConstructionStep(vertex, operation);
						if (tempVertex != null && !tempVertex.equals(vertex)) {
							stack.push(tempVertex);
							i++;
						}
				}
				else {
					operation = vertex.getResetOperation();
					if (stack.size() >= 1)
						stack.pop();
					protocolAutomaton.automatonResetStep(vertex, operation);
					if (stack.size() >= 1)
						protocolAutomaton.restart(stack.peek());					
				}
			}
			
			
			
			/*
			int i = 0;
			while (i<=20) {
				ProtocolAutomatonVertex next = protocolAutomaton.getNextNonVisited();
				if (next != null) {
					protocolAutomaton.automatonConstructionStep(wsdlInterface, next);
					//System.out.println(protocolAutomaton.toString());
				}
				i++;
			}
			*/
			System.out.println("contatore: " + i);
			System.out.println(protocolAutomaton.toString());
			

			// get desired operation
			WsdlOperation operation = (WsdlOperation) wsdlInterface
					.getOperationByName("openSession");
			// ///////////////////////////
			
			ContentPart m = (ContentPart)operation.getDefaultRequestParts()[0];
			SchemaType stringType = m.getSchemaType();
			SchemaType st = ((ContentPart)(((WsdlOperation) wsdlInterface
					.getOperationByName("destroySession")).getDefaultRequestParts()[0])).getSchemaType();

			SchemaParticle sp = st.getContentModel();
//			XmlAnySimpleType[] cc = st.getEnumerationValues();
//			String[] dd = st.getPatterns();
			
			SchemaProperty[] aa = st.getElementProperties(), //good
			bb = st.getProperties();
			
			SchemaGlobalElement sg = ((ContentPart)(((WsdlOperation) wsdlInterface
					.getOperationByName("destroySession")).getDefaultRequestParts()[0])).getPartElement();

			
			//SchemaType scddfdsf = aa[0].getType();
			ArrayList<SchemaType> asas = StrawberryUtils.getAllSubTypes(st, new ArrayList<SchemaType>());
			
			//true
boolean b = ((ContentPart)(operation.getDefaultRequestParts()[0])).getSchemaType().equals(
		((ContentPart)(operation.getDefaultRequestParts()[1])).getSchemaType());
//false
boolean b1 = ((ContentPart)(operation.getDefaultRequestParts()[0])).getSchemaType().equals(
		((ContentPart)(((WsdlOperation) wsdlInterface
				.getOperationByName("destroySession")).getDefaultRequestParts()[0])).getSchemaType());

			QName q = operation.getRequestBodyElementQName();
			XmlBeansSettingsImpl xml = operation.getSettings();

			WsdlContentPart c = null; 



			// create a new empty request for that operation
			WsdlRequest request = operation.addNewRequest("My request");

			// generate the request content from the schema
			String req = operation.createRequest(true);
			req = XmlUtils.setXPathContent( req, "//username", "username2" );
			req = XmlUtils.setXPathContent( req, "//password", "password2" );
			System.err.println(req);

			request.setRequestContent(req);

			// submit the request
			WsdlSubmit submit;
			WsdlSubmitContext wsdlSubmitContext = new WsdlSubmitContext(request);

			submit = (WsdlSubmit) request.submit(wsdlSubmitContext, false);

			// wait for the response
			Response response = submit.getResponse();

			// print the response
			String content = response.getContentAsString();
			System.err.println(content);
			
			
			//////////////////////////////////////////////////////////////////////////////////
			
			
			//return
			Object a1 = ((ContentPart)(wsdlInterface.getOperationByName("getAvailableProducts").getDefaultResponseParts()[0])).getName();
			//null
			Object a2 = ((ContentPart)(wsdlInterface.getOperationByName("getAvailableProducts").getDefaultResponseParts()[0])).getPartElement();
			//null
			Object a3 = ((ContentPart)(wsdlInterface.getOperationByName("getAvailableProducts").getDefaultResponseParts()[0])).getPartElementName();
			Object a4 = ((ContentPart)(wsdlInterface.getOperationByName("getAvailableProducts").getDefaultResponseParts()[0])).getPartType();
			//schema type impl (T=productArray@http://ecservice.univaq.it/)
			Object a5 = ((ContentPart)(wsdlInterface.getOperationByName("getAvailableProducts").getDefaultResponseParts()[0])).getSchemaType();
			
			javax.wsdl.Part[] ps = WsdlUtils.getOutputParts(wsdlInterface.getOperationByName("getAvailableProducts").findBindingOperation(wsdlInterface.getWsdlContext().getDefinition()));
			
	//schema type impl (T=productArray@http://ecservice.univaq.it/)
	SchemaType schtpy = WsdlUtils.getSchemaTypeForPart(wsdlInterface.getWsdlContext() , ps[0]);
	
	//null		
	SchemaGlobalElement schelm = WsdlUtils.getSchemaElementForPart(wsdlInterface.getWsdlContext(), ps[0]);
			String conttype = response.getContentType();


			javax.wsdl.Part[] ps1 = WsdlUtils.getOutputParts(wsdlInterface.getOperationByName("openSession").findBindingOperation(wsdlInterface.getWsdlContext().getDefinition()));
		String name1 = ps1[0].getName();
		
			SchemaType schemaType1 = WsdlUtils.getSchemaTypeForPart(wsdlInterface.getWsdlContext() , ps1[0]);
			

			String responseXml = response.getContentAsXml(); // INPUT1
	
			
			WsdlRequest request2 = wsdlInterface.getOperationByName("destroySession").addNewRequest("My request");
			String string2 = wsdlInterface.getOperationByName("destroySession").createRequest(true); //INPUT2

			String PROVA = StrawberryUtils.response2request(null, responseXml , null, string2);
			

System.err.println(PROVA);
request2.setRequestContent(PROVA);
WsdlSubmit submit2;
WsdlSubmitContext wsdlSubmitContext2 = new WsdlSubmitContext(request2);

submit2 = (WsdlSubmit) request2.submit(wsdlSubmitContext2, false);

// wait for the response
Response response2 = submit2.getResponse();

// print the response
String content2 = response2.getContentAsString();
System.err.println(content2);


			return;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// assertNotNull( content );
		// assertTrue( content.indexOf( "404 Not Found" ) > 0 );
	}

}
