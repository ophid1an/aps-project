package aps_project;

import fr.uga.pddl4j.parser.Domain;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.parser.Problem;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        // Default paths for domain and problem
        File domainPath = new File("pddl/blocksworld/domain.pddl");
        File problemPath = new File("pddl/blocksworld/p01.pddl");

        // Parse command line options if they exist for
        // domain and problem paths
        if (args.length >= 2) {
            domainPath = new File(args[0]);
            problemPath = new File(args[1]);
        }

        Parser parser = new Parser();
        try {
            parser.parse(domainPath, problemPath);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if (!parser.getErrorManager().isEmpty()) {
            parser.getErrorManager().printAll();
        }

        Domain domain = parser.getDomain();
        Problem problem = parser.getProblem();

        System.out.println("\nDomain path: " + domainPath);
        System.out.println("Problem path: " + problemPath);

        System.out.println("\nDomain: " + domain.getName());
        System.out.println("Problem: " + problem.getName());

        System.out.println("\n****** DOMAIN ******\n");
        System.out.println("Operators: " + domain.getOperators());
        System.out.println("Predicates: " + domain.getPredicates());

        System.out.println("\n***** PROBLEM *****\n");
        System.out.println("Domain: " + problem.getDomain());
        System.out.println("Init: " + problem.getInit());
        System.out.println("Objects: " + problem.getObjects());
        System.out.println("Requirements: " + problem.getRequirements());
        System.out.println("Goal: " + problem.getGoal());
    }
}