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

import com.eviware.soapui.model.ModelItem;

public abstract class AbstractAfterModelItemDropHandler<T1 extends ModelItem, T2 extends ModelItem> extends
		AbstractModelItemDropHandler<T1, T2>
{
	protected AbstractAfterModelItemDropHandler( Class<T1> sourceClass, Class<T2> targetClass )
	{
		super( sourceClass, targetClass );
	}

	
	boolean canCopyBefore( T1 source, T2 target )
	{
		return false;
	}

	
	boolean canMoveBefore( T1 source, T2 target )
	{
		return false;
	}

	
	boolean copyBefore( T1 source, T2 target )
	{
		return false;
	}

	
	String getCopyBeforeInfo( T1 source, T2 target )
	{
		return null;
	}

	
	String getMoveBeforeInfo( T1 source, T2 target )
	{
		return null;
	}

	
	boolean moveBefore( T1 source, T2 target )
	{
		return false;
	}

	
	boolean canCopyOn( T1 source, T2 target )
	{
		return false;
	}

	
	boolean canMoveOn( T1 source, T2 target )
	{
		return false;
	}

	
	boolean copyOn( T1 source, T2 target )
	{
		return false;
	}

	
	String getCopyOnInfo( T1 source, T2 target )
	{
		return null;
	}

	
	String getMoveOnInfo( T1 source, T2 target )
	{
		return null;
	}

	
	boolean moveOn( T1 source, T2 target )
	{
		return false;
	}
}
