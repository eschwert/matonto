package org.matonto.etl.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/csv")
@Api( value = "/csv" )
public interface CSVRest {

    /**
     * Uploads a delimited document to the data/tmp/ directory.
     *
     * @param fileInputStream an InputStream of a delimited document passed as form data
     * @param fileDetail information about the file being uploaded, including the name
     * @return a response with the name of the file created on the server
    */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Upload delimited file sent as form data.")
    Response upload(@FormDataParam("delimitedFile") InputStream fileInputStream,
                    @FormDataParam("delimitedFile")FormDataContentDisposition fileDetail);

    /**
     * Replaces an uploaded delimited document in the data/tmp/ directory with another
     * delimited file.
     *
     * @param fileInputStream an InputStream of a delimited document passed as form data
     * @param fileDetail information about the file being uploaded, including the name
     * @param fileName the name of the uploaded file on the server to replace
     * @return a response with the name of the file replaced on the server
     */
    @PUT
    @Path("{documentName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Replace an uploaded delimited file with another")
    Response upload(@FormDataParam("delimitedFile") InputStream fileInputStream,
                    @FormDataParam("delimitedFile")FormDataContentDisposition fileDetail,
                    @PathParam("documentName") String fileName);

    /**
     * Retrieves a preview of the first specified number of rows of an uploaded
     * delimited document using the specified separator. The file must be present
     * in the data/tmp/ directory.
     *
     * @param fileName the name of the delimited document in the data/tmp/ directory
     * @param rowEnd the number of rows to retrieve from the delimited document. NOTE:
     *               the default number of rows is 10
     * @param separator the character the columns are separated by
     * @return a response with a JSON array. Each element in the array is a row in the
     *         document. The row is an array of strings which are the cells in the row
     *         in the document.
     */
    @GET
    @Path("{documentName}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Gather rows from an uploaded delimited document.")
    Response getRows(@PathParam("documentName") String fileName,
                     @DefaultValue("10") @QueryParam("Row-Count") int rowEnd,
                     @DefaultValue(",") @QueryParam("Separator") String separator);

    /**
     * Maps the data in an uploaded delimited document into RDF in JSON-LD format
     * using either an uploaded mapping in the data store or a JSON-LD mapping string.
     * The file must be present in the data/tmp/ directory.
     *
     * @param fileName the name of the delimited document in the data/tmp/ directory
     * @param jsonld a mapping in JSON-LD
     * @param mappingLocalName the local name of an uploaded mapping
     * @param format the RDF serialization to use if getting a preview
     * @param isPreview whether or not a preview of the mapped data should be returned
     * @param containsHeaders whether the delimited file has headers
     * @param separator the character the columns are separated by if it is a CSV
     * @return a response with a JSON object containing the mapping file name and a
     *      JSON-LD string containing the converted data
    */
    @POST
    @Path("{documentName}/map")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    @ApiOperation("ETL an uploaded delimited document using an uploaded mapping file or mapping JSON-LD")
    Response etlFile(@PathParam("documentName") String fileName, 
            @FormDataParam("jsonld") String jsonld,
            @FormDataParam("mappingName") String mappingLocalName,
            @DefaultValue("jsonld") @QueryParam("Format") String format,
            @DefaultValue("false") @QueryParam("Preview") boolean isPreview,
            @DefaultValue("true") @QueryParam("Contains-Headers") boolean containsHeaders,
            @DefaultValue(",") @QueryParam("Separator") String separator);
}