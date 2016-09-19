package example;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MySpringBootRouter.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MySpringBootRouterTest extends Assert {

    @Autowired
    CamelContext camelContext;

    @Test
    public void shouldProduceMessages() throws InterruptedException {
        // we expect that a number of messages is automatic done by the Camel
        // route as it uses a timer to trigger
        NotifyBuilder notify = new NotifyBuilder(camelContext).whenDone(4).create();

        assertTrue(notify.matches(10, TimeUnit.SECONDS));
    }

}