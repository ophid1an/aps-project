// Implementation of the A* search algorithm
// where h(x) = 0 for all x (Dijkstra's algorithm)
// and each action has cost equal to 1 to find plans
// for PDDL problems.

package aps_project;

import fr.uga.pddl4j.parser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
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
//        File domainPath = new File("pddl/blocksworld/domain.pddl");
//        File problemPath = new File("pddl/blocksworld/p01.pddl");
        File domainPath = new File("stuff/domain.pddl");
        File problemPath = new File("stuff/p00.pddl");

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

        if (!initialChecks(domain, problem, supportedReqs)) {
            System.exit(1);
        }

        List<TypedSymbol> problemObjects = problem.getObjects();
        Set<Action> actions = new HashSet<>();

        int maxArity = domain.getOperators().stream()
                .map(Op::getArity)
                .reduce(Math::max)
                .get();

        if (maxArity > 4) {
            System.err.println("Max operator arity supported is 4");
            System.exit(1);
        }

        for (int i = 1; i <= maxArity; i += 1) {
            List<List<TypedSymbol>> problemObjectsPermutations = new ArrayList<>();
            switch (i) {
                case 1:
                    problemObjectsPermutations = Arrays.asList(problemObjects);
                    break;
                case 2:
                    problemObjectsPermutations = getPermutations2(problemObjects);
                    break;
                case 3:
                    problemObjectsPermutations = getPermutations3(problemObjects);
                    break;
                case 4:
                    problemObjectsPermutations = getPermutations4(problemObjects);
                    break;
            }

            for (Op op : domain.getOperators()) {
                if (i == op.getArity()) {
                    for (List<TypedSymbol> objects : problemObjectsPermutations) {
                        Boolean typesMatch = true;

                        for (int j = 0, objectsSize = objects.size(); j < objectsSize; j += 1) {
                            if (!op.getParameters().get(j).getTypes().equals(objects.get(j).getTypes())) {
                                typesMatch = false;
                            }
                        }

                        if (typesMatch) {
                            for (TypedSymbol obj: objects) {
                                Op tmpOp = new Op(op);

                                // Action name
                                Symbol name = tmpOp.getName();

                                // Action parameters
                                List<TypedSymbol> parameters = tmpOp.getParameters();
                                for (TypedSymbol p : parameters) {
                                    p.setImage(obj.getImage());
                                }

                                // Action preconditions
                                Exp tmpOpPreconditions = tmpOp.getPreconditions();
                                Connective tmpOpPreconditionsConnective = tmpOpPreconditions.getConnective();
                                Set<List<Symbol>> preconditions = new HashSet<>();
                                List<Symbol> atom;
                                // If connective ATOM
                                if (tmpOpPreconditionsConnective.equals(Connective.ATOM)) {
                                    atom = tmpOpPreconditions.getAtom();
                                    for (int k = 0; k < atom.size(); k += 1) {
                                        if (k == 1) {
                                            atom.set(k, obj);
                                        }
                                        preconditions.add(atom);
                                    }


                                }
                                // If connective not ATOM
                                else {
                                    List<Exp> tmpOpPreconditionsChildren = tmpOpPreconditions.getChildren();
                                    for (Exp exp : tmpOpPreconditionsChildren) {
                                        atom = exp.getAtom();
                                        for (int k = 0; k < atom.size(); k += 1) {
                                            if (k == 1) {
                                                atom.set(k, obj);
                                            }
                                            preconditions.add(atom);
                                        }
                                    }
                                }

                                // Action effects
                                Exp tmpOpEffects = tmpOp.getEffects();
                                Connective tmpOpEffectsConnective = tmpOpEffects.getConnective();
                                Set<List<Symbol>> addList = new HashSet<>();
                                Set<List<Symbol>> deleteList = new HashSet<>();
                                // If connective AND
                                if (tmpOpEffectsConnective.equals(Connective.AND)) {
                                    List<Exp> tmpOpEffectsChildren = tmpOpEffects.getChildren();
                                    for (Exp child : tmpOpEffectsChildren) {
                                        Connective childConnective = child.getConnective();
                                        // if childConnective ATOM (Not Negation)
                                        if (childConnective.equals(Connective.ATOM)) {
                                            addList.add(child.getAtom());
                                        }
                                        // else childConnective should be NOT (Negation)
                                        else {
                                            deleteList.add(child.getChildren().get(0).getAtom());
                                        }

                                    }

                                    for (List<Symbol> sList : addList) {
                                        for (int k = 0; k < sList.size(); k += 1) {
                                            if (k == 1) {
                                                sList.set(k, obj);
                                            }
                                        }
                                    }

                                    for (List<Symbol> sList : deleteList) {
                                        for (int k = 0; k < sList.size(); k += 1) {
                                            if (k == 1) {
                                                sList.set(k, obj);
                                            }
                                        }
                                    }
                                }


                                for (TypedSymbol p : parameters) {

                                    actions.add(new Action(
                                            name,
                                            parameters,
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
            }
        }

//        for (Op op : domain.getOperators()) {
//            int arity = op.getArity();
//            if (arity == 1) {
//                for (TypedSymbol obj : problemObjects) {
//                    if (op.getParameters().get(0).getTypes().equals(obj.getTypes())) {
//
//                        cnt1++;
//
//                        Op tmpOp = new Op(op);
//
//                        // Action name
//                        Symbol name = tmpOp.getName();
//
//                        // Action parameters
//                        List<TypedSymbol> parameters = tmpOp.getParameters();
//                        for (TypedSymbol p : parameters) {
//                            p.setImage(obj.getImage());
//                        }
//
//                        // Action preconditions
//                        Exp tmpOpPreconditions = tmpOp.getPreconditions();
//                        Connective tmpOpPreconditionsConnective = tmpOpPreconditions.getConnective();
//                        Set<List<Symbol>> preconditions = new HashSet<>();
//                        List<Symbol> atom;
//                        // If connective ATOM
//                        if (tmpOpPreconditionsConnective.equals(Connective.ATOM)) {
//                            atom = tmpOpPreconditions.getAtom();
//                            for (int i = 0; i < atom.size(); i += 1) {
//                                if (i == 1) {
//                                    atom.set(i, obj);
//                                }
//                                preconditions.add(atom);
//                            }
//
//
//                        }
//                        // If connective not ATOM
//                        else {
//                            List<Exp> tmpOpPreconditionsChildren = tmpOpPreconditions.getChildren();
//                            for (Exp exp : tmpOpPreconditionsChildren) {
//                                atom = exp.getAtom();
//                                for (int i = 0; i < atom.size(); i += 1) {
//                                    if (i == 1) {
//                                        atom.set(i, obj);
//                                    }
//                                    preconditions.add(atom);
//                                }
//                            }
//                        }
//
//                        // Action effects
//                        Exp tmpOpEffects = tmpOp.getEffects();
//                        Connective tmpOpEffectsConnective = tmpOpEffects.getConnective();
//                        Set<List<Symbol>> addList = new HashSet<>();
//                        Set<List<Symbol>> deleteList = new HashSet<>();
//                        // If connective AND
//                        if (tmpOpEffectsConnective.equals(Connective.AND)) {
//                            List<Exp> tmpOpEffectsChildren = tmpOpEffects.getChildren();
//                            for (Exp child : tmpOpEffectsChildren) {
//                                Connective childConnective = child.getConnective();
//                                // if childConnective ATOM (Not Negation)
//                                if (childConnective.equals(Connective.ATOM)) {
//                                    addList.add(child.getAtom());
//                                }
//                                // else childConnective should be NOT (Negation)
//                                else {
//                                    deleteList.add(child.getChildren().get(0).getAtom());
//                                }
//
//                            }
//
//                            for (List<Symbol> sList : addList) {
//                                for (int i = 0; i < sList.size(); i += 1) {
//                                    if (i == 1) {
//                                        sList.set(i, obj);
//                                    }
//                                }
//                            }
//
//                            for (List<Symbol> sList : deleteList) {
//                                for (int i = 0; i < sList.size(); i += 1) {
//                                    if (i == 1) {
//                                        sList.set(i, obj);
//                                    }
//                                }
//                            }
//                        }
//
//
//                        for (TypedSymbol p : parameters) {
//
//                            actions.add(new Action(
//                                    name,
//                                    parameters,
//                                    preconditions,
//                                    addList,
//                                    deleteList,
//                                    globalActionCost
//                            ));
//                        }
//
//
//                    }
//                }
//            }
//        }
//        System.out.println(cnt1);
//        for (Action act : actions) {
//            System.out.println("\n**** ACTION ****");
//            System.out.println("--- Name: " + act);
//            System.out.println("--- Preconditions: ");
//            if (act.getPreconditions().getChildren().size() > 0) {
//                for (Exp prec : act.getPreconditions().getChildren()) {
//                    System.out.println("\t" + prec);
//                }
//            } else {
//                System.out.println("\t" + act.getPreconditions().getAtom());
//            }
//            System.out.println("--- Add List: ");
//            for (Exp al : act.getAddList()) {
//                System.out.println("\t" + al.toString());
//            }
//            System.out.println("--- Delete List: ");
//            for (Exp dl : act.getDeleteList()) {
//                System.out.println("\t" + dl.toString());
//            }
//
//        }
//        System.exit(0);
        Set<List<Symbol>> initialState = new HashSet<>();
        for (Exp exp : problem.getInit()) {
            initialState.add(exp.getAtom());
        }

        Set<List<Symbol>> goal = new HashSet<>();
        for (Exp exp : problem.getGoal().getChildren()) {
            goal.add(exp.getAtom());
        }

        List<String> plan =
                aStar(initialState, goal, actions);

        System.out.println("Plan found: " + plan);
        System.out.println("Plan cost: " + plan.size());
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

    // Implement Heap's algorithm to generate
    // all possible permutations of objects
    // Adapted from https://en.wikipedia.org/wiki/Heap%27s_algorithm
    private static List<List<TypedSymbol>> heapsAlgorithm(int n, List<TypedSymbol> objects) {
        int[] c = new int[n];
        Arrays.fill(c, 0);

        List<List<TypedSymbol>> permutations = new ArrayList<>(Arrays.asList(objects));
        int i = 0;
        while (i < n) {
            if (c[i] < i) {
                if (i % 2 == 0) {
                    Collections.swap(objects, 0, i);
                } else {
                    Collections.swap(objects, c[i], i);
                }
                permutations.add(objects);
                c[i] += 1;
                i = 0;
            } else {
                c[i] = 0;
                i += 1;
            }
        }

        return permutations;
    }

    private static List<List<TypedSymbol>> getPermutations2(List<TypedSymbol> list) {
        List<List<TypedSymbol>> result = new ArrayList<>();
        for (TypedSymbol s1 : list) {
            for (TypedSymbol s2 : list) {
                result.add(Arrays.asList(s1, s2));
            }
        }
        return result;
    }

    private static List<List<TypedSymbol>> getPermutations3(List<TypedSymbol> list) {
        List<List<TypedSymbol>> result = new ArrayList<>();
        for (TypedSymbol s1 : list) {
            for (TypedSymbol s2 : list) {
                for (TypedSymbol s3 : list) {
                    result.add(Arrays.asList(s1, s2, s3));
                }
            }
        }
        return result;
    }

    private static List<List<TypedSymbol>> getPermutations4(List<TypedSymbol> list) {
        List<List<TypedSymbol>> result = new ArrayList<>();
        for (TypedSymbol s1 : list) {
            for (TypedSymbol s2 : list) {
                for (TypedSymbol s3 : list) {
                    for (TypedSymbol s4 : list) {
                        result.add(Arrays.asList(s1, s2, s3, s4));
                    }
                }
            }
        }
        return result;
    }

    private static List<String> aStar(Set<List<Symbol>> initialState,
                                      Set<List<Symbol>> goal, Set<Action> actions) {
        // Implement frontier using a priority queue
        Queue<Node> frontier = new PriorityQueue<>(10, Comparator.comparingInt(Node::getFValue));
        frontier.add(new Node(initialState));

        Set<Node> expanded = new HashSet<>();

        System.out.println(actions.size());
        int cnt = 0;
        while (!frontier.isEmpty()) {
            // Node selection
            Node selectedNode = frontier.poll();

            expanded.add(selectedNode);

            cnt++;
            System.out.println("\n***** NODE " + cnt + " ******");
            System.out.println(selectedNode.getAction());
            System.out.println(selectedNode.getState());

            if (selectedNode.getState().containsAll(goal)) {

                return selectedNode.getPlan();
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
                    System.out.println("Adding Child to Frontier: " + nodeFromChildren.getAction());
                    frontier.add(nodeFromChildren);
                }
            }
        }
        return new ArrayList<>(Arrays.asList("*** FAILED TO FIND PLAN ***"));
    }
}




