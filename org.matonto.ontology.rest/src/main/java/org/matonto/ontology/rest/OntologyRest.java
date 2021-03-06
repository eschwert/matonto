package org.matonto.ontology.rest;

/*-
 * #%L
 * org.matonto.ontology.rest
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ontologies")
@Api( value = "/ontologies" )
public interface OntologyRest {

    /**
     * Returns all ontology Resource identifiers.
     *
     * @return all ontology Resource identifiers.
     */
    @GET
    @Path("ontologyids")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    @ApiOperation(value = "Gets all Ontology Resource identifiers")
    Response getAllOntologyIds();

    /**
     * Returns JSON-formatted ontologies with requested ontology IDs; The ontology id list
     * is provided as a comma separated string. NOTE: If an ontology in the list does not exist,
     * it will be excluded from the response.
     *
     * @param ontologyIdList a comma separated String representing the ontology ids
     * @return all ontologies specified by ontologyIdList in JSON-LD format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getOntologies(@QueryParam("ontologyids") String ontologyIdList);

    /**
     * Ingests/uploads an ontology file to a data store.
     *
     * @param fileInputStream The ontology file to upload
     * @return true if persisted, false otherwise
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response uploadFile(@FormDataParam("file") InputStream fileInputStream);

    /**
     * Ingests an ontology json-ld string to a data store.
     *
     * @param ontologyJson The ontology json-ld to upload
     * @return true if persisted, false otherwise
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response uploadOntologyJson(@QueryParam("ontologyjson") String ontologyJson);

    /**
     * Returns ontology with requested ontology ID in the requested format
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param rdfFormat the desired RDF return format. NOTE: Optional param - defaults to "jsonld".
     * @return ontology with requested ontology ID in the requested format
     */
    @GET
    @Path("{ontologyid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getOntology(@PathParam("ontologyid") String ontologyIdStr,
                         @DefaultValue("jsonld") @QueryParam("rdfformat") String rdfFormat);

    /**
     * Streams the ontology with requested ontology ID to an OutputStream.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param rdfFormat the desired RDF return format. NOTE: Optional param - defaults to "jsonld".
     * @return the ontology with requested ontology ID to download.
     */
    @GET
    @Path("{ontologyid}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM, "text/*", "application/*"})
    @RolesAllowed("user")
    Response downloadOntologyFile(@PathParam("ontologyid") String ontologyIdStr,
                                  @DefaultValue("jsonld") @QueryParam("rdfFormat") String rdfFormat,
                                  @DefaultValue("ontology") @QueryParam("fileName") String fileName);

    /**
     * Replaces the ontology's resource with the new data
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param resourceIdStr the String representing the edited Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param resourceJson the String representing the edited Resource.
     * @return true if updated, false otherwise
     */
    @POST
    @Path("{ontologyid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response saveChangesToOntology(@PathParam("ontologyid") String ontologyIdStr,
                                   @QueryParam("resourceid") String resourceIdStr,
                                   @QueryParam("resourcejson") String resourceJson);

    /**
     * Delete ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return true if deleted, false otherwise.
     */
    @DELETE
    @Path("{ontologyid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response deleteOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns IRIs in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return IRIs in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/iris")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getIRIsInOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns annotation properties in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return annotation properties in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/annotations")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getAnnotationsInOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Create a new owl annotation property in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param annotationJson the String representing the new annotation in JSON-LD.
     * @return annotation properties in the ontology with requested ontology ID.
     */
    @POST
    @Path("{ontologyid}/annotations")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response addAnnotationToOntology(@PathParam("ontologyid") String ontologyIdStr,
                                     @QueryParam("annotationjson") String annotationJson);

    /**
     * Returns classes in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return classes in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/classes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getClassesInOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Add class to ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param resourceJson the String representing the new class model.
     * @return true if added, false otherwise.
     */
    @POST
    @Path("{ontologyid}/classes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response addClassToOntology(@PathParam("ontologyid") String ontologyIdStr,
                                @QueryParam("resourcejson") String resourceJson);

    /**
     * Delete class with requested class ID from ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param classIdStr the String representing the class Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return true if deleted, false otherwise.
     */
    @DELETE
    @Path("{ontologyid}/classes/{classid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response deleteClassFromOntology(@PathParam("ontologyid") String ontologyIdStr,
                                      @PathParam("classid") String classIdStr);

    /**
     * Returns datatypes in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return datatypes in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/datatypes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getDatatypesInOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns object properties in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return object properties in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/object-properties")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getObjectPropertiesInOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Adds the object property to the ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param resourceJson the String representing the new property model.
     * @return true if added, false otherwise.
     */
    @POST
    @Path("{ontologyid}/object-properties")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response addObjectPropertyToOntology(@PathParam("ontologyid") String ontologyIdStr,
                                         @QueryParam("resourcejson") String resourceJson);

    /**
     * Delete object property with requested class ID from ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param propertyIdStr the String representing the class Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return true if deleted, false otherwise.
     */
    @DELETE
    @Path("{ontologyid}/object-properties/{propertyid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response deleteObjectPropertyFromOntology(@PathParam("ontologyid") String ontologyIdStr,
                                      @PathParam("propertyid") String propertyIdStr);

    /**
     * Returns data properties in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return data properties in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/data-properties")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getDataPropertiesInOntology(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Adds the data property to the ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param resourceJson the String representing the new property model.
     * @return true if added, false otherwise.
     */
    @POST
    @Path("{ontologyid}/data-properties")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response addDataPropertyToOntology(@PathParam("ontologyid") String ontologyIdStr,
                                       @QueryParam("resourcejson") String resourceJson);

    /**
     * Delete data property with requested class ID from ontology with requested ontology ID from the server.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param propertyIdStr the String representing the class Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return true if deleted, false otherwise.
     */
    @DELETE
    @Path("{ontologyid}/data-properties/{propertyid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response deleteDataPropertyFromOntology(@PathParam("ontologyid") String ontologyIdStr,
                                              @PathParam("propertyid") String propertyIdStr);

    /**
     * Returns named individuals in the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return named individuals in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/named-individuals")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getNamedIndividualsInOntology(@PathParam("ontologyid") String ontologyIdStr);

    @POST
    @Path("{ontologyid}/named-individuals")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response addIndividualToOntology(@PathParam("ontologyid") String ontologyIdStr,
                                     @QueryParam("resourcejson") String resourceJson);

    @DELETE
    @Path("{ontologyid}/named-individuals/{individualid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response deleteIndividualFromOntology(@PathParam("ontologyid") String ontologyIdStr,
                                          @PathParam("individualid") String individualIdStr);
    
    /**
     * Returns IRIs in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return IRIs in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-iris")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getIRIsInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns an array of the imports closure in the requested format from the ontology
     * with the requested ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param rdfFormat the desired RDF return format. NOTE: Optional param - defaults to "jsonld".
     * @return array of imported ontologies from the ontology with the requested ID in the requested format
     */
    @GET
    @Path("{ontologyid}/imported-ontologies")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    @ApiOperation(value = "Retrieves the JSON-LD of all directly imported ontologies")
    Response getImportsClosure(@PathParam("ontologyid") String ontologyIdStr,
                                     @DefaultValue("jsonld") @QueryParam("rdfformat") String rdfFormat);

    /**
     * Returns annotation properties in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return annotation properties in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-annotations")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getAnnotationsInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns classes in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return classes in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-classes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getClassesInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns datatypes in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return datatypes in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-datatypes")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getDatatypesInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns object properties in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return object properties in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-object-properties")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getObjectPropertiesInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns data properties in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return data properties in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-data-properties")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getDataPropertiesInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns named individuals in the direct imported ontologies of the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return named individuals in the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/imported-named-individuals")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getNamedIndividualsInImportedOntologies(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns the JSON class hierarchy for the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return nested JSON structure that represents the class hierarchy for the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/class-hierarchies")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getOntologyClassHierarchy(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns the JSON object property hierarchy for the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return nested JSON structure that represents the object property hierarchy for the ontology with requested
     *         ontology ID.
     */
    @GET
    @Path("{ontologyid}/object-property-hierarchies")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getOntologyObjectPropertyHierarchy(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns the JSON data property hierarchy for the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return nested JSON structure that represents the data property hierarchy for the ontology with requested
     *         ontology ID.
     */
    @GET
    @Path("{ontologyid}/data-property-hierarchies")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getOntologyDataPropertyHierarchy(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns classes with individuals defined in the ontology with the requested ontologyID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return nested JSON structure that represents the class hierarchy for the ontology with requested ontology ID.
     */
    @GET
    @Path("{ontologyid}/classes-with-individuals")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getClassesWithIndividuals(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns JSON SPARQL query results containing statements with the requested entity IRI as the predicate of
     * each statement.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param entityIRIStr the String representing the entity Resource IRI.
     * @return JSON SPARQL query results.
     */
    @GET
    @Path("{ontologyid}/entity-usages/{entityiri}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getEntityUsages(@PathParam("ontologyid") String ontologyIdStr,
                             @PathParam("entityiri") String entityIRIStr);

    /**
     * Returns the JSON SKOS concept hierarchy for the ontology with requested ontology ID.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @return nested JSON structure that represents the SKOS concept hierarchy for the ontology with requested
     *         ontology ID.
     */
    @GET
    @Path("{ontologyid}/concept-hierarchies")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getConceptHierarchy(@PathParam("ontologyid") String ontologyIdStr);

    /**
     * Returns the JSON String of the resulting entities sorted by type from the ontology with the requested ontology ID
     * that have statements which contain the requested searchText in a Literal Value.
     *
     * @param ontologyIdStr the String representing the ontology Resource id. NOTE: Assumes id represents
     *                      an IRI unless String begins with "_:".
     * @param searchText the String for the text that is searched for in all of the Literals within the ontology with
     *                   the requested ontology ID.
     * @return JSON String providing the sorted list of results from the search.
     */
    @GET
    @Path("{ontologyid}/search-results")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    Response getSearchResults(@PathParam("ontologyid") String ontologyIdStr,
                              @QueryParam("searchText") String searchText);
}
