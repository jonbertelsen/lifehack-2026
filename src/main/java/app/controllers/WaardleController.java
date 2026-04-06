package app.controllers;

import app.persistence.WaardleMapper;
import io.javalin.http.Context;

public class WaardleController {
    private final WaardleMapper waardleMapper;

    public WaardleController(WaardleMapper wordleMapper) {
        this.waardleMapper = wordleMapper;
    }

    public void getAll(Context ctx){
        ctx.render("waardle/.html");
    }
    public void login(Context ctx){
        ctx.render("waardle/startside.html");
    }
    public void signup(Context ctx){
        ctx.render("waardle/signup.html");
    }

}
