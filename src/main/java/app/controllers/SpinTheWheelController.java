package app.controllers;

import app.persistence.SpinTheWheelMapper;
import io.javalin.http.Context;

public class SpinTheWheelController {
    SpinTheWheelMapper spinTheWheelMapper;

    public SpinTheWheelController(SpinTheWheelMapper spinTheWheelMapper) {
        this.spinTheWheelMapper = spinTheWheelMapper;
    }

    public void getAll(Context ctx){
        ctx.render("stw/index.html");
    }


}
