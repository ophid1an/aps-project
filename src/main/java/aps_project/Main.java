/**
 * Implementation of the A* search algorithm
 * where h(x) = 0 for all x (Dijkstra's algorithm)
 * and each action has cost equal to 1 to find plans
 * for PDDL problems.
 **/

package aps_project;

import fr.uga.pddl4j.parser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        // Supported PDDL requirements
        final Set<String> supportedReqs =
                new HashSet<>(Arrays.asList(":strips", ":typing"));
        // Global cost of action
        final int globalActionCost = 1;

        // Default paths for domain and problem
        File domainPath = new File("pddl/gripper/domain.pddl");
        File problemPath = new File("pddl/gripper/p02.pddl");

        // Output node information?
        Boolean printNodeInfo = false;

        // Parse command line options if they exist for
        // domain and problem paths
        if (args.length >= 2) {
            domainPath = new File(args[0]);
            problemPath = new File(args[1]);
        }

        if (args.length >= 3) {
            printNodeInfo = Boolean.parseBoolean(args[2]);
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

        if (!initialChecks(domain, problem, supportedReqs)) {
            System.exit(1);
        }

        // Extract possible actions based on operators and problem objects
        List<TypedSymbol> problemTypedObjects = problem.getObjects();

        // Add domain constants to problemTypedObjects
        problemTypedObjects.addAll(domain.getConstants());

        Set<Action> actions = new HashSet<>();

        int maxArity = domain.getOperators().stream()
                .map(Op::getArity)
                .reduce(Math::max)
                .get();

        for (int arity = 1; arity <= maxArity; arity += 1) {
            List<List<TypedSymbol>> problemTypedObjectsSequences = Util.getNPermutations(problemTypedObjects, arity);

            for (Op op : domain.getOperators()) {
                if (arity == op.getArity()) {
                    for (List<TypedSymbol> typedObjects : problemTypedObjectsSequences) {
                        Boolean typesMatch = true;

                        List<TypedSymbol> opParams = op.getParameters();
                        for (int i = 0, opParamSize = opParams.size(); i < opParamSize; i += 1) {
                            if (!opParams.get(i).getTypes().
                                    equals(typedObjects.get(i).getTypes())) {
                                typesMatch = false;
                                break;
                            }
                        }

                        if (typesMatch) {
                            Op tmpOp = new Op(op);

                            // Populate Map (operator parameters-> action parameters)
                            Map<TypedSymbol, Symbol> paramsMap = new HashMap<>();
                            for (int i = 0, paramsSize = tmpOp.getParameters().size(); i < paramsSize; i += 1) {
                                paramsMap.put(tmpOp.getParameters().get(i), new Symbol(typedObjects.get(i)));
                            }

                            // Action parameters
                            List<Symbol> params = tmpOp.getParameters().stream()
                                    .map(paramsMap::get)
                                    .map(Symbol::new)
                                    .collect(Collectors.toList());

                            // Action preconditions
                            Exp tmpOpPreconditions = tmpOp.getPreconditions();
                            List<Exp> tmpOpPreconditionsChildren = tmpOpPreconditions.getChildren();
                            Set<List<Symbol>> preconditions = new HashSet<>();
                            // If ATOM
                            if (tmpOpPreconditionsChildren.isEmpty()) {
                                List<Symbol> atom = tmpOpPreconditions.getAtom();
                                atom.replaceAll(s -> {
                                    if (paramsMap.containsKey(s)) {
                                        return paramsMap.get(s);
                                    }
                                    return s;
                                });
                                preconditions.add(atom);
                            }
                            // If not ATOM
                            else {
                                for (Exp exp : tmpOpPreconditionsChildren) {
                                    List<Symbol> atom = exp.getAtom();
                                    atom.replaceAll(s -> {
                                        if (paramsMap.containsKey(s)) {
                                            return paramsMap.get(s);
                                        }
                                        return s;
                                    });
                                    preconditions.add(atom);
                                }
                            }

                            // Action effects
                            Exp tmpOpEffects = tmpOp.getEffects();
                            Set<List<Symbol>> addList = new HashSet<>();
                            Set<List<Symbol>> deleteList = new HashSet<>();
                            List<Exp> tmpOpEffectsChildren = tmpOpEffects.getChildren();
                            for (Exp child : tmpOpEffectsChildren) {
                                List<Exp> childrenOfChild = child.getChildren();
                                // if child is ATOM (not negation)
                                if (childrenOfChild.isEmpty()) {
                                    List<Symbol> atom = child.getAtom();
                                    atom.replaceAll(s -> {
                                        if (paramsMap.containsKey(s)) {
                                            return paramsMap.get(s);
                                        }
                                        return s;
                                    });
                                    addList.add(atom);
                                }
                                // else child is negation
                                else {
                                    for (Exp exp : childrenOfChild) {
                                        List<Symbol> atom = exp.getAtom();
                                        atom.replaceAll(s -> {
                                            if (paramsMap.containsKey(s)) {
                                                return paramsMap.get(s);
                                            }
                                            return s;
                                        });
                                        deleteList.add(atom);
                                    }
                                }
                            }

                            actions.add(new Action(
                                    tmpOp.getName(),
                                    params,
                                    preconditions,
                                    addList,
                                    deleteList,
                                    globalActionCost
                            ));
                        }
                    }
                }
            }
        }

        // Apply algorithm on problem
        Set<List<Symbol>> initialState = new HashSet<>();
        for (Exp exp : problem.getInit()) {
            List<Exp> children = exp.getChildren();
            if (children.isEmpty()) {
                initialState.add(exp.getAtom());
            } else {
                for (Exp expChildren : children) {
                    initialState.add(expChildren.getAtom());
                }
            }
        }

        Set<List<Symbol>> goal = new HashSet<>();
        for (Exp exp : problem.getGoal().getChildren()) {
            goal.add(exp.getAtom());
        }

        Node finalNode =
                aStar(initialState, goal, actions, printNodeInfo);

        // Plan resolution output
        System.out.println("\nInitial State: " + initialState);
        System.out.println("Goal: " + goal);
        if (finalNode.getState().isEmpty()) {
            System.out.println("\n***** No plan found *****");
        } else {
            System.out.println("\nPlan found: " + finalNode.getPlan());
            System.out.println("Plan cost: " + finalNode.getFValue());
        }
    }

    private static boolean initialChecks(Domain domain, Problem problem,
                                         Set<String> suppReqs) {
        Set<String> domainReqs = domain.getRequirements().stream()
                .map(RequireKey::toString)
                .collect(Collectors.toSet());

        domainReqs.removeAll(suppReqs);

        if (!domainReqs.isEmpty()) {
            System.err.println("Supported PDDL requirements: " + suppReqs);
            return false;
        }

        if (!problem.getDomain().equals(domain.getName())) {
            System.err.println("Problem belongs to a different domain.");
            return false;
        }

        return true;
    }

    private static Node aStar(Set<List<Symbol>> initialState,
                              Set<List<Symbol>> goal, Set<Action> actions, Boolean printNodeInfo) {
        // Implement frontier using a priority queue
        Queue<Node> frontier = new PriorityQueue<>(10, Comparator.comparingInt(Node::getFValue));
        frontier.add(new Node(initialState));

        Set<Node> expanded = new HashSet<>();

        int cnt = 0;
        while (!frontier.isEmpty()) {
            // Node selection
            Node selectedNode = frontier.poll();

            expanded.add(selectedNode);

            if (printNodeInfo) {
                System.out.println("\n***** NODE " + cnt + " ******");
                System.out.println("\t Action used: " + selectedNode.getAction());
                System.out.println("\t State achieved: " + selectedNode.getState());
                cnt++;
            }

            if (selectedNode.getState().containsAll(goal)) {
                return selectedNode;
            }

            Queue<Node> children = new PriorityQueue<>(10, Comparator.comparingInt(Node::getFValue));

            for (Action action : actions) {
                Set<List<Symbol>> state = new HashSet<>(selectedNode.getState());
                if (state.containsAll(action.getPreconditions())) {
                    state.removeAll(action.getDeleteList());
                    state.addAll(action.getAddList());

                    children.add(new Node(state, action, selectedNode));
                }
            }

            // Clone sets
            Queue<Node> childrenTmp = new PriorityQueue<>(children);
            Set<Node> expandedTmp = new HashSet<>(expanded);
            Queue<Node> frontierTmp = new PriorityQueue<>(frontier);

            // Pruning
            for (Node nodeFromChildren : childrenTmp) {
                Boolean nodeFromChildrenRemoved = false;
                // Compare with nodes from frontier
                for (Node nodeFromFrontier : frontierTmp) {
                    if (nodeFromChildren.getState().equals(nodeFromFrontier.getState())) {
                        if (nodeFromChildren.getFValue() <= nodeFromFrontier.getFValue()) {
                            children.remove(nodeFromChildren);
                            nodeFromChildrenRemoved = true;
                        } else {
                            frontier.remove(nodeFromFrontier);
                        }
                    }
                }

                // Compare with nodes from expanded
                if (!nodeFromChildrenRemoved) {
                    for (Node nodeFromExpanded : expandedTmp) {
                        if (nodeFromChildren.getState().equals(nodeFromExpanded.getState())) {
                            if (nodeFromChildren.getFValue() <= nodeFromExpanded.getFValue()) {
                                children.remove(nodeFromChildren);
                                nodeFromChildrenRemoved = true;
                            } else {
                                expanded.remove(nodeFromExpanded);
                            }
                        }
                    }
                }

                // Add node to frontier if not removed from children
                if (!nodeFromChildrenRemoved) {
                    frontier.add(nodeFromChildren);
                }
            }
        }
        return new Node(new HashSet<>());
    }
}




