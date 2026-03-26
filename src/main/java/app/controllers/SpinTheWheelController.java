package app.controllers;

import app.persistence.SpinTheWheelMapper;
import io.javalin.http.Context;

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
        // TODO: pick a random option
        ctx.attribute("result", "Denne option er valg: option 1");
        ctx.render("stw/result.html");
    }


}
