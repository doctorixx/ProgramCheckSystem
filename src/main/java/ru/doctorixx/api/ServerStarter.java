package ru.doctorixx.api;

import io.javalin.Javalin;

public class ServerStarter {
    public static void main(String[] args) {
        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .post("/api/v1/test", new APIHandler())
                .start(7070);
    }
}
