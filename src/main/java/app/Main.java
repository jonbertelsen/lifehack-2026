package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.BmiController;
import app.controllers.SpinTheWheelController;
import app.controllers.WaardleController;
import app.persistence.BmiMapper;
import app.persistence.ConnectionPool;
import app.persistence.SpinTheWheelMapper;
import app.persistence.WaardleMapper;
import app.routes.Routes;
import app.routes.SpinTheWheelRoutes;
import app.routes.WordleRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "lifehack";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {

        BmiMapper bmiMapper = new BmiMapper(connectionPool);
        BmiController bmiController = new BmiController(bmiMapper);

        WaardleMapper wordleMapper = new WaardleMapper(connectionPool);
        WaardleController waardleController = new WaardleController(wordleMapper);
        SpinTheWheelMapper spinTheWheelMapper = new SpinTheWheelMapper(connectionPool);
        SpinTheWheelController spinTheWheelController = new SpinTheWheelController(spinTheWheelMapper);
        WordleRoutes wordleRoutes = new WordleRoutes(waardleController);
        SpinTheWheelRoutes spinTheWheelRoutes = new SpinTheWheelRoutes(spinTheWheelController);

        Routes routes = new Routes(wordleRoutes, spinTheWheelRoutes);

        Javalin app = Javalin.create(config -> {
                    config.staticFiles.add("/public");
                    config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
                    config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
                    config.router.apiBuilder(routes.getRoutes());
                })
                .start(7070);
    }

}