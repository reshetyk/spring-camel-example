package example;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MySpringBootRouter extends FatJarRouter {

    @Autowired
    private HealthEndpoint health;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private SomeService someService;

//    @Bean
//    public RestConfiguration restConfiguration() {
//      return new RestConfiguration();
//    }
    @Bean
    public RestletComponent restlet() {
        return new RestletComponent();
    }

    @Bean
    public RouteBuilder restRoute () {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                restConfiguration().component("restlet").port(8091);
                rest("/say")
                        .get("/hello").produces("text/html").to("direct:hello")
                        .get("/bye").consumes("application/json").to("direct:bye")
                        .post("/bye").to("mock:update");

                from("direct:hello")
                        .log(LoggingLevel.INFO, "ok!!!")
                        .transform().constant("<!DOCTYPE html>\n<html><body><h1>Hello World</h1></body></html>");
                from("direct:bye")
                        .transform().constant("Bye World");
            }
        };
    }


//    @Bean
    public RouteBuilder route1 () {

        RouteBuilder routeBuilder = new RouteBuilder() {
            JacksonDataFormat json = new JacksonDataFormat(SomeDomain.class);

            @Override
            public void configure() throws Exception {
                from("timer:status?period=10000")
                        .unmarshal(json)
                        .bean(someService, "test()")
                        .to("log:out");
            }
        };

        routeBuilder
                .onException(Exception.class)
                .log(LoggingLevel.ERROR, "--------\n" + "${header.EXCEPTION_CAUGHT}" + " \n--------the bug --------");

        return routeBuilder;
    }

    @Bean
    public SomeService someService() {
        return new SomeService();
    }

}