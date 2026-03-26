package app.routes;

import app.controllers.SpinTheWheelController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SpinTheWheelRoutes {

    private final SpinTheWheelController spinTheWheelController;

    public SpinTheWheelRoutes(SpinTheWheelController spinTheWheelController) {
        this.spinTheWheelController = spinTheWheelController;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", spinTheWheelController::home);
            post("/result", spinTheWheelController::getResult);
            // TODO: Add more routes when needed here
        };
    }
}
