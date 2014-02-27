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

package com.eviware.soapui.security.support;

import com.eviware.soapui.model.security.SecurityScan;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.security.SecurityTestRunContext;
import com.eviware.soapui.security.result.SecurityScanRequestResult;
import com.eviware.soapui.security.result.SecurityScanResult;
import com.eviware.soapui.security.result.SecurityTestStepResult;

/**
 * Adapter for SecurityTestRunListener implementations
 * 
 * @author dragica.soldo
 */

public class SecurityTestRunListenerAdapter implements SecurityTestRunListener
{

	
	public void beforeRun( TestCaseRunner testRunner, SecurityTestRunContext runContext )
	{
	}

	
	public void beforeStep( TestCaseRunner testRunner, SecurityTestRunContext runContext, TestStepResult testStepResult )
	{
	}

	
	public void afterStep( TestCaseRunner testRunner, SecurityTestRunContext runContext, SecurityTestStepResult result )
	{
	}

	
	public void afterRun( TestCaseRunner testRunner, SecurityTestRunContext runContext )
	{
	}

	
	public void afterSecurityScan( TestCaseRunner testRunner, SecurityTestRunContext runContext,
			SecurityScanResult securityCheckResult )
	{
	}

	
	public void beforeSecurityScan( TestCaseRunner testRunner, SecurityTestRunContext runContext,
			SecurityScan securityCheck )
	{
	}

	
	public void afterSecurityScanRequest( TestCaseRunner testRunner, SecurityTestRunContext runContext,
			SecurityScanRequestResult securityCheckReqResult )
	{
		// TODO Auto-generated method stub

	}

	
	public void afterOriginalStep( TestCaseRunner testRunner, SecurityTestRunContext runContext,
			SecurityTestStepResult result )
	{
		// TODO Auto-generated method stub

	}

}
