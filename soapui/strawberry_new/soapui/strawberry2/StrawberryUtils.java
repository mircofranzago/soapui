package soapui.strawberry2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import soapui.strawberry2.StrawberryUtils;
import soapui.strawberry2.jgraph.OpResponse;
import soapui.strawberry2.jgraph.OperationAndParameters;
import soapui.strawberry2.jgraph.ParameterEntry;

import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.support.soap.SoapUtils;
import com.eviware.soapui.impl.wsdl.support.soap.SoapVersion;
import com.eviware.soapui.model.iface.MessagePart;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.MessagePart.ContentPart;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.support.xml.XmlUtils;

public class StrawberryUtils {

	public static Document createRequestStrawberry(WsdlOperation operation)
			throws IOException {
		String request = operation.createRequest(true);
		return XmlUtils.parseXml(request);
	}

	// create an xml request from a xml fragment
	// if return null something wrong happened
	public static String response2request(String responseXml,
			String responseTag, String requestXml, String requestTag)
			throws IOException {

		// TODO devono essere parametrici
		// String responseTag = "return";
		// String requestTag = "session";

		Document responseDoc = XmlUtils.parseXml(responseXml);
		NodeList responseNodeList = responseDoc
				.getElementsByTagName(responseTag);

		if (responseNodeList.getLength() == 0)
			return null;

		Element responseElement = (Element) responseNodeList.item(0);

		Document requestDocument = XmlUtils.parseXml(requestXml);
		NodeList requestNodeList = requestDocument
				.getElementsByTagName(requestTag);

		if (requestNodeList.getLength() == 0)
			return null;

		Element requestElement = (Element) requestNodeList.item(0);

		Node copiedNode = requestDocument.importNode(responseElement, true);

		requestElement.getParentNode().replaceChild(copiedNode, requestElement);

		requestDocument
				.renameNode(
						requestDocument.getElementsByTagName(
								copiedNode.getNodeName()).item(0),
						requestDocument.getNamespaceURI(), requestTag);

		return XmlUtils.serializePretty(requestDocument);
	}
	
	//restituisce il valore del node corrispondente al nome del parametro passato 
	public static XmlObject getNodeFromResponse (Response response, String outputPartName)  {
		
//		String responseXml = response.getContentAsXml();
//		XmlObject xmlObject;
//		try {
//			xmlObject = XmlUtils.createXmlObject(responseXml);
//			XmlObject[] xmlObjects = xmlObject.selectChildren(new QName(outputPartName));
//			//assumo che i parametri di ritorno hanno tutti nomi diversi, quindi prendo il primo
//			//poichè nella lista ce ne sarà solo uno
//			if (xmlObjects.length > 0) {
//				return xmlObjects[0];
//			}
//			else return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
		
		String responseXml = response.getContentAsXml();
		Document responseDoc;
		try {
			responseDoc = XmlUtils.parseXml(responseXml);
			NodeList responseNodeList = responseDoc.getElementsByTagName(outputPartName);
			//assumo che i parametri di ritorno hanno tutti nomi diversi, quindi prendo il primo
			//poichè nella lista ce ne sarà solo uno
			return XmlUtils.createXmlObject(responseNodeList.item(0));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// dato uno SchemaType restituise un array con tutti i suoi sub-schematypes
	public static ArrayList<SchemaType> getAllSubTypes(SchemaType schemaType,
			ArrayList<SchemaType> schemaSubTypes) {
		SchemaProperty[] schemaProperties = schemaType.getElementProperties();
		for (int i = 0; i < schemaProperties.length; i++) {
			SchemaType st = schemaProperties[i].getType();
			if (!schemaSubTypes.contains(st)) {
				schemaSubTypes.add(st);
				schemaSubTypes = getAllSubTypes(st, schemaSubTypes);
			}
		}

		return schemaSubTypes;
	}

	public static ArrayList<SchemaType> getInputSchemaTypesByOperation(
			WsdlOperation wsdlOperation) {
		ArrayList<SchemaType> schemaTypes = new ArrayList<SchemaType>();
		MessagePart[] messageParts = wsdlOperation.getDefaultRequestParts();
		for (int i = 0; i < messageParts.length; i++) {
			if (messageParts[i] instanceof ContentPart) {
				SchemaType e = ((ContentPart) messageParts[i]).getSchemaType();
				schemaTypes.add(e);
			}
		}

		return schemaTypes;
	}
	
	public static ArrayList<String> getInputParameterNamesByOperation(
			WsdlOperation wsdlOperation) {
		ArrayList<String> parameterNames = new ArrayList<String>();
		MessagePart[] messageParts = wsdlOperation.getDefaultRequestParts();
		for (int i = 0; i < messageParts.length; i++) {
			if (messageParts[i] instanceof ContentPart) {
				String name = ((ContentPart) messageParts[i]).getName();
				parameterNames.add(name);
			}
		}

		return parameterNames;
	}

	public static int numberOfInputs(WsdlOperation wsdlOperation) {
		int inputs = 0;
		MessagePart[] messageParts = wsdlOperation.getDefaultRequestParts();
		for (int i = 0; i < messageParts.length; i++) {
			if (messageParts[i] instanceof ContentPart) {
				inputs++;
			}
		}
		return inputs;
	}

	public static OpResponse requestWithoutInputs(OperationAndParameters operationAndParameters) {
		WsdlOperation wsdlOperation = operationAndParameters.getOperation();
		WsdlRequest request = wsdlOperation.addNewRequest("Request"
				+ UUID.randomUUID().toString());
		String req = wsdlOperation.createRequest(true);
		request.setRequestContent(req);
		WsdlSubmit submit;
		WsdlSubmitContext wsdlSubmitContext = new WsdlSubmitContext(request);
		Response response = null;
		try {
			submit = (WsdlSubmit) request.submit(wsdlSubmitContext, false);
			response = submit.getResponse();
			return new OpResponse(wsdlOperation, null, response);
		} catch (SubmitException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static OpResponse requestWithInputs (OperationAndParameters operationAndParameters) {
		
		WsdlOperation wsdlOperation = operationAndParameters.getOperation();
		ArrayList<String> inputParameterNames = getInputParameterNamesByOperation(wsdlOperation);
				
		try {
			WsdlRequest request = wsdlOperation.addNewRequest("Request"
					+ UUID.randomUUID().toString());
			String req = wsdlOperation.createRequest(true);
			Document reqDoc = XmlUtils.parseXml(req);
			
			//devo settare i parametri all'interno della richiesta (che avrà i placeholder '?')
			ICombinatoricsVector<ParameterEntry> vector = operationAndParameters.getParameterEntries();
			for(int i = 0; i < vector.getSize(); i++) {
				ParameterEntry curr = (ParameterEntry)vector.getValue(i);
				XmlObject xmlObject = curr.getValue();
				
				//TODO assumo che i parametri abbiano name diversi
				NodeList reqNodeList = reqDoc.getElementsByTagName(inputParameterNames.get(i));
				if (reqNodeList.getLength() != 0) {
					Element reqElement = (Element) reqNodeList.item(0);

					Document xmlObjectDoc = XmlUtils.parseXml(xmlObject.toString());		

					Node copiedNode = reqDoc.importNode(xmlObjectDoc.getDocumentElement(), true);

					reqElement.getParentNode().replaceChild(copiedNode, reqElement);

					reqDoc.renameNode(copiedNode,
							reqDoc.getNamespaceURI(), inputParameterNames.get(i));
					request.setRequestContent(XmlUtils.serializePretty(reqDoc));
				}
			}
			
			request.setRequestContent(XmlUtils.serializePretty(reqDoc));
			WsdlSubmit submit;
			WsdlSubmitContext wsdlSubmitContext = new WsdlSubmitContext(request);
			Response response = null;
			
			submit = (WsdlSubmit) request.submit(wsdlSubmitContext, false);
			response = submit.getResponse();
			return new OpResponse(wsdlOperation, vector ,response);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return null;
	}
	
	public static boolean canCallOperation (ICombinatoricsVector<ParameterEntry> vector, WsdlOperation wsdlOperation) {
		for(int i = 0; i < vector.getSize(); i++) {
			ArrayList<SchemaType> operationSchemaTypes = StrawberryUtils.getInputSchemaTypesByOperation(wsdlOperation);
			ParameterEntry curr = (ParameterEntry)vector.getValue(i);
			if (!curr.getSchemaType().equals(operationSchemaTypes.get(i))) 
				return false;
		}
		return true;
	}

	public static boolean isFaultResponse(Response response, WsdlOperation wsdlOperation) {
		try {
			XmlObject xmlObject = XmlUtils.createXmlObject(response.getContentAsXml());
			SoapVersion soapVersion = wsdlOperation.getInterface().getSoapVersion();

			Element body = (Element) SoapUtils.getBodyElement(xmlObject,
					soapVersion).getDomNode();
			Element soapenvFault = XmlUtils.getFirstChildElementNS(body,
					"http://schemas.xmlsoap.org/soap/envelope/", "Fault");
			
			if (soapenvFault == null) {
				return false;
			}
			else return true;

		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

}
