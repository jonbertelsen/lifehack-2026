package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final WordleRoutes wordleRoutes;
    private final SpinTheWheelRoutes spinTheWheelRoutes;

    public Routes(WordleRoutes wordleRoutes, SpinTheWheelRoutes spinTheWheelRoutes) {
        this.wordleRoutes = wordleRoutes;
        this.spinTheWheelRoutes = spinTheWheelRoutes;
    }

    public EndpointGroup getRoutes(){
        return () -> {
            get("/", ctx -> ctx.render("signup.html"));
            path("/waardle", wordleRoutes.getRoutes() );
            path("/stw", spinTheWheelRoutes.getRoutes());
        };
    }
}
