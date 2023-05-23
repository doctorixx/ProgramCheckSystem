package ru.doctorixx.core;

import ru.doctorixx.core.executors.CommandExecutor;
import ru.doctorixx.core.structures.Callback;
import ru.doctorixx.core.structures.ProgramResult;
import ru.doctorixx.core.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;

public class ExecutionManager {

    private static int lastProgramId = 0;
    private final CommandExecutor executor;
    private final File executeHomeDirectory;
    private String inputData;
    private final String fileData;
    private Callback<ProgramResult> callback;

    private boolean hasNewId;
    private int myId;

    public ExecutionManager(CommandExecutor executor, String inputData, String fileData) {
        this.executor = executor;
        executeHomeDirectory = new File(executor.getDirectory());
        this.inputData = inputData;
        this.fileData = fileData;
    }

    public ExecutionManager(CommandExecutor executor, String inputData, String fileData, Callback<ProgramResult> callback) {
        this(executor, inputData, fileData);

        this.callback = callback;
    }

    synchronized static private int generateNewProgramId() {
        lastProgramId++;
        return lastProgramId;
    }

    public ProgramResult executeOne() {
        return execute(generateNewProgramId());
    }


    public ProgramResult executeMany() {
        if (hasNewId) {
            return execute(myId);
        } else {
            myId = generateNewProgramId();
            hasNewId = true;
            return execute(myId);
        }

    }

    private ProgramResult execute(int runId) {
//        int runId = generateNewProgramId();
        ProgramResult out = null;
        File tempdir = new File(executeHomeDirectory + "\\" + runId);
        try {

            tempdir.mkdir();

            try (FileWriter writer = new FileWriter(new File(tempdir, executor.getFilename()))) {
                writer.write(fileData);
                writer.flush();
            }


            executor.setDirectory(tempdir.getAbsolutePath());
            ProgramResult programResult = executor.execute(inputData);

            if (callback != null) {
                callback.call(programResult);
            }

            out = programResult;


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            FileUtils.recursiveDelete(tempdir);
        }

        return out;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
}
