package org.matonto.catalog.impl;

/*-
 * #%L
 * org.matonto.catalog.impl
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

import org.junit.Before;
import org.junit.Test;
import org.matonto.catalog.api.Distribution;
import org.matonto.catalog.api.Ontology;
import org.matonto.catalog.api.PublishedResource;
import org.matonto.query.TupleQueryResult;
import org.matonto.query.api.BindingSet;
import org.matonto.query.api.TupleQuery;
import org.matonto.rdf.api.*;
import org.matonto.rdf.core.impl.sesame.LinkedHashModelFactory;
import org.matonto.rdf.core.impl.sesame.LinkedHashNamedGraphFactory;
import org.matonto.rdf.core.impl.sesame.SimpleIRI;
import org.matonto.rdf.core.utils.Values;
import org.matonto.repository.api.Repository;
import org.matonto.repository.api.RepositoryConnection;
import org.matonto.repository.base.RepositoryResult;
import org.matonto.repository.impl.sesame.query.SesameBindingSet;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.impl.MapBindingSet;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimpleCatalogManagerTest {

    @Mock
    Repository repo;

    @Mock
    RepositoryConnection conn;

    @Mock
    TupleQuery query;

    @Mock
    TupleQueryResult result;

    @Mock
    RepositoryResult<Statement> repositoryResult;

    private SimpleCatalogManager manager;
    private NamedGraphFactory ngf = LinkedHashNamedGraphFactory.getInstance();
    private org.matonto.rdf.api.ValueFactory vf = org.matonto.rdf.core.impl.sesame.SimpleValueFactory.getInstance();
    private ModelFactory mf = LinkedHashModelFactory.getInstance();

    private static final String DC = "http://purl.org/dc/terms/";
    private static final String DCAT = "http://www.w3.org/ns/dcat#";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        manager = new SimpleCatalogManager();
        manager.setRepository(repo);
        manager.setNamedGraphFactory(ngf);
        manager.setValueFactory(vf);
        manager.setModelFactory(mf);

        when(repo.getConnection()).thenReturn(conn);
        when(conn.prepareTupleQuery(anyString())).thenReturn(query);
        when(conn.getStatements(any(), any(), any(), any())).thenReturn(repositoryResult);
        when(query.evaluate()).thenReturn(result);
    }

    @Test
    public void testGetMissingResource() throws Exception {
        // given
        // when
        Optional<PublishedResource> optional = manager.getResource(mock(IRI.class));

        // then
        assertEquals(Optional.empty(), optional);
    }

    @Test
    public void testGetExistingResourceWithNoOptionals() throws Exception {
        // given
        IRI existingResource = mock(IRI.class);
        GregorianCalendar issued = new GregorianCalendar(2016, 1, 1);
        GregorianCalendar modified = new GregorianCalendar(2016, 1, 2);

        ValueFactory valueFactory = SimpleValueFactory.getInstance();
        MapBindingSet sesameBindingSet = new MapBindingSet();
        sesameBindingSet.addBinding("resource", valueFactory.createIRI("http://matonto.org/testCatalog"));
        sesameBindingSet.addBinding("title", valueFactory.createLiteral("Test Resource"));
        sesameBindingSet.addBinding("types", valueFactory.createIRI("http://matonto.org/ontologies/catalog#Ontology"));
        sesameBindingSet.addBinding("issued", valueFactory.createLiteral(issued.getTime()));
        sesameBindingSet.addBinding("modified", valueFactory.createLiteral(modified.getTime()));
        BindingSet bindingSet = new SesameBindingSet(sesameBindingSet);

        List<String> bindingNames = new ArrayList<>();
        bindingNames.add("resource");
        bindingNames.add("title");
        bindingNames.add("types");
        bindingNames.add("issued");
        bindingNames.add("modified");

        // when
        when(result.hasNext()).thenReturn(true);
        when(result.next()).thenReturn(bindingSet);
        when(result.getBindingNames()).thenReturn(bindingNames);

        Optional<PublishedResource> optional = manager.getResource(existingResource);

        // then
        assertNotEquals(Optional.empty(), optional);

        PublishedResource resource = optional.get();

        assertEquals("Test Resource", resource.getTitle());
        assertTrue(resource.getTypes().contains(vf.createIRI("http://matonto.org/ontologies/catalog#Ontology")));
        assertEquals(issued.toZonedDateTime().toOffsetDateTime(), resource.getIssued());
        assertEquals(modified.toZonedDateTime().toOffsetDateTime(), resource.getModified());
    }

    @Test
    public void testCreateOntologyWithoutDistribution() throws Exception {
        // given
        IRI ontologyIri = new SimpleIRI("http://matonto.org/catalog/1");
        OffsetDateTime now = OffsetDateTime.now();
        Set<Resource> types = new HashSet<>();
        types.add(new SimpleIRI("http://matonto.org/ontologies/catalog#Ontology"));

        Ontology ontology = mock(Ontology.class);

        // when
        when(repositoryResult.hasNext()).thenReturn(false);

        when(ontology.getResource()).thenReturn(ontologyIri);
        when(ontology.getTypes()).thenReturn(types);
        when(ontology.getTitle()).thenReturn("MatOnto Catalog");
        when(ontology.getDescription()).thenReturn("Catalog of MatOnto Resources");
        when(ontology.getIssued()).thenReturn(now);
        when(ontology.getModified()).thenReturn(now);

        manager.createOntology(ontology);

        // then
        NamedGraph expectedGraph = ngf.createNamedGraph(ontologyIri);
        expectedGraph.add(ontologyIri, Values.matontoIRI(RDF.TYPE), vf.createIRI("http://matonto.org/ontologies/catalog#Ontology"));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "title"), vf.createLiteral("MatOnto Catalog"));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "description"), vf.createLiteral("Catalog of MatOnto Resources"));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "issued"), vf.createLiteral(now));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "modified"), vf.createLiteral(now));

        ArgumentCaptor<NamedGraph> argument = ArgumentCaptor.forClass(NamedGraph.class);
        verify(conn).add(argument.capture());
        checkNamedGraphs(expectedGraph, argument.getValue());
    }

    @Test
    public void testCreateOntologyWithDistribution() throws Exception {
        // given
        IRI ontologyIri = new SimpleIRI("http://matonto.org/catalog/1");
        IRI distributionIri = new SimpleIRI("http://matonto.org/distribution/1");
        OffsetDateTime now = OffsetDateTime.now();
        Set<Resource> types = new HashSet<>();
        types.add(new SimpleIRI("http://matonto.org/ontologies/catalog#Ontology"));

        Ontology ontology = mock(Ontology.class);
        Distribution distribution = mock(Distribution.class);
        Set<Distribution> distributions = new HashSet<>();
        distributions.add(distribution);

        // when
        when(repositoryResult.hasNext()).thenReturn(false);

        when(ontology.getResource()).thenReturn(ontologyIri);
        when(ontology.getTypes()).thenReturn(types);
        when(ontology.getTitle()).thenReturn("MatOnto Catalog");
        when(ontology.getDescription()).thenReturn("Catalog of MatOnto Resources");
        when(ontology.getIssued()).thenReturn(now);
        when(ontology.getModified()).thenReturn(now);
        when(ontology.getDistributions()).thenReturn(distributions);

        when(distribution.getResource()).thenReturn(distributionIri);

        manager.createOntology(ontology);

        // then
        NamedGraph expectedGraph = ngf.createNamedGraph(ontologyIri);
        expectedGraph.add(ontologyIri, Values.matontoIRI(RDF.TYPE), vf.createIRI("http://matonto.org/ontologies/catalog#Ontology"));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "title"), vf.createLiteral("MatOnto Catalog"));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "description"), vf.createLiteral("Catalog of MatOnto Resources"));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "issued"), vf.createLiteral(now));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "modified"), vf.createLiteral(now));
        expectedGraph.add(ontologyIri, vf.createIRI(DC + "modified"), vf.createLiteral(now));
        expectedGraph.add(ontologyIri, vf.createIRI(DCAT + "distribution"), distributionIri);

        ArgumentCaptor<NamedGraph> argument = ArgumentCaptor.forClass(NamedGraph.class);
        verify(conn).add(argument.capture());
        checkNamedGraphs(expectedGraph, argument.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOntologyThrowsExceptionWhenAlreadyExists() throws Exception {
        // given
        Ontology ontology = mock(Ontology.class);

        // when
        when(repositoryResult.hasNext()).thenReturn(true);
        when(ontology.getResource()).thenReturn(mock(IRI.class));

        manager.createOntology(ontology);
    }

    // TODO: Test and Implement Remove
//    @Test
//    public void testRemoveResource() {
//        // given
//        Ontology ontology = mock(Ontology.class);
//
//        // when
////        when(repositoryResult.hasNext()).thenReturn(true);
////        when(ontology.getResource()).thenReturn(mock(IRI.class));
//
//        manager.removeResource(ontology);
//    }

    private void checkNamedGraphs(NamedGraph expected, NamedGraph actual) throws Exception {
        try {
            assertEquals(expected, actual);
        } catch (AssertionError e) {
            // Print contents to aid in fix
            System.out.println("Expected Graph:");
            expected.forEach(System.out::println);
            System.out.println("Actual Graph:");
            actual.forEach(System.out::println);
            throw e;
        }
    }
}
