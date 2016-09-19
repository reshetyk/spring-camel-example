package example;

import org.apache.camel.spring.boot.FatJarRouter;
import org.apache.camel.spring.boot.FatWarInitializer;

/**
 * Created by Reshetuyk on 18.09.2016.
 */
public class MySpringBootRouterWarInitializer extends FatWarInitializer {

    @Override
    protected Class<? extends FatJarRouter> routerClass() {
        return MySpringBootRouter.class;
    }
}
