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

package com.eviware.soapui.impl.wsdl.actions.mockservice;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.mock.MockService;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.support.AbstractSoapUIAction;

/**
 * Removes a MockService from its WsdlProject
 * 
 * @author Ole.Matzura
 */

public class DeleteMockServiceAction extends AbstractSoapUIAction<MockService>
{
	public DeleteMockServiceAction()
	{
		super( "Remove", "Removes this MockService from the Project" );
	}

	public void perform( MockService mockService, Object param )
	{
		if( SoapUI.getMockEngine().hasRunningMock( mockService ) )
		{
			UISupport.showErrorMessage( "Cannot remove MockService while mocks are running" );
			return;
		}

		if( UISupport.confirm( "Remove MockService [" + mockService.getName() + "] from Project", "Remove MockService" ) )
		{
			mockService.getProject().removeMockService( mockService );
		}
	}

}
