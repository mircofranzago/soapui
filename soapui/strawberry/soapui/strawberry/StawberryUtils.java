package soapui.strawberry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.support.xml.XmlUtils;

public class StawberryUtils {
	
	public static Document createRequestStrawberry (WsdlOperation operation) throws IOException {
		String request = operation.createRequest(true);
		return XmlUtils.parseXml(request);
	}

	//create an xml request from a xml fragment
	//if return null something wrong happened
	public static String response2request(String responseXml, String responseTag, String requestXml, String requestTag)
			throws IOException {

		// TODO devono essere parametrici
//		String responseTag = "return";
//		String requestTag = "session";

		Document responseDoc = XmlUtils.parseXml(responseXml);
		NodeList responseNodeList = responseDoc
				.getElementsByTagName(responseTag);
		
		if (responseNodeList.getLength() == 0) return null;
		
		Element responseElement = (Element) responseNodeList.item(0);

		Document requestDocument = XmlUtils.parseXml(requestXml);
		NodeList requestNodeList = requestDocument
				.getElementsByTagName(requestTag);
		
		if (requestNodeList.getLength() == 0) return null;
		
		Element requestElement = (Element) requestNodeList.item(0);

		Node copiedNode = requestDocument.importNode(responseElement, true);

		requestElement.getParentNode().replaceChild(copiedNode, requestElement);

		requestDocument.renameNode(requestDocument.getElementsByTagName(copiedNode.getNodeName())
				.item(0), requestDocument.getNamespaceURI(), requestTag);

		return XmlUtils.serializePretty(requestDocument);
	}
	
	
	//dato uno SchemaType restituise un array con tutti i suoi sub-schematypes
	public static ArrayList<SchemaType> getAllSubTypes (SchemaType schemaType, ArrayList<SchemaType> schemaSubTypes) {
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
	
}
