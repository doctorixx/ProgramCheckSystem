package ru.doctorixx.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import ru.doctorixx.api.structures.APIRequest;
import ru.doctorixx.api.structures.ApiResponse;
import ru.doctorixx.core.ExecutionManager;
import ru.doctorixx.core.RunManager;
import ru.doctorixx.core.executors.CommandExecutor;
import ru.doctorixx.core.executors.JavaExecutor;
import ru.doctorixx.core.executors.KumirExecutor;
import ru.doctorixx.core.executors.PythonExecutor;
import ru.doctorixx.core.structures.ProgramResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIHandler implements Handler {


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final Map<String, CommandExecutor> executors = new HashMap<>();

    static {
        executors.put("python", new PythonExecutor("hello.py", "adir"));
        executors.put("java", new JavaExecutor("Main.java", "adir"));
        executors.put("kumir", new KumirExecutor("kumir.kum", "adir"));
    }

    @Override
    public void handle(@NotNull Context context) {

        APIRequest request = context.bodyAsClass(APIRequest.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ExecutionManager executionManager = new ExecutionManager(executors.get(request.compiler()), "", request.source());
                    RunManager runManager = new RunManager(executionManager);

                    List<ProgramResult> results = runManager.test(request.tests());
                    System.out.println();

                    for (ProgramResult res : results) {
                        System.out.println(res.msg() + " " + res.time() + "ms");
                    }


                    System.out.println(runManager.getSuccessTestCount() + "/" + runManager.getTestCount());

                    OkHttpClient client = new OkHttpClient();

                    ObjectMapper objectMapper = new ObjectMapper();

                    ApiResponse response = new ApiResponse(results, request.meta());

                    RequestBody requestBody = RequestBody.create(JSON, objectMapper.writeValueAsString(response));
                    Request senderRequest = new Request.Builder()
                            .url("http://127.0.0.1:5000/api/check_system_callback")
                            .post(requestBody)
                            .build();

                    client.newCall(senderRequest).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        context.result("OK");
    }
}
