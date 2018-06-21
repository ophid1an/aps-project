package aps_project;

import fr.uga.pddl4j.parser.Symbol;
import fr.uga.pddl4j.parser.TypedSymbol;

import java.util.List;
import java.util.Set;

public class Action {
    private Symbol name;
    private List<TypedSymbol> parameters;
    private Set<List<Symbol>> preconditions;
    private Set<List<Symbol>> addList;
    private Set<List<Symbol>> deleteList;
    private int cost;

    // Constructor
    public Action(Symbol name,
                  List<TypedSymbol> parameters,
                  Set<List<Symbol>> preconditions,
                  Set<List<Symbol>> addList,
                  Set<List<Symbol>> deleteList,
                  int cost) {
        this.name = name;
        this.parameters = parameters;
        this.preconditions = preconditions;
        this.addList = addList;
        this.deleteList = deleteList;
        this.cost = cost;
    }

    public Set<List<Symbol>> getPreconditions() {
        return preconditions;
    }

    public Set<List<Symbol>> getAddList() {
        return addList;
    }

    public Set<List<Symbol>> getDeleteList() {
        return deleteList;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(name.toString() + " ");

        for (TypedSymbol ts : parameters) {
            builder.append(ts.getImage() + " ");
        }

        return builder.toString();
    }
}