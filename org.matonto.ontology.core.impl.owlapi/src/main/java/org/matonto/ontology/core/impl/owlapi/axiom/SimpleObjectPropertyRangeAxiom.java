package org.matonto.ontology.core.impl.owlapi.axiom;

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

import java.util.Set;
import javax.annotation.Nonnull;
import org.matonto.ontology.core.api.Annotation;
import org.matonto.ontology.core.api.classexpression.ClassExpression;
import org.matonto.ontology.core.api.propertyexpression.ObjectPropertyExpression;
import org.matonto.ontology.core.api.axiom.ObjectPropertyRangeAxiom;
import org.matonto.ontology.core.api.types.AxiomType;


public class SimpleObjectPropertyRangeAxiom 
	extends SimpleAxiom 
	implements ObjectPropertyRangeAxiom {

	
	private ObjectPropertyExpression objectProperty;
	private ClassExpression range;
	
	
	public SimpleObjectPropertyRangeAxiom(@Nonnull ObjectPropertyExpression objectProperty, @Nonnull ClassExpression range, Set<Annotation> annotations) 
	{
		super(annotations);
		this.objectProperty = objectProperty;
		this.range = range;
	}

	
	@Override
	public ObjectPropertyRangeAxiom getAxiomWithoutAnnotations() 
	{
		if(!isAnnotated())
			return this;
		
		return new SimpleObjectPropertyRangeAxiom(objectProperty, range, NO_ANNOTATIONS);	
	}

	
	@Override
	public ObjectPropertyRangeAxiom getAnnotatedAxiom(@Nonnull Set<Annotation> annotations) 
	{
		return new SimpleObjectPropertyRangeAxiom(objectProperty, range, mergeAnnos(annotations));
	}


	@Override
	public AxiomType getAxiomType()
	{
		return AxiomType.OBJECT_PROPERTY_RANGE;
	}

	
	@Override
	public ObjectPropertyExpression getObjectProperty() 
	{
		return objectProperty;
	}

	
	@Override
	public ClassExpression getRange() 
	{
		return range;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) 
		    return true;
		
		if (!super.equals(obj)) 
			return false;
		
		if (obj instanceof ObjectPropertyRangeAxiom) {
			ObjectPropertyRangeAxiom other = (ObjectPropertyRangeAxiom)obj;			 
			return ((objectProperty.equals(other.getObjectProperty())) && (range.equals(other.getRange())));
		}
		
		return false;
	}


}
