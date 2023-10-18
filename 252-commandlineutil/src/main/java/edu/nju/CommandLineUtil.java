package edu.nju;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineUtil {
    private static CommandLine commandLine;
    private static final CommandLineParser parser = new DefaultParser();
    private static final Options options = new Options();
    private boolean sideEffect = false;
    public static final String WRONG_MESSAGE = "Invalid input.";

    /**
     * you can define options here
     * or you can create a func such as [static void defineOptions()] and call it before parse input
     */
    static {
        options.addOption("h", "help", false, "打印出所有预定义的选项与用法");
        options.addOption("p", "print", true, "打印出arg");
        options.addOption("s", "将CommandlineUtil中sideEffect变量置为true");
    }

    /**
     * step1 add some option rules（you can do it in static{}）
     * step2 parse the input
     * step3 handle options
     *
     * @param args input of program
     */
    public void main(String[] args) {
        parseInput(args);
        handleOptions();
    }

    /**
     * Print the usage of all options
     * Actually, you can print anything to pass the test
     * but you are recommended to use HelpFormatter to see what will happen
     */
    private static void printHelpMessage() {
//        System.out.println("help");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("cli", options);
    }

    /**
     * Parse the input and handle exception
     *
     * @param args origin args form input
     */
    public void parseInput(String[] args) {
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("command line parse failed: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * You can handle options here or create your own func
     */
    public void handleOptions() {
        if (commandLine.hasOption("h")) {
            printHelpMessage();
            return;
        }

        if (commandLine.getArgList().isEmpty()) {
            System.out.println(WRONG_MESSAGE);
            return;
        }

        if (commandLine.hasOption("p")) {
            String content = commandLine.getOptionValue("p");
            if (content == null) {
                System.out.println("Missing argument for option: p");
            } else {
                System.out.println(content);
            }
        } else if (commandLine.hasOption("s")) {
            sideEffect = true;
        }
    }

    public boolean getSideEffectFlag() {
        return sideEffect;
    }

}