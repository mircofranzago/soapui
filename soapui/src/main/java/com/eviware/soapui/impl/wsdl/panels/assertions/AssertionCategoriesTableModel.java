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
package com.eviware.soapui.impl.wsdl.panels.assertions;

import java.util.Set;

import javax.swing.table.DefaultTableModel;

public class AssertionCategoriesTableModel extends DefaultTableModel
{
	Set<String> listEntriesSet;

	public AssertionCategoriesTableModel()
	{
	}

	public void setLisetEntriesSet( Set<String> keySet )
	{
		listEntriesSet = keySet;
	}

	
	public int getColumnCount()
	{
		return 1;
	}

	
	public int getRowCount()
	{
		if( listEntriesSet != null )
		{
			return listEntriesSet.size();
		}
		else
		{
			return 1;
		}
	}

	
	public Object getValueAt( int row, int column )
	{
		if( listEntriesSet != null )
		{
			return listEntriesSet.toArray()[row];
		}
		else
		{
			return null;
		}
	}

}
