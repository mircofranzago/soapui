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
package com.eviware.soapui.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.SchemaType;

import com.eviware.soapui.impl.wsdl.MutableTestPropertyHolder;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.testsuite.TestProperty;
import com.eviware.soapui.model.testsuite.TestPropertyListener;

public class SensitiveInformationPropertyHolder implements MutableTestPropertyHolder
{

	private Map<String, TestProperty> properties = new HashMap<String, TestProperty>();

	
	public void addTestPropertyListener( TestPropertyListener listener )
	{
		// TODO Auto-generated method stub

	}

	
	public ModelItem getModelItem()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public Map<String, TestProperty> getProperties()
	{
		return properties;
	}

	
	public String getPropertiesLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public TestProperty getProperty( String name )
	{
		return properties.get( name );
	}

	
	public TestProperty getPropertyAt( int index )
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getPropertyCount()
	{
		// TODO Auto-generated method stub
		return properties.size();
	}

	
	public List<TestProperty> getPropertyList()
	{
		return new ArrayList<TestProperty>( properties.values() );
	}

	
	public String[] getPropertyNames()
	{
		return properties.keySet().toArray( new String[properties.keySet().size()] );
	}

	
	public String getPropertyValue( String name )
	{
		return properties.get( name ).getValue();
	}

	
	public boolean hasProperty( String name )
	{
		return properties.containsKey( name );
	}

	
	public void removeTestPropertyListener( TestPropertyListener listener )
	{
		// TODO Auto-generated method stub

	}

	
	public void setPropertyValue( String name, String value )
	{
		if( properties.get( name ) != null )
			properties.get( name ).setValue( value );
		else
		{
			properties.put( name, new SensitiveTokenProperty( name, value ) );
		}
	}

	
	public TestProperty addProperty( String name )
	{
		return properties.put( name, new SensitiveTokenProperty( name, null ) );
	}

	
	public void moveProperty( String propertyName, int targetIndex )
	{
		// TODO Auto-generated method stub
	}

	
	public TestProperty removeProperty( String propertyName )
	{
		return properties.remove( propertyName );
	}

	
	public boolean renameProperty( String name, String newName )
	{
		TestProperty tp = properties.get( name );
		if( tp != null )
		{
			properties.put( newName, tp );
			properties.remove( name );
			return true;
		}
		else
			return false;
	}

	public class SensitiveTokenProperty implements TestProperty
	{

		private String name;
		private String value;

		public SensitiveTokenProperty( String name, String value )
		{
			this.name = name;
			this.value = value;
		}

		public String getDefaultValue()
		{
			return "";
		}

		
		public String getDescription()
		{
			// TODO Auto-generated method stub
			return null;
		}

		
		public ModelItem getModelItem()
		{
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getName()
		{
			return name;
		}

		
		public SchemaType getSchemaType()
		{
			// TODO Auto-generated method stub
			return null;
		}

		
		public QName getType()
		{
			// TODO Auto-generated method stub
			return null;
		}

		
		public String getValue()
		{
			return value;
		}

		
		public boolean isReadOnly()
		{
			// TODO Auto-generated method stub
			return false;
		}

		
		public boolean isRequestPart()
		{
			// TODO Auto-generated method stub
			return false;
		}

		
		public void setValue( String value )
		{
			this.value = value;
		}

		public void setName( String aValue )
		{
			this.name = aValue;
		}

	}

}
