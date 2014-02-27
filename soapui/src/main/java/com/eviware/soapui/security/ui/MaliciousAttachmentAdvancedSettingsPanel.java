package com.eviware.soapui.security.ui;

import javax.swing.JComponent;

import com.eviware.soapui.config.MaliciousAttachmentSecurityScanConfig;
import com.eviware.soapui.support.UISupport;
import com.eviware.x.form.XFormField;
import com.eviware.x.form.XFormFieldListener;
import com.eviware.x.form.support.ADialogBuilder;
import com.eviware.x.form.support.AField;
import com.eviware.x.form.support.AField.AFieldType;
import com.eviware.x.form.support.AForm;
import com.eviware.x.impl.swing.JFormDialog;

public class MaliciousAttachmentAdvancedSettingsPanel
{
	private JFormDialog dialog;
	private MaliciousAttachmentSecurityScanConfig config;

	public MaliciousAttachmentAdvancedSettingsPanel( MaliciousAttachmentSecurityScanConfig config )
	{
		this.config = config;
		dialog = ( JFormDialog )ADialogBuilder.buildDialog( AdvancedSettings.class );
		initDialog();
	}

	private void initDialog()
	{
		dialog.setValue( AdvancedSettings.REQUEST_TIMEOUT, String.valueOf( config.getRequestTimeout() ) );

		dialog.getFormField( AdvancedSettings.REQUEST_TIMEOUT ).addFormFieldListener( new XFormFieldListener()
		{

			
			public void valueChanged( XFormField sourceField, String newValue, String oldValue )
			{

				if( newValue == null || newValue.trim().length() == 0 )
				{
					return;
				}

				try
				{
					int val = Integer.valueOf( newValue );
					config.setRequestTimeout( val );
				}
				catch( Exception e )
				{
					UISupport.showErrorMessage( "Request timeout value must be integer number" );
				}
			}
		} );
	}

	public JComponent getPanel()
	{
		return dialog.getPanel();
	}

	public MaliciousAttachmentSecurityScanConfig getConfig()
	{
		return config;
	}

	public void setConfig( MaliciousAttachmentSecurityScanConfig config )
	{
		this.config = config;
	}

	@AForm( description = "Malicious Attachment Configuration", name = "Malicious Attachment Configuration" )
	protected interface AdvancedSettings
	{
		@AField( description = "Request timeout(ms)", name = "Request timeout(ms)", type = AFieldType.INT )
		final static String REQUEST_TIMEOUT = "Request timeout(ms)";
	}

	public void release()
	{
		dialog.release();
		dialog = null;
		config = null;
	}
}
