/*
 *  SoapUI, copyright (C) 2004-2012 smartbear.com
 *
 *  SoapUI is free software; you can redistribute it and/or modify it under the
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  SoapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.support.editor.inspectors.wsrm;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eviware.soapui.impl.support.AbstractHttpRequest;
import com.eviware.soapui.impl.wsdl.support.wsrm.WsrmContainer;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.eviware.soapui.support.editor.EditorView;
import com.eviware.soapui.support.editor.inspectors.AbstractXmlInspector;
import com.eviware.soapui.support.editor.views.xml.raw.RawXmlEditorFactory;
import com.eviware.soapui.support.editor.xml.XmlDocument;
import com.jgoodies.binding.PresentationModel;

public abstract class AbstractWsrmInspector extends AbstractXmlInspector
{

	private JPanel mainPanel;
	private SimpleBindingForm form;
	private final WsrmContainer wsrmContainer;

	protected AbstractWsrmInspector( WsrmContainer wsrmContainer )
	{
		super( "WS-RM", "WS-Reliable Messaging related settings", true, WsrmInspectorFactory.INSPECTOR_ID );
		this.wsrmContainer = wsrmContainer;
	}

	public JComponent getComponent()
	{
		if( mainPanel == null )
		{
			mainPanel = new JPanel( new BorderLayout() );
			form = new SimpleBindingForm( new PresentationModel<AbstractHttpRequest<?>>( wsrmContainer.getWsrmConfig() ) );
			buildContent( form );
			mainPanel.add( new JScrollPane( form.getPanel() ), BorderLayout.CENTER );
		}
		return mainPanel;
	}

	public void buildContent( SimpleBindingForm form )
	{
	};

	
	public void release()
	{
		super.release();

		if( form != null )
			form.getPresentationModel().release();
	}

	
	public boolean isEnabledFor( EditorView<XmlDocument> view )
	{
		return !view.getViewId().equals( RawXmlEditorFactory.VIEW_ID );
	}

}
