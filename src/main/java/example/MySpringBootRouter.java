package example;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

@SpringBootApplication
public class MySpringBootRouter extends FatJarRouter {

    @Autowired
    private HealthEndpoint health;

    @Autowired
    private CamelContext camelContext;

    @Override
    public void configure() {
        camelContext.addComponent("activemq", activeMQComponent("vm://localhost?broker.persistent=false"));

        from("activemq:queue:test_queue")
                .transform().simple("ref:myBean")
                .to("log:out");

        from("timer:status")
                .bean(health, "invoke")
                .log("Health is ${body}");

    }

    @Bean
    String myBean() {
        return "I'm Spring bean!";
    }

}