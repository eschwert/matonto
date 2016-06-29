package org.matonto.etl.service.csv

import org.matonto.rdf.api.Model
import org.matonto.rdf.core.impl.sesame.LinkedHashModelFactory
import org.matonto.rdf.core.impl.sesame.SimpleValueFactory
import org.matonto.rdf.core.utils.Values;

/*-
 * #%L
 * org.matonto.etl.csv
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
import org.openrdf.rio.RDFFormat
import org.openrdf.rio.RDFParseException
import org.openrdf.rio.Rio
import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

class ConverterSpec extends Specification {

    def mf = LinkedHashModelFactory.getInstance();
    def vf = SimpleValueFactory.getInstance();
    def mm = new SimpleMappingManager();

    def c = Spy(CSVConverterImpl)

    def out = new ClassPathResource("testOutput.ttl").getFile();
    def r = new FileReader(out);

    def setup() {
        c.setValueFactory(vf)
        c.setModelFactory(mf);
        c.setMappingManager(mm);
    }

    def "Convert CSV File with Multiple Object per Row and Object and Data Properties"() {
        setup:
        File csv = new ClassPathResource("testFile.csv").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(csv, mappingFile, true, (char) ',');

        expect:
        m.equals(convertedModel);
    }

    def "Test non-comma separator"(){
        setup:
        File csv = new ClassPathResource("semicolonFile.csv").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(csv, mappingFile, true, (char) ';')

        expect:
        m.equals(convertedModel);
    }

    def "Tab Separated"(){
        setup:
        File csv = new ClassPathResource("tabFile.csv").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(csv, mappingFile, true, (char) '\t')

        expect:
        m.equals(convertedModel);
    }

    def "Mapping with default Local Name"(){
        setup:
        File csv = new ClassPathResource("testFile.csv").getFile();
        File mappingFile = new ClassPathResource("mappingNoLocalName.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(csv, mappingFile, true, (char) ',')

        expect:
        m.equals(convertedModel);
    }

    def "Without headers"() {
        setup:
        File csv = new ClassPathResource("testFileNoHeaders.csv").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(csv, mappingFile, false, (char) ',');

        expect:
        m.equals(convertedModel);
    }

    def "Convert Excel 97-2003 File with Multiple Object per Row and Object and Data Properties"() {
        setup:
        File xls = new ClassPathResource("testFile.xls").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(xls, mappingFile, true, (char) ',');

        expect:
        m.equals(convertedModel);
    }

    def "Convert Excel 2007 File with Multiple Object per Row and Object and Data Properties"() {
        setup:
        File xls = new ClassPathResource("testFile.xlsx").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(xls, mappingFile, true, (char) ',');

        expect:
        m.equals(convertedModel);
    }

    def "Test Generation of Local Name"(){
        setup:
        String[] nextLine = ["abcd","efgh","ijkl","mnop","qrst"]
        c.generateUuid() >> "12345"

        expect:
        result.equals(c.generateLocalName(localName, nextLine))

        where:
        result              | localName
        "12345"             | "\${UUID}"
        "12345/abcd"        | "\${UUID}/\${0}"
        "abcd"              | "\${0}"
        "abcd/12345"        | "\${0}/\${UUID}"
        "abcd/12345/ijkl"   | "\${0}/\${UUID}/\${2}"
        "abcd/abcd"         | "\${0}/\${0}"
        "12345"             | ""
    }

    def "Invalid RDF Causes RDFParseException"(){
        setup:
        File csv = new ClassPathResource("testFile.csv").getFile();
        File mappingFile = new ClassPathResource("testInvalidMapping.ttl").getFile();

        when:
        c.convert(csv, mappingFile, true, (char) ',');

        then:
        thrown RDFParseException;
    }

    def "Convert File with Missing Properties Ignored"() {
        setup:
        File csv = new ClassPathResource("testPropertiesMissing.csv").getFile();
        File mappingFile = new ClassPathResource("newestMapping.ttl").getFile();
        out = new ClassPathResource("testPropertiesMissingOut.ttl").getFile();
        r = new FileReader(out);
        c.generateUuid() >>> ["abc", "bcd", "cdf", "dfg", "fgh", "ghi", "hij", "ijk", "jkl", "klm", "lmn", "nop", "pqr", "rst", "tuv", "vwx", "xyz", "123", "345"]
        Model m = Values.matontoModel(Rio.parse(r, "", RDFFormat.TURTLE))
        Model convertedModel = c.convert(csv, mappingFile, true, (char) ',');

        expect:
        m.equals(convertedModel);
    }
}