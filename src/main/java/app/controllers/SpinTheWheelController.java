package app.controllers;

import app.persistence.SpinTheWheelMapper;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinTheWheelController {
    SpinTheWheelMapper spinTheWheelMapper;

    public SpinTheWheelController(SpinTheWheelMapper spinTheWheelMapper) {
        this.spinTheWheelMapper = spinTheWheelMapper;
    }

    public void home(Context ctx){
        ctx.render("stw/index.html");
    }

    public void getResult(Context ctx){
        // TODO: get options
        String option1 = ctx.formParam("option1");
        String option2 = ctx.formParam("option2");

        List<String> options = new ArrayList<>();

        if (!option1.isEmpty()){
            options.add(option1);
        }

        if (!option2.isEmpty()){
            options.add(option2);
        }
        // TODO: pick a random option
        Random random = new Random();
        int choice = random.nextInt(0, options.size());

        ctx.attribute("result", options.get(choice));
        ctx.render("stw/result.html");
    }


}
