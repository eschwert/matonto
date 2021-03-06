package org.matonto.ontology.core.api;

/*-
 * #%L
 * org.matonto.ontology.api
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

import org.matonto.ontology.core.api.axiom.Axiom;
import org.matonto.ontology.core.api.classexpression.OClass;
import org.matonto.ontology.core.api.datarange.Datatype;
import org.matonto.ontology.core.api.propertyexpression.AnnotationProperty;
import org.matonto.ontology.core.api.propertyexpression.DataProperty;
import org.matonto.ontology.core.api.propertyexpression.ObjectProperty;
import org.matonto.ontology.core.utils.MatontoOntologyException;
import org.matonto.rdf.api.IRI;
import org.matonto.rdf.api.Model;
import org.matonto.rdf.api.ModelFactory;

import java.io.OutputStream;
import java.util.Set;

public interface Ontology {

    Model asModel(ModelFactory factory) throws MatontoOntologyException;

    OutputStream asTurtle() throws MatontoOntologyException;

    OutputStream asRdfXml() throws MatontoOntologyException;

    OutputStream asOwlXml() throws MatontoOntologyException;

    OutputStream asJsonLD() throws MatontoOntologyException;

    /**
     * Returns the OntologyID that describes the Ontology IRI, Version IRI,
     * and Ontology identifier. Note: If the serialized ontology contains an
     * Ontology IRI or Version IRI, it must match the Ontology and Version IRIs in
     * this OntologyId object.
     *
     * @return the OntologyID that describes the Ontology IRI, Version IRI,
     *         and Ontology identifier
     */
    OntologyId getOntologyId();

    /**
     * Returns the set of IRIs of unloadable imported ontologies.  The set is accumulated during loading the
     * ontology from an ontology document or an ontology IRI.
     *
     * @return set of IRIs
     */
    Set<IRI> getUnloadableImportIRIs();

    /**
     * Gets the set of loaded ontologies that this ontology is related to via the directlyImports relation.
     *
     * @return set of ontologies
     */
    Set<Ontology> getDirectImports();

    /**
     * Gets the set of loaded ontologies that this ontology is related to via the reflexive transitive closure 
     * of the directlyImports relation as defined in Section 3.4 of the OWL 2 Structural Specification.
     * 
     * <p>Note: The import closure of an ontology O is a set containing O and all the ontologies that O imports.
     * The import closure of O SHOULD NOT contain ontologies O1 and O2 such that O1 and O2
     * are different ontology versions
     * from the same ontology series, or O1 contains an ontology annotation owl:incompatibleWith with
     * the value equal to either
     * the ontology IRI or the version IRI of O2.</p>
     * 
     * @return set of ontologies
     */
    Set<Ontology> getImportsClosure();

    /**
     * Gets the ontology annotations, excluding annotations for other objects such as classes and entities.
     *
     * @return ontology annotations
     */
    Set<Annotation> getOntologyAnnotations();

    /**
     * Gets all the annotations in the ontology, excluding ontology annotations, annotations for other objects such
     * as classes and entities.
     * 
     * @return ontology annotations
     */
    Set<Annotation> getAllAnnotations();

    /**
     * Gets all the annotation properties defined in the ontology.
     *
     * @return ontology annotation properties
     */
    Set<AnnotationProperty> getAllAnnotationProperties();

    Set<OClass> getAllClasses();

    Set<Axiom> getAxioms();
    
    Set<Datatype> getAllDatatypes();
    
    Set<ObjectProperty> getAllObjectProperties();
    
    Set<DataProperty> getAllDataProperties();
    
    Set<Individual> getAllIndividuals();

    /**
     * Compares two SimpleOntology objects by their resource ids (ontologyId) and RDF model of the ontology objects,
     * and returns true if the resource ids are equal and their RDF models are isomorphic.
     *
     * <p>Two models are considered isomorphic if for each of the graphs in one model, an isomorphic graph exists in the
     * other model, and the context identifiers of these graphs are either identical or (in the case of blank nodes)
     * map 1:1 on each other.  RDF graphs are isomorphic graphs if statements from one graphs can be mapped 1:1 on to
     * statements in the other graphs. In this mapping, blank nodes are not considered mapped when having an identical
     * internal id, but are mapped from one graph to the other by looking at the statements in which the blank nodes
     * occur.</p>
     *
     * <p>Note: Depending on the size of the models, this can be an expensive operation.</p>
     *
     * @return true if the resource ids are equal and their RDF models are isomorphic.
     */
    boolean equals(Object obj);

    int hashCode();
}
