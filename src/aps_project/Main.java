package aps_project;

import fr.uga.pddl4j.parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        // Get the domain and the problem paths from the command line
//        File domain = new File(args[0]); //String domain = args[0]
//        File problem = new File(args[1]); //String problem = args[1]
        File domain = new File("pddl/blocksworld/domain.pddl"); //String domain = args[0]
        File problem = new File("pddl/blocksworld/p01.pddl"); //String problem = args[1]

        if (args.length == 0) {
            Parser parser = new Parser();
            try {
                parser.parse(domain, problem);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
            if (!parser.getErrorManager().isEmpty()) {
                parser.getErrorManager().printAll();
            }

            System.out.println("***** DOMAIN ******\n");
            System.out.println(parser.getDomain());
            System.out.println("\n***** PROBLEM ******\n");
            System.out.println(parser.getProblem());
        }
    }
}