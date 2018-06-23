package aps_project;

import fr.uga.pddl4j.parser.Symbol;

import java.util.*;
import java.util.stream.Collectors;

public class Node {
    private Set<List<Symbol>> state;
    private Action action;
    private Node parent;
    private int cost;
    // Heuristic function, 0 for every node
    private int hValue = 0;

    // Constructor for root node
    public Node(Set<List<Symbol>> state) {
        this.state = state;
        this.action = null;
        this.parent = null;
        this.cost = 0;
    }

    // Constructor for non-root nodes
    public Node(Set<List<Symbol>> state,
                Action action,
                Node parent) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.cost = parent.getCost() + action.getCost();
    }

    public Set<List<Symbol>> getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }

    public Node getParent() {
        return parent;
    }

    private int getCost() {
        return cost;
    }

    public int getFValue() {
        return cost + hValue;
    }

    private List<Action> calcPlan(List<Action> planSoFar) {
        if (this.getParent() != null) {
            planSoFar.add(action);
            parent.calcPlan(planSoFar);
        }
        return planSoFar;
    }

    public List<String> getPlan() {
        List<Action> planSoFar = new ArrayList<>();

        if (this.getParent() == null) {
            return Arrays.asList(planSoFar.toString());
        } else {
            planSoFar.add(action);
            parent.calcPlan(planSoFar);

            List<String> plan =
                    planSoFar.stream()
                            .map(Action::toString)
                            .collect(Collectors.toList());

            // Plan is reversed, so we reverse it
            Collections.reverse(plan);

            return plan;
        }
    }
}