package app.controllers;

import app.persistence.SpinTheWheelMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinTheWheelController {

    private final SpinTheWheelMapper spinTheWheelMapper;

    public SpinTheWheelController(SpinTheWheelMapper spinTheWheelMapper) {
        this.spinTheWheelMapper = spinTheWheelMapper;
    }

    public void home(Context ctx) {
        ctx.render("stw/index.html");
    }

    public void getResult(Context ctx) {
        String option1 = ctx.formParam("option1");
        String option2 = ctx.formParam("option2");
        String option3 = ctx.formParam("option3");
        String option4 = ctx.formParam("option4");
        String option5 = ctx.formParam("option5");

        List<String> options = new ArrayList<>();

        if (option1 != null && !option1.isBlank()) {
            options.add(option1);
        }

        if (option2 != null && !option2.isBlank()) {
            options.add(option2);
        }

        if (option3 != null && !option3.isBlank()) {
            options.add(option3);
        }

        if (option4 != null && !option4.isBlank()) {
            options.add(option4);
        }

        if (option5 != null && !option5.isBlank()) {
            options.add(option5);
        }

        if (options.isEmpty()) {
            ctx.attribute("error", "Du skal skrive mindst én mulighed");
            ctx.render("stw/index.html");
            return;
        }

        Random random = new Random();
        int choice = random.nextInt(options.size());

        ctx.attribute("result", options.get(choice));

        // send også mulighederne videre til "RUL IGEN"
        ctx.attribute("option1", option1);
        ctx.attribute("option2", option2);
        ctx.attribute("option3", option3);
        ctx.attribute("option4", option4);
        ctx.attribute("option5", option5);

        ctx.render("stw/result.html");
    }
}