package org.matonto.etl.service.csv;

import aQute.bnd.annotation.component.*;
import org.matonto.etl.api.csv.MappingManager;
import org.matonto.exception.MatOntoException;
import org.matonto.rdf.api.*;
import org.matonto.rdf.core.utils.Values;
import org.matonto.repository.api.Repository;
import org.matonto.repository.api.RepositoryConnection;
import org.matonto.repository.api.RepositoryManager;
import org.matonto.repository.base.RepositoryResult;
import org.matonto.repository.exception.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.annotation.Nonnull;

@Component (immediate = true,
        provide = MappingManager.class,
        name = SimpleMappingManager.COMPONENT_NAME,
        configurationPolicy = ConfigurationPolicy.require)
public class SimpleMappingManager implements MappingManager {
    protected static final String COMPONENT_NAME = "org.matonto.etl.api.MappingManager";
    private Resource mappingRegistryResource;
    private IRI registryPredicate;
    private static final Logger logger = LoggerFactory.getLogger(SimpleMappingManager.class);
    private RepositoryManager repositoryManager;
    private static Repository repository;
    private ValueFactory factory;
    private ModelFactory modelFactory;

    public SimpleMappingManager() {}

    @Activate
    public void activate(final Map<String, Object> properties) {
        logger.info("Activating " + COMPONENT_NAME);
        setPropertyValues(properties);
    }

    @Deactivate
    public void deactivate() {
        logger.info("Deactivating " + COMPONENT_NAME);
    }

    @Modified
    public void modified(final Map<String, Object> properties) {
        logger.info("Modifying the " + COMPONENT_NAME);
        setPropertyValues(properties);
    }

    @Reference
    protected void setValueFactory(final ValueFactory vf) {
        factory = vf;
    }

    @Reference
    protected void setModelFactory(final ModelFactory mf) {
        modelFactory = mf;
    }

    @Reference
    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public Set<Resource> getMappingRegistry() {
        testRepositoryConnection();
        RepositoryConnection conn = null;
        Set<Resource> registry = new HashSet<>();
        try {
            conn = repository.getConnection();
            conn.getStatements(mappingRegistryResource, registryPredicate, null, mappingRegistryResource)
                    .forEach(statement -> registry.add(factory.createIRI(statement.getObject().stringValue())));
        } catch (RepositoryException e) {
            throw new MatOntoException("Error in repository connection", e);
        } finally {
            closeConnection(conn);
        }

        return registry;
    }

    @Override
    public Resource createMappingIRI() {
        String localName = generateUuid();
        return factory.createIRI(Delimited.MAPPING.stringValue() + "/" + localName.trim());
    }

    @Override
    public Resource createMappingIRI(String localName) {
        return factory.createIRI(Delimited.MAPPING.stringValue() + "/" + localName.trim());
    }

    @Override
    public Model createMapping(File mapping) throws IOException {
        RDFFormat mapFormat;
        mapFormat = Rio.getParserFormatForFileName(mapping.getName()).orElseThrow(IllegalArgumentException::new);
        FileReader reader = new FileReader(mapping);
        return Values.matontoModel(Rio.parse(reader, "", mapFormat));
    }

    @Override
    public Model createMapping(String jsonld) throws IOException {
        InputStream in = new ByteArrayInputStream(jsonld.getBytes(StandardCharsets.UTF_8));
        return Values.matontoModel(Rio.parse(in, "", RDFFormat.JSONLD));
    }

    @Override
    public Model createMapping(InputStream in, RDFFormat format) throws IOException {
        return Values.matontoModel(Rio.parse(in, "", format));
    }

    @Override
    public boolean storeMapping(Model mappingModel, @Nonnull Resource mappingIRI) throws MatOntoException {
        testRepositoryConnection();
        if (mappingExists(mappingIRI)) {
            throw new MatOntoException("Mapping with mapping ID already exists");
        }

        RepositoryConnection conn = null;
        try {
            conn = repository.getConnection();
            conn.add(mappingModel, mappingIRI);
            conn.add(mappingRegistryResource, registryPredicate, mappingIRI, mappingRegistryResource);
        } catch (RepositoryException e) {
            throw new MatOntoException("Error in repository connection", e);
        } finally {
            closeConnection(conn);
        }

        return true;
    }

    @Override
    public Optional<Model> retrieveMapping(@Nonnull Resource mappingIRI) {
        testRepositoryConnection();
        if (!mappingExists(mappingIRI)) {
            return Optional.empty();
        }
        RepositoryConnection conn = null;
        Model mappingModel = modelFactory.createModel();
        try {
            conn = repository.getConnection();
            RepositoryResult<Statement> statements = conn.getStatements(null, null, null, mappingIRI);
            statements.forEach(mappingModel::add);
        } catch (RepositoryException e) {
            throw new MatOntoException("Error in repository connection", e);
        } finally {
            closeConnection(conn);
        }
        return Optional.of(mappingModel);
    }

    @Override
    public boolean deleteMapping(@Nonnull Resource mappingIRI) {
        testRepositoryConnection();
        if (!mappingExists(mappingIRI)) {
            throw new MatOntoException("Mapping with mapping ID does not exist");
        }

        RepositoryConnection conn = null;
        try {
            conn = repository.getConnection();
            conn.clear(mappingIRI);
            conn.remove(mappingRegistryResource, registryPredicate, mappingIRI, mappingRegistryResource);
        } catch (RepositoryException e) {
            throw new MatOntoException("Error in repository connection", e);
        } finally {
            closeConnection(conn);
        }

        return true;
    }

    protected void getRepository(String repositoryId) {
        if (repositoryManager == null) {
            throw new IllegalStateException("Repository Manager is null");
        }

        Optional<Repository> optRepo = repositoryManager.getRepository(repositoryId);
        if (optRepo.isPresent()) {
            setRepo(optRepo.get());
        } else {
            throw new IllegalStateException("Repository does not exist");
        }
    }

    protected void setRepo(Repository repo) {
        repository = repo;
    }

    protected boolean mappingExists(@Nonnull Resource resource) {
        testRepositoryConnection();

        RepositoryConnection conn = null;
        boolean exists = false;
        try {
            conn = repository.getConnection();
            RepositoryResult<Statement> statements = conn.getStatements(mappingRegistryResource, registryPredicate,
                    resource, mappingRegistryResource);
            if (statements.hasNext()) {
                exists = true;
            }
        } catch (RepositoryException e) {
            throw new MatOntoException("Error in repository connection", e);

        } finally {
            closeConnection(conn);
        }
        return exists;
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private void setPropertyValues(Map<String, Object> properties) {
        if (properties.containsKey("repositoryId") && !properties.get("repositoryId").equals("")) {
            getRepository((String)properties.get("repositoryId"));
            logger.info("repositoryId - " + properties.get("repositoryId"));
            initMappingRegistryResources();
        } else {
            logger.error("Unable to activate Mapping Manager: Unable to set repositoryId");
            throw new IllegalStateException("Unable to set repositoryId");
        }
    }

    private void closeConnection(RepositoryConnection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (RepositoryException e) {
            logger.warn("Could not close Repository." + e.toString());
        }
    }

    private void testRepositoryConnection() {
        if (repository == null) {
            throw new IllegalStateException("Repository is null");
        }
    }

    private void initMappingRegistryResources() throws MatOntoException {
        mappingRegistryResource = factory.createIRI("https://matonto.orgregistries/mappings");
        registryPredicate = factory.createIRI("https://matonto.org/registries#hasItem");
    }
}