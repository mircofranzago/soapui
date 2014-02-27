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

package com.eviware.soapui.support.dnd.handlers;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.support.UISupport;

public class TestCaseToTestCaseDropHandler extends AbstractBeforeAfterModelItemDropHandler<WsdlTestCase, WsdlTestCase>
{
	public TestCaseToTestCaseDropHandler()
	{
		super( WsdlTestCase.class, WsdlTestCase.class );
	}

	
	boolean canCopyAfter( WsdlTestCase source, WsdlTestCase target )
	{
		return true;
	}

	
	boolean canMoveAfter( WsdlTestCase source, WsdlTestCase target )
	{
		return source != target;
	}

	
	boolean copyAfter( WsdlTestCase source, WsdlTestCase target )
	{
		WsdlTestCase testCase = TestCaseToTestSuiteDropHandler.copyTestCase( source, target.getTestSuite(), target
				.getTestSuite().getIndexOfTestCase( target ) + 1 );

		if( testCase != null )
			UISupport.select( testCase );

		return testCase != null;
	}

	
	boolean moveAfter( WsdlTestCase source, WsdlTestCase target )
	{
		WsdlTestCase testCase = TestCaseToTestSuiteDropHandler.moveTestCase( source, target.getTestSuite(), target
				.getTestSuite().getIndexOfTestCase( target ) + 1 );

		if( testCase != null )
			UISupport.select( testCase );

		return testCase != null;
	}

	
	String getCopyAfterInfo( WsdlTestCase source, WsdlTestCase target )
	{
		return "Copy TestCase [" + source.getName() + "] to TestSuite [" + target.getTestSuite().getName() + "]";
	}

	
	String getMoveAfterInfo( WsdlTestCase source, WsdlTestCase target )
	{
		return source == target ? "Move TestCase [" + source.getName() + "] within TestSuite" : "Move TestCase ["
				+ source.getName() + "] to TestSuite in Project [" + target.getName() + "]";
	}

	
	boolean canCopyBefore( WsdlTestCase source, WsdlTestCase target )
	{
		return true;
	}

	
	boolean canMoveBefore( WsdlTestCase source, WsdlTestCase target )
	{
		return source != target;
	}

	
	boolean copyBefore( WsdlTestCase source, WsdlTestCase target )
	{
		WsdlTestCase testCase = TestCaseToTestSuiteDropHandler.copyTestCase( source, target.getTestSuite(), target
				.getTestSuite().getIndexOfTestCase( target ) );

		if( testCase != null )
			UISupport.select( testCase );

		return testCase != null;
	}

	
	String getCopyBeforeInfo( WsdlTestCase source, WsdlTestCase target )
	{
		return getCopyAfterInfo( source, target );
	}

	
	String getMoveBeforeInfo( WsdlTestCase source, WsdlTestCase target )
	{
		return getMoveAfterInfo( source, target );
	}

	
	boolean moveBefore( WsdlTestCase source, WsdlTestCase target )
	{
		WsdlTestCase testCase = TestCaseToTestSuiteDropHandler.moveTestCase( source, target.getTestSuite(), target
				.getTestSuite().getIndexOfTestCase( target ) );

		if( testCase != null )
			UISupport.select( testCase );

		return testCase != null;
	}
}
