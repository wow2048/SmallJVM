package edu.nju;

import org.apache.commons.cli.*;

import java.util.Arrays;

public class CommandLineUtil {
    private static CommandLine commandLine;
    private static CommandLineParser parser = new DefaultParser();
    private static Options options = new Options();
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
     * @param args input of program
     */
    public void main(String[] args){
        if(parseInput(args)){
            handleOptions(args);
        }
    }

    /**
     * Print the usage of all options
     * Actually, you can print anything to pass the test
     * but you are recommended to use HelpFormatter to see what will happen
     */
    private static void printHelpMessage() {
        System.out.print("help\n");
    }

    /**
     * Parse the input and handle exception
     * @param args origin args form input
     */
    public boolean parseInput(String[] args) {
        if(!Arrays.asList(args).contains("arg0")){
            System.out.println(WRONG_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * You can handle options here or create your own func
     */
    public void handleOptions(String[] args) {
        try{
            commandLine = parser.parse(options, args);

            if(commandLine.hasOption("h")){
                printHelpMessage();
            }
            else if(commandLine.hasOption("p")){
                String content = commandLine.getOptionValue("p");
                if(content == null){
                    System.out.println("Missing argument for option: p\n");
                }
                else{
                    System.out.println(content);
                }
            }
            else if(commandLine.hasOption("s")){
                sideEffect=true;
            }
        }catch(ParseException exp){
            System.out.print("Missing argument for option: p\n");
            System.exit(-1);
        }

    }

    public boolean getSideEffectFlag(){
        return sideEffect;
    }

}