package org.matonto.ontology.core.impl.owlapi.classexpression;

import java.util.HashSet;
import java.util.Set;

import org.matonto.ontology.core.api.classexpression.ClassExpression;
import org.matonto.ontology.core.api.classexpression.ObjectCardinalityRestriction;
import org.matonto.ontology.core.api.propertyexpression.ObjectPropertyExpression;

import com.google.common.base.Preconditions;
import org.matonto.ontology.core.api.types.ClassExpressionType;

public class SimpleObjectCardinalityRestriction implements ObjectCardinalityRestriction {

	
	private int cardinality;
	private ObjectPropertyExpression property;
	private ClassExpression expression;
	
	
	public SimpleObjectCardinalityRestriction(ObjectPropertyExpression property, int cardinality, ClassExpression expression)
	{
		this.cardinality = cardinality;
		this.property = Preconditions.checkNotNull(property, "property cannot be null");
		this.expression = Preconditions.checkNotNull(expression, "expression cannot be null");
	}
	
	
	@Override
	public ObjectPropertyExpression getProperty()
	{
		return property;
	}
	
	
	@Override
	public int getCardinality()
	{
		return cardinality;
	}
	
	
	@Override
	public ClassExpression getClassExpression()
	{
		return expression;
	}
	
	
	@Override
	public ClassExpressionType getClassExpressionType()
	{
		return null;
	}

	
	@Override
	public Set<ClassExpression> asConjunctSet() 
	{
		Set<ClassExpression> conjuncts = new HashSet<ClassExpression>();
		conjuncts.add(this);	
		return conjuncts;
	}
	

	@Override
	public boolean containsConjunct(ClassExpression ce) 
	{
		return ce.equals(this);
	}
	

	@Override
	public Set<ClassExpression> asDisjunctSet() 
	{
		Set<ClassExpression> disjuncts = new HashSet<ClassExpression>();
		disjuncts.add(this);
		return disjuncts;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) 
		    return true;
		
		if (obj instanceof ObjectCardinalityRestriction) {
			ObjectCardinalityRestriction other = (ObjectCardinalityRestriction) obj;
			return ((other.getProperty().equals(getProperty())) && (other.getCardinality()==cardinality) && (other.getClassExpression().equals(getClassExpression())));
		}
		return false;
	}

}