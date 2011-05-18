/*
 *  soapUI, copyright (C) 2004-2011 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.security.support;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep;
import com.eviware.soapui.model.security.SecurityScan;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.security.SecurityTest;
import com.eviware.soapui.security.SecurityTestRunContext;
import com.eviware.soapui.security.result.SecurityScanRequestResult;
import com.eviware.soapui.security.result.SecurityScanResult;
import com.eviware.soapui.security.result.SecurityTestStepResult;
import com.eviware.soapui.security.result.SecurityResult.ResultStatus;
import com.eviware.soapui.security.scan.AbstractSecurityScanWithProperties;

/**
 * Class that keeps a JProgressBars state in sync with a SecurityTest
 * 
 * Progress bar status can be: 
 * 1. In Progreess while test is running.
 * 2. Done when test is done.
 * 3. Canced when is canceled execution
 * 4. Missing Assertions/Parameters if assertions/parameters are missing in security scan.
 * 
 * Importance/power of states:
 * 1. Missing Assertions
 * 2. Missing Parameters
 * 3. Cancel
 * 4. Done
 * 5. In Progress
 * 
 * Progress bar color can be: 
 * 1. OK
 * 2. FAILED
 * 3. MISSING_ASSERTION - same color if parameters are missing.
 * 
 * Color power:
 * 1. FAILED
 * 2. MISSING_ASSERTION
 * 3. OK
 * 
 */

public class ProgressBarSecurityTestStepAdapter
{
	private JProgressBar progressBar;
	private TestStep testStep;
	private SecurityTest securityTest;
	private InternalTestRunListener internalTestRunListener;
	private JTree tree;
	private DefaultMutableTreeNode node;
	private JLabel counterLabel;
	private static final Color OK_COLOR = new Color( 0, 204, 102 );
	private static final Color FAILED_COLOR = new Color( 255, 102, 0 );
	private static final Color MISSING_ASSERTION_COLOR = new Color( 204, 153, 255 );
	private static final Color UNKNOWN_COLOR = new Color( 240, 240, 240 );

	private static final String STATE_RUN = "In progress";
	private static final String STATE_DONE = "Done";
	private static final String STATE_CANCEL = "Canceled";
	private static final String STATE_MISSING_ASSERTIONS = "Missing Assertions";
	private static final String STATE_MISSING_PARAMETERS = "Missing Parameters";

	public ProgressBarSecurityTestStepAdapter( JTree tree, DefaultMutableTreeNode node, JProgressBar progressBar,
			SecurityTest securityTest, WsdlTestStep testStep, JLabel cntLabel )
	{
		this.tree = tree;
		this.node = node;
		this.progressBar = progressBar;
		this.testStep = testStep;
		this.securityTest = securityTest;

		this.counterLabel = cntLabel;
		internalTestRunListener = new InternalTestRunListener();
		if( progressBar != null && cntLabel != null )
		{
			this.counterLabel.setPreferredSize( new Dimension( 50, 18 ) );
			this.counterLabel.setHorizontalTextPosition( SwingConstants.CENTER );
			this.counterLabel.setHorizontalAlignment( SwingConstants.CENTER );
			this.securityTest.addSecurityTestRunListener( internalTestRunListener );
		}
	}

	public void release()
	{
		securityTest.removeSecurityTestRunListener( internalTestRunListener );

		securityTest = null;
		testStep = null;
	}

	public class InternalTestRunListener extends SecurityTestRunListenerAdapter
	{

		private int totalAlertsCounter;

		@Override
		public void beforeStep( TestCaseRunner testRunner, SecurityTestRunContext runContext, TestStepResult tsr )
		{
			if( tsr.getTestStep().getId().equals( testStep.getId() ) )
			{
				int count = securityTest.getStepSecurityApplicableChecksCount( tsr );
				// int maximum = securityTest.getTestStepSecurityChecksCount(
				// testStep.getId() );
				progressBar.getModel().setMaximum( count );

				progressBar.setString( STATE_RUN );
				progressBar.setForeground( OK_COLOR );
				progressBar.setBackground( Color.white );
				progressBar.setValue( 0 );
				counterLabel.setText( "" );
				counterLabel.setOpaque( false );

//				totalAlertsCounter = 0;
				( ( DefaultTreeModel )tree.getModel() ).nodeChanged( node );
			}
		}

		@Override
		public void beforeRun( TestCaseRunner testRunner, SecurityTestRunContext runContext )
		{

			progressBar.setString( "" );
			progressBar.setValue( 0 );
			counterLabel.setText( "" );
			counterLabel.setOpaque( false );

			totalAlertsCounter = 0;
			( ( DefaultTreeModel )tree.getModel() ).nodeChanged( node );
		}

		@Override
		public void afterStep( TestCaseRunner testRunner, SecurityTestRunContext runContext, SecurityTestStepResult result )
		{
			if( runContext.getCurrentStep().getId().equals( testStep.getId() ) )
			{
				if( !( progressBar.getString().equals( STATE_CANCEL )
						|| progressBar.getString().equals( STATE_MISSING_ASSERTIONS ) || progressBar.getString().equals(
						STATE_MISSING_PARAMETERS ) ) )
					progressBar.setString( STATE_DONE );
			}
			progressBar.setBackground( UNKNOWN_COLOR );
		}

		@Override
		public void beforeSecurityCheck( TestCaseRunner testRunner, SecurityTestRunContext runContext,
				SecurityScan securityCheck )
		{
			if( securityCheck.getTestStep().getId().equals( testStep.getId() ) )
			{
				// set progress bar color/state based on/if there is result
				if( securityCheck.getSecurityScanResult() != null
						&& securityCheck.getSecurityScanResult().getStatus() != ResultStatus.CANCELED )
				{
					if( progressBar.getString().equals( "" ) )
					{
						progressBar.setString( STATE_RUN );
						progressBar.setForeground( OK_COLOR );
					}
				}
				// report is there is no assertions.
				if( securityCheck.getAssertionCount() == 0 )
				{
					if( !progressBar.getForeground().equals( FAILED_COLOR ) )
						progressBar.setForeground( MISSING_ASSERTION_COLOR );
					progressBar.setString( STATE_MISSING_ASSERTIONS );
				}
				// or if there is no parameters.
				if( securityCheck instanceof AbstractSecurityScanWithProperties
						&& ( ( AbstractSecurityScanWithProperties )securityCheck ).getParameterHolder()
						.getParameterList().size() == 0 )
				{
					if( !progressBar.getForeground().equals( FAILED_COLOR ) )
						progressBar.setForeground( MISSING_ASSERTION_COLOR );
					if( !progressBar.getString().equals( STATE_MISSING_ASSERTIONS ) )
						progressBar.setString( STATE_MISSING_PARAMETERS );
				}
			}
		}

		public void afterSecurityCheck( TestCaseRunner testRunner, SecurityTestRunContext runContext,
				SecurityScanResult securityCheckResult )
		{

			if( securityCheckResult.getSecurityCheck().getTestStep().getId().equals( testStep.getId() ) )
			{

				if( securityCheckResult.getStatus() == ResultStatus.CANCELED )
				{
					progressBar.setString( STATE_CANCEL );
					progressBar.setBackground( UNKNOWN_COLOR );
				}
				else
				// progressbar can change its color only if not missing
				// assertions or parameters
				if( securityCheckResult.getStatus() == ResultStatus.FAILED )
				{
					progressBar.setForeground( FAILED_COLOR );
				}
				else if( securityCheckResult.getStatus() == ResultStatus.OK )
				{
					// can not change to OK color if any of previous checks
					// failed or missing assertions/parameters
					if( !progressBar.getForeground().equals( FAILED_COLOR )
							&& !progressBar.getForeground().equals( MISSING_ASSERTION_COLOR ) )
					{
						progressBar.setForeground( OK_COLOR );
					}
				}

				progressBar.setValue( ( ( SecurityTestRunContext )runContext ).getCurrentCheckIndex() + 1 );
				( ( DefaultTreeModel )tree.getModel() ).nodeChanged( node );
			}
		}

		@Override
		public void afterSecurityScanRequest( TestCaseRunner testRunner, SecurityTestRunContext runContext,
				SecurityScanRequestResult securityCheckReqResult )
		{

			if( securityCheckReqResult.getSecurityCheck().getTestStep().getId().equals( testStep.getId() ) )
			{
				if( securityCheckReqResult.getStatus() == ResultStatus.FAILED )
				{
					counterLabel.setOpaque( true );
					counterLabel.setBackground( FAILED_COLOR );
					totalAlertsCounter++ ;
					counterLabel.setText( " " + totalAlertsCounter + " " );
					( ( DefaultTreeModel )tree.getModel() ).nodeChanged( node );
					progressBar.setForeground( FAILED_COLOR );
				}
			}
		}

	}

}
