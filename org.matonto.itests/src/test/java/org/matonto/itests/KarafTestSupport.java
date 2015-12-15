package org.matonto.itests;

import org.apache.karaf.features.BootFinished;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.junit.Assert;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.osgi.framework.*;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;

import javax.inject.Inject;
import javax.security.auth.Subject;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

public class KarafTestSupport {

    public static final String RMI_SERVER_PORT = "44445";
    public static final String HTTP_PORT = "9081";
    public static final String RMI_REG_PORT = "1100";
    public static final String SSH_PORT = "8102";
    public static final String NEXUS = "http://nexus.inovexcorp.com/nexus/content/groups/public/";

    static final Long COMMAND_TIMEOUT = 30000L;
    static final Long SERVICE_TIMEOUT = 30000L;

    @Inject
    protected BundleContext bundleContext;

    @Inject
    protected FeaturesService featureService;

    @Inject
    ConfigurationAdmin configAdmin;

    ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * To make sure the tests run only when the boot features are fully installed
     */
    @Inject
    BootFinished bootFinished;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

    @Configuration
    public Option[] config() throws IOException {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .versionAsInProject()
                .type("tar.gz");

        List<Option> options = new ArrayList<>();

        options.addAll(Arrays.asList(
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target/exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                logLevel(LogLevel.INFO),
                replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", getFileResource("/etc/org.ops4j.pax.logging.cfg")),
                editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", NEXUS),
                editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", HTTP_PORT),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", RMI_REG_PORT),
                editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", SSH_PORT),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", RMI_SERVER_PORT)
        ));

        Files.list(getFileResource("/etc").toPath()).forEach(path -> {
            options.add(replaceConfigurationFile("etc/" + path.getFileName(), path.toFile()));
        });

        return options.toArray(new Option[options.size()]);
    }

    public File getFileResource(String path) {
        URL res = this.getClass().getResource(path);
        if (res == null) {
            throw new RuntimeException("File resource " + path + " not found");
        }
        return new File(res.getFile());
    }

    /**
     * Executes a shell command and returns output as a String.
     * Commands have a default timeout of 10 seconds.
     *
     * @param command    The command to execute
     * @param principals The principals (e.g. RolePrincipal objects) to run the command under
     * @return The String representing the output from the command.
     */
    protected String executeCommand(final String command, Principal... principals) {
        return executeCommand(command, COMMAND_TIMEOUT, false, principals);
    }

    /**
     * Executes a shell command and returns output as a String.
     * Commands have a default timeout of 10 seconds.
     *
     * @param command    The command to execute.
     * @param timeout    The amount of time in millis to wait for the command to execute.
     * @param silent     Specifies if the command should be displayed in the screen.
     * @param principals The principals (e.g. RolePrincipal objects) to run the command under
     * @return The String representing the output from the command.
     */
    protected String executeCommand(final String command, final Long timeout, final Boolean silent, final Principal... principals) {
        waitForCommandService(command);

        String response;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        final SessionFactory sessionFactory = getOsgiService(SessionFactory.class);
        final Session session = sessionFactory.create(System.in, printStream, System.err);

        final Callable<String> commandCallable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    if (!silent) {
                        System.err.println(command);
                    }
                    session.execute(command);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                printStream.flush();
                return byteArrayOutputStream.toString();
            }
        };

        FutureTask<String> commandFuture;
        if (principals.length == 0) {
            commandFuture = new FutureTask<>(commandCallable);
        } else {
            // If principals are defined, run the command callable via Subject.doAs()
            commandFuture = new FutureTask<>(() -> {
                Subject subject = new Subject();
                subject.getPrincipals().addAll(Arrays.asList(principals));
                return Subject.doAs(subject, (PrivilegedExceptionAction<String>) commandCallable::call);
            });
        }

        try {
            executor.submit(commandFuture);
            response = commandFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace(System.err);
            response = "SHELL COMMAND TIMED OUT: ";
        } catch (ExecutionException e) {
            Throwable cause = e.getCause().getCause();
            throw new RuntimeException(cause.getMessage(), cause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return response;
    }


    protected <T> T getOsgiService(Class<T> type, long timeout) {
        return getOsgiService(type, null, timeout);
    }

    protected <T> T getOsgiService(Class<T> type) {
        return getOsgiService(type, null, SERVICE_TIMEOUT);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
        ServiceTracker tracker = null;
        try {
            String flt;
            if (filter != null) {
                if (filter.startsWith("(")) {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")" + filter + ")";
                } else {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")(" + filter + "))";
                }
            } else {
                flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
            }
            Filter osgiFilter = FrameworkUtil.createFilter(flt);
            tracker = new ServiceTracker(bundleContext, osgiFilter, null);
            tracker.open(true);
            // Note that the tracker is not closed to keep the reference
            // This is buggy, as the service reference may change i think
            Object svc = type.cast(tracker.waitForService(timeout));
            if (svc == null) {
                Dictionary dic = bundleContext.getBundle().getHeaders();
                System.err.println("Test bundle headers: " + explode(dic));

                for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, null))) {
                    System.err.println("ServiceReference: " + ref);
                }

                for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, flt))) {
                    System.err.println("Filtered ServiceReference: " + ref);
                }

                throw new RuntimeException("Gave up waiting for service " + flt);
            }
            return type.cast(svc);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid filter", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForCommandService(String command) {
        // the commands are represented by services. Due to the asynchronous nature of services they may not be
        // immediately available. This code waits the services to be available, in their secured form. It
        // means that the code waits for the command service to appear with the roles defined.

        if (command == null || command.length() == 0) {
            return;
        }

        int spaceIdx = command.indexOf(' ');
        if (spaceIdx > 0) {
            command = command.substring(0, spaceIdx);
        }
        int colonIndx = command.indexOf(':');

        try {
            if (colonIndx > 0) {
                String scope = command.substring(0, colonIndx);
                String function = command.substring(colonIndx + 1);
                waitForService("(&(osgi.command.scope=" + scope + ")(osgi.command.function=" + function + "))", SERVICE_TIMEOUT);
            } else {
                waitForService("(osgi.command.function=" + command + ")", SERVICE_TIMEOUT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForService(String filter, long timeout) throws InvalidSyntaxException, InterruptedException {
        ServiceTracker st = new ServiceTracker(bundleContext, bundleContext.createFilter(filter), null);
        try {
            st.open();
            st.waitForService(timeout);
        } finally {
            st.close();
        }
    }

    /*
    * Explode the dictionary into a ,-delimited list of key=value pairs
    */
    @SuppressWarnings("rawtypes")
    private static String explode(Dictionary dictionary) {
        Enumeration keys = dictionary.keys();
        StringBuffer result = new StringBuffer();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            result.append(String.format("%s=%s", key, dictionary.get(key)));
            if (keys.hasMoreElements()) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    /**
     * Provides an iterable collection of references, even if the original array is null
     */
    @SuppressWarnings("rawtypes")
    private static Collection<ServiceReference> asCollection(ServiceReference[] references) {
        return references != null ? Arrays.asList(references) : Collections.<ServiceReference>emptyList();
    }

    protected void installAndAssertFeature(String feature) throws Exception {
        featureService.installFeature(feature);
        assertFeatureInstalled(feature);
    }

    protected void installAndAssertFeature(String feature, String version) throws Exception {
        featureService.installFeature(feature, version);
        assertFeatureInstalled(feature, version);
    }

    public void assertFeatureInstalled(String featureName) throws Exception {
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature feature : features) {
            if (featureName.equals(feature.getName())) {
                return;
            }
        }
        Assert.fail("Feature " + featureName + " should be installed but is not");
    }

    public void assertFeatureInstalled(String featureName, String featureVersion) throws Exception {
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature feature : features) {
            if (featureName.equals(feature.getName()) && featureVersion.equals(feature.getVersion())) {
                return;
            }
        }
        Assert.fail("Feature " + featureName + "/" + featureVersion + " should be installed but is not");
    }

    public Bundle installBundle(String url) throws Exception {
        return bundleContext.installBundle(url);
    }

    protected Bundle findBundleByName(String symbolicName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(symbolicName)) {
                return bundle;
            }
        }
        return null;
    }

    public org.osgi.service.cm.Configuration[] getFactoryConfigs(String factoryPid) throws IOException, InvalidSyntaxException {
        org.osgi.service.cm.Configuration[] configs = configAdmin.listConfigurations("(service.factorypid="+ factoryPid + ")");
        return configs;
    }

    protected void close(Closeable closeAble) {
        if (closeAble != null) {
            try {
                closeAble.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}