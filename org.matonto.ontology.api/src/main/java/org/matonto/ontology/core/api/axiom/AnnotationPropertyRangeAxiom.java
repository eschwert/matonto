package org.matonto.ontology.core.api.axiom;

import org.matonto.ontology.core.api.propertyexpression.AnnotationProperty;
import org.matonto.ontology.core.api.OntologyIRI;

public interface AnnotationPropertyRangeAxiom extends AnnotationAxiom {

	OntologyIRI getRange();
	
	AnnotationProperty getProperty();
}