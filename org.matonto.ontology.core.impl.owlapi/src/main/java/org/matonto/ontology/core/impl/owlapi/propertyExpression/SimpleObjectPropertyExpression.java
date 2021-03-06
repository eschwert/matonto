package org.matonto.ontology.core.impl.owlapi.propertyExpression;

/*-
 * #%L
 * org.matonto.ontology.core.impl.owlapi
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 iNovex Information Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.matonto.ontology.core.api.propertyexpression.ObjectPropertyExpression;


public class SimpleObjectPropertyExpression 
	extends SimplePropertyExpression
	implements ObjectPropertyExpression {


	public ObjectPropertyExpression inverse;
	
	
	@Override
	public ObjectPropertyExpression getInverseProperty() 
	{
		if(inverse == null)
			return new SimpleObjectInverseOf(this);
		return inverse;
	}

	@Override
	public ObjectPropertyExpression getNamedProperty() 
	{
		return inverse;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) 
			return true;
		
		if(!super.equals(obj))
			return false;
		
		return obj instanceof ObjectPropertyExpression;	
	}

}
