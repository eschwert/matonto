package org.matonto.ontology.core.impl.owlapi.classexpression;

import org.matonto.ontology.core.api.classexpression.ClassExpression;
import org.matonto.ontology.core.api.classexpression.ObjectMaxCardinality;
import org.matonto.ontology.core.api.propertyexpression.ObjectPropertyExpression;
import org.matonto.ontology.core.api.types.ClassExpressionType;

public class SimpleObjectMaxCardinality 
	extends SimpleObjectCardinalityRestriction 
	implements ObjectMaxCardinality {

	
	
	public SimpleObjectMaxCardinality(ObjectPropertyExpression property, int cardinality, ClassExpression expression) 
	{
		super(property, cardinality, expression);
	}
	
	
	@Override
	public ClassExpressionType getClassExpressionType()
	{
		return ClassExpressionType.OBJECT_MAX_CARDINALITY;
	}
	
	
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return obj instanceof ObjectMaxCardinality;
	}
	


}