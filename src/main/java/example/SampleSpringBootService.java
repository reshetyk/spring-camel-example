package example;

import io.hawt.config.ConfigFacade;
import io.hawt.springboot.EnableHawtio;
import io.hawt.springboot.HawtPlugin;
import io.hawt.springboot.PluginService;
import io.hawt.system.ConfigManager;
import io.hawt.web.AuthenticationFilter;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;
import java.net.URI;

@SpringBootApplication
@EnableHawtio
public class SampleSpringBootService {

    @Autowired
    private ServletContext servletContext;

    public static void main(String[] args) {
        System.setProperty(AuthenticationFilter.HAWTIO_AUTHENTICATION_ENABLED, "false");
        SpringApplication.run(SampleSpringBootService.class, args);
    }

    @PostConstruct
    public void init() {
        final ConfigManager configManager = new ConfigManager();
        configManager.init();
        servletContext.setAttribute("ConfigManager", configManager);
    }

    /**
     * Loading an example plugin
     * @return
     */
    @Bean
    public HawtPlugin samplePlugin() {
        return new HawtPlugin("sample-plugin", "/hawtio/plugins", "", new String[] { "sample-plugin/js/sample-plugin.js" });
    }

    /**
     * Set things up to be in offline mode
     * @return
     * @throws Exception
     */
    @Bean
    public ConfigFacade configFacade() throws Exception {
        ConfigFacade config = new ConfigFacade() {
            public boolean isOffline() {
                return true;
            }
        };
        config.init();
        return config;
    }

    /**
     * Register rest endpoint to handle requests for /plugin, and return all registered plugins.
     * @return
     */
    @Bean
    public PluginService pluginService(){
        return new PluginService();
    }

    @Bean
    public BrokerService brokerService () throws Exception {

        KahaDBPersistenceAdapter kahadb = new KahaDBPersistenceAdapter();
        kahadb.setDirectory(new File("d:/temp/activemq-data"));

        BrokerService brokerService = new BrokerService();
        TransportConnector connector = new TransportConnector();
        connector.setName("vm");
        connector.setUri(new URI("vm://localhost:100001"));
        brokerService.addConnector(connector);
        brokerService.setPersistenceAdapter(kahadb);

        return brokerService;
    }
}