package app.routes;

import app.controllers.WaardleController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.get;

public class WordleRoutes {
    private final WaardleController waardleController;

    public WordleRoutes(WaardleController waardleController) {
        this.waardleController = waardleController;
    }

    protected EndpointGroup getRoutes() {

        return () -> {
            get("/", waardleController::getAll);
            // TODO: Add more routes when needed here
        };
    }

}
