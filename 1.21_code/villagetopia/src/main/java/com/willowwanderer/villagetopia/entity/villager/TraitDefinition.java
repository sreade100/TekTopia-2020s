package com.willowwanderer.villagetopia.entity.villager;

public class TraitDefinition {

    private final String name;
    private final double weight;

    public TraitDefinition(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }
}