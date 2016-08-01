package org.matonto.etl.rest.impl;

/*-
 * #%L
 * org.matonto.etl.rest
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

import net.sf.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.matonto.etl.api.config.ExcelConfig;
import org.matonto.etl.api.config.SVConfig;
import org.matonto.etl.api.delimited.DelimitedConverter;
import org.matonto.etl.api.delimited.MappingManager;
import org.matonto.rdf.api.Resource;
import org.matonto.rdf.core.impl.sesame.LinkedHashModel;
import org.matonto.rest.util.MatontoRestTestNg;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class DelimitedRestImplTest extends MatontoRestTestNg {
    private DelimitedRestImpl rest;

    @Mock
    DelimitedConverter converter;

    @Mock
    MappingManager manager;

    @Override
    protected Application configureApp() throws Exception {
        MockitoAnnotations.initMocks(this);
        rest = new DelimitedRestImpl();
        rest.setDelimitedConverter(converter);
        rest.setMappingManager(manager);

        when(converter.convert(any(SVConfig.class))).thenReturn(new LinkedHashModel());
        when(converter.convert(any(ExcelConfig.class))).thenReturn(new LinkedHashModel());
        when(manager.createMappingIRI(anyString())).thenReturn(String::new);
        when(manager.retrieveMapping(any(Resource.class))).thenReturn(Optional.of(new LinkedHashModel()));

        rest.start();

        return new ResourceConfig()
            .register(rest)
            .register(MultiPartFeature.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    @Test
    public void uploadDelimitedTest() {
        FormDataMultiPart fd;
        Response response;
        String[] files = {
                "test.csv", "test.xls", "test.xlsx"
        };
        for (String file : files) {
            fd = getFileFormData(file);
            response = target().path("delimited-files").request().post(Entity.entity(fd,
                    MediaType.MULTIPART_FORM_DATA));
            String filename = response.readEntity(String.class);

            Assert.assertEquals(200, response.getStatus());
            Assert.assertTrue(Files.exists(Paths.get(DelimitedRestImpl.TEMP_DIR + "/" + filename)));
        }
    }

    @Test
    public void updateNonexistentDelimitedTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".csv";
        FormDataMultiPart fd = getFileFormData("test_updated.csv");
        Response response = target().path("delimited-files/" + fileName).request().put(Entity.entity(fd,
                MediaType.MULTIPART_FORM_DATA));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(Files.exists(Paths.get(DelimitedRestImpl.TEMP_DIR + "/" + fileName)));
    }

    @Test
    public void updateDelimitedReplacesContentTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test.csv", fileName);
        List<String> expectedLines = getCsvResourceLines("test_updated.csv");

        FormDataMultiPart fd = getFileFormData("test_updated.csv");
        Response response = target().path("delimited-files/" + fileName).request().put(Entity.entity(fd,
                MediaType.MULTIPART_FORM_DATA));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(fileName, response.readEntity(String.class));
        List<String> resultLines = Files.readAllLines(Paths.get(DelimitedRestImpl.TEMP_DIR + "/" + fileName));
        Assert.assertEquals(expectedLines.size(), resultLines.size());
        for (int i = 0; i < resultLines.size(); i++) {
            Assert.assertEquals(expectedLines.get(i), resultLines.get(i));
        }
    }

    @Test
    public void getRowsFromCsvWithDefaultsTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test.csv", fileName);
        List<String> expectedLines = getCsvResourceLines("test.csv");
        Response response = target().path("delimited-files/" + fileName).request().get();
        Assert.assertEquals(200, response.getStatus());
        testResultsRows(response, expectedLines, 10);
    }

    @Test
    public void getRowsFromCsvWithParamsTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test_tabs.csv", fileName);
        List<String> expectedLines = getCsvResourceLines("test_tabs.csv");

        int rowNum = 5;
        Response response = target().path("delimited-files/" + fileName).queryParam("rowCount", rowNum)
                .queryParam("separator", "\t").request().get();
        Assert.assertEquals(200, response.getStatus());
        testResultsRows(response, expectedLines, rowNum);
    }

    @Test
    public void nonExistentRowsTest() {
        Response response = target().path("delimited-files/error").request().get();
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void getRowsFromExcelWithDefaultsTest() throws Exception {
        String fileName1 = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName1);
        List<String> expectedLines = getExcelResourceLines("test.xls");
        Response response = target().path("delimited-files/" + fileName1).request().get();
        Assert.assertEquals(200, response.getStatus());
        testResultsRows(response, expectedLines, 10);

        String fileName2 = UUID.randomUUID().toString() + ".xlsx";
        copyResourceToTemp("test.xlsx", fileName2);
        expectedLines = getExcelResourceLines("test.xlsx");
        response = target().path("delimited-files/" + fileName2).request().get();
        Assert.assertEquals(200, response.getStatus());
        testResultsRows(response, expectedLines, 10);
    }

    @Test
    public void getRowsFromExcelWithParamsTest() throws Exception {
        int rowNum = 5;

        String fileName1 = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName1);
        List<String> expectedLines = getExcelResourceLines("test.xls");
        Response response = target().path("delimited-files/" + fileName1).queryParam("rowCount", rowNum).request().get();
        Assert.assertEquals(200, response.getStatus());
        testResultsRows(response, expectedLines, rowNum);

        String fileName2 = UUID.randomUUID().toString() + ".xlsx";
        copyResourceToTemp("test.xlsx", fileName2);
        expectedLines = getExcelResourceLines("test.xlsx");
        response = target().path("delimited-files/" + fileName2).queryParam("rowCount", rowNum).request().get();
        Assert.assertEquals(200, response.getStatus());
        testResultsRows(response, expectedLines, rowNum);
    }

    @Test
    public void mapWithoutMappingTest() {
        String mapping = "";
        Response response = target().path("delimited-files/test.csv/map").queryParam("mappingName", mapping)
                .request().get();
        Assert.assertEquals(400, response.getStatus());

        response = target().path("delimited-files/test.csv/map").request().get();
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void mapCsvWithDefaultsTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test.csv", fileName);
        String body = testMap(fileName, "test", null);
        isJsonld(body);
    }

    @Test
    public void mapCsvWithParamsTest() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("format", "turtle");
        params.put("containsHeaders", true);
        params.put("separator", "\t");
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test_tabs.csv", fileName);

        String body = testMap(fileName, "test", params);
        isNotJsonld(body);
    }

    @Test
    public void mapExcelWithDefaultsTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName);

        String body = testMap(fileName, "test", null);
        isJsonld(body);
    }
    @Test
    public void mapExcelWithParamsTest() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("format", "turtle");
        params.put("containsHeaders", true);

        String fileName = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName);

        String body = testMap(fileName, "test", params);
        isNotJsonld(body);
    }

    @Test
    public void mapNonexistentDelimitedTest() {
        Response response = target().path("delimited-files/error/map").queryParam("mappingName", "test").request()
                .get();
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void mapDeletesFile() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("containsHeaders", true);
        String fileName = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName);

        Assert.assertTrue(Files.exists(Paths.get(DelimitedRestImpl.TEMP_DIR + "/" + fileName)));

        testMap(fileName, "test", params);
        Assert.assertFalse(Files.exists(Paths.get(DelimitedRestImpl.TEMP_DIR + "/" + fileName)));
    }

    @Test
    public void mapPreviewWithoutMappingTest() {
        String mapping = "";
        FormDataMultiPart fd = new FormDataMultiPart();
        fd.field("jsonld", mapping);
        Response response = target().path("delimited-files/test.csv/map-preview").request().post(Entity.entity(fd,
                MediaType.MULTIPART_FORM_DATA));
        Assert.assertEquals(400, response.getStatus());

        response = target().path("delimited-files/test.csv/map-preview").request().post(Entity.entity(null,
                MediaType.MULTIPART_FORM_DATA));
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void mapPreviewCsvWithDefaultsTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test.csv", fileName);
        String body = testMapPreview(fileName, "[]", null);
        isJsonld(body);
    }

    @Test
    public void mapPreviewCsvWithParamsTest() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("format", "turtle");
        params.put("containsHeaders", true);
        params.put("separator", "\t");
        String fileName = UUID.randomUUID().toString() + ".csv";
        copyResourceToTemp("test_tabs.csv", fileName);

        String body = testMapPreview(fileName, "[]", params);
        isNotJsonld(body);
    }

    @Test
    public void mapPreviewExcelWithDefaultsTest() throws Exception {
        String fileName = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName);

        String body = testMapPreview(fileName, "[]", null);
        isJsonld(body);
    }

    @Test
    public void mapPreviewExcelWithParamsTest() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("format", "turtle");
        params.put("containsHeaders", true);
        String fileName = UUID.randomUUID().toString() + ".xls";
        copyResourceToTemp("test.xls", fileName);

        String body = testMapPreview(fileName, "[]", params);
        isNotJsonld(body);
    }

    @Test
    public void mapPreviewNonexistentDelimitedTest() throws Exception {
        FormDataMultiPart fd = new FormDataMultiPart();
        fd.field("jsonld", "[]");
        Response response = target().path("delimited-files/error/map-preview").request().post(Entity.entity(fd,
                MediaType.MULTIPART_FORM_DATA));
        Assert.assertEquals(400, response.getStatus());
    }

    private void isJsonld(String str) {
        try {
            JSONArray result = JSONArray.fromObject(str);
        } catch (Exception e) {
            Assert.fail("Expected no exception, but got: " + e.getMessage());
        }
    }

    private void isNotJsonld(String str) {
        try {
            JSONArray result = JSONArray.fromObject(str);
            Assert.fail();
        } catch (Exception e) {
            System.out.println("Format is not JSON-LD, as expected");
        }
    }

    private String testMapPreview(String fileName, String jsonld, Map<String, Object> params) {
        FormDataMultiPart fd = new FormDataMultiPart();
        fd.field("jsonld", jsonld);
        WebTarget wt = target().path("delimited-files/" + fileName + "/map-preview");
        if (params != null) {
            for (String k : params.keySet()) {
                wt = wt.queryParam(k, params.get(k));
            }
        }
        Response response = wt.request().post(Entity.entity(fd, MediaType.MULTIPART_FORM_DATA));
        Assert.assertEquals(200, response.getStatus());
        return response.readEntity(String.class);
    }

    private String testMap(String fileName, String mappingName, Map<String, Object> params) {
        WebTarget wt = target().path("delimited-files/" + fileName + "/map").queryParam("mappingName", mappingName);
        if (params != null) {
            for (String k : params.keySet()) {
                wt = wt.queryParam(k, params.get(k));
            }
        }
        Response response = wt.request().get();
        Assert.assertEquals(200, response.getStatus());
        return response.readEntity(String.class);
    }

    private void testResultsRows(Response response, List<String> expectedLines, int rowNum) {
        String body = response.readEntity(String.class);
        JSONArray lines = JSONArray.fromObject(body);
        Assert.assertEquals(rowNum + 1, lines.size());
        for (int i = 0; i < lines.size(); i++) {
            JSONArray line = lines.getJSONArray(i);
            String expectedLine = expectedLines.get(i);
            for (Object item : line) {
                Assert.assertTrue(expectedLine.contains(item.toString()));
            }
        }
    }

    private List<String> getCsvResourceLines(String fileName) throws Exception {
        return IOUtils.readLines(getClass().getClassLoader().getResourceAsStream(fileName));
    }

    private List<String> getExcelResourceLines(String fileName) {
        List<String> expectedLines = new ArrayList<>();
        try {
            Workbook wb = WorkbookFactory.create(getClass().getResourceAsStream("/" + fileName));
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter df = new DataFormatter();
            int index = 0;
            for (Row row : sheet) {
                String rowStr = "";
                for (Cell cell : row) {
                    rowStr += df.formatCellValue(cell);
                }
                expectedLines.add(index, rowStr);
                index++;
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return expectedLines;
    }

    private FormDataMultiPart getFileFormData(String resourceName) {
        FormDataMultiPart fd = new FormDataMultiPart();
        InputStream content = getClass().getResourceAsStream("/" + resourceName);
        fd.bodyPart(new FormDataBodyPart(FormDataContentDisposition.name("delimitedFile").fileName(resourceName).build(),
                content, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        return fd;
    }

    private void copyResourceToTemp(String resourceName, String newName) throws IOException {
        Files.copy(getClass().getResourceAsStream("/" + resourceName),
                Paths.get(DelimitedRestImpl.TEMP_DIR + "/" + newName), StandardCopyOption.REPLACE_EXISTING);
    }
}