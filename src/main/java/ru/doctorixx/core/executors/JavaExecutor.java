package ru.doctorixx.core.executors;

import ru.doctorixx.core.compilers.AbstractCompiler;

public class JavaExecutor extends CommandExecutor {


    public JavaExecutor(String filename, String directory) {
        super(filename, directory);
    }

    @Override
    protected String getRunCommand() {
        return "java";
    }

    @Override
    protected AbstractCompiler getCompiler() {
        return new JavaCompiler();
    }

    private static class JavaCompiler extends AbstractCompiler {

        @Override
        public String getCompileCommand() {
            return "javac";
        }

        @Override
        public String filenameModifyAfterComplete(String in) {
            return in.replace(".java", "");
        }
    }

}
