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

package com.eviware.soapui.support.dnd;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.tree.SoapUITreeNode;

public class NavigatorDragAndDropable extends JTreeDragAndDropable<ModelItem>
{
	public NavigatorDragAndDropable( JTree tree )
	{
		super( tree );
	}

	
	public ModelItem getModelItemAtRow( int row )
	{
		TreePath pathForRow = getTree().getPathForRow( row );
		return pathForRow == null ? null : ( ( SoapUITreeNode )pathForRow.getLastPathComponent() ).getModelItem();
	}

	
	public int getRowForModelItem( ModelItem modelItem )
	{
		if( modelItem == null )
			return -1;

		TreePath treePath = SoapUI.getNavigator().getTreePath( modelItem );
		return getTree().getRowForPath( treePath );
	}

	public Component getRenderer( ModelItem modelItem )
	{
		TreePath treePath = SoapUI.getNavigator().getTreePath( modelItem );
		int row = getTree().getRowForPath( treePath );
		SoapUITreeNode treeNode = ( SoapUITreeNode )treePath.getLastPathComponent();

		return getTree().getCellRenderer().getTreeCellRendererComponent( getTree(), treeNode, true,
				getTree().isExpanded( row ), treeNode.isLeaf(), row, true );
	}

	
	public void toggleExpansion( ModelItem last )
	{
		if( last == SoapUI.getWorkspace() )
			return;

		super.toggleExpansion( last );
	}
}
