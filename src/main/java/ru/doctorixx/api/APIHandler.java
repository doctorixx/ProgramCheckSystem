package ru.doctorixx.api;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import ru.doctorixx.api.structures.APIRequest;
import ru.doctorixx.core.ExecutionManager;
import ru.doctorixx.core.RunManager;
import ru.doctorixx.core.executors.PythonExecutor;
import ru.doctorixx.core.structures.ProgramResult;
import ru.doctorixx.core.structures.Test;

import java.util.ArrayList;
import java.util.List;

public class APIHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        APIRequest request = context.bodyAsClass(APIRequest.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ExecutionManager executionManager = new ExecutionManager(new PythonExecutor("hello.py", "adir"), "", request.source());
                RunManager runManager = new RunManager(executionManager);

                List<Test> tests = new ArrayList<>();

                tests.add(new Test("1\n1", "2"));
                tests.add(new Test("10\n1", "11"));
                tests.add(new Test("1\n1", "2"));
                tests.add(new Test("1\n3", "4"));
                tests.add(new Test("-1\n3", "2"));
                tests.add(new Test("-2\n3", "1"));
                tests.add(new Test("-3\n3", "0"));


                List<ProgramResult> results = runManager.test(tests);
                System.out.println();

                for (ProgramResult res : results) {
                    System.out.println(res.msg() + " " + res.time() + "ms");
                }

                System.out.println(runManager.getSuccessTestCount() + "/" + runManager.getTestCount());
            }
        }).start();

        context.result("OK");
    }
}
