package org.matonto.ontology.core.api.axiom;

import org.matonto.ontology.core.api.classexpression.ClassExpression;
import org.matonto.ontology.core.api.propertyexpression.DataPropertyExpression;
import org.matonto.ontology.core.api.propertyexpression.ObjectPropertyExpression;
import org.matonto.ontology.core.api.propertyexpression.PropertyExpression;

import java.util.Set;


public interface HasKeyAxiom extends Axiom {

    ClassExpression getClassExpression();

    Set<PropertyExpression> getPropertyExpressions();

    Set<ObjectPropertyExpression> getObjectPropertyExpressions();

    Set<DataPropertyExpression> getDataPropertyExpressions();
}
