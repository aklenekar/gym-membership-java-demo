package com.apexgym.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GymClassCategory {
    HIIT("HIIT"),
    Yoga("Upper Body"),
    Strength("Upper Body"),
    Cardio("Cardio"),
    Boxing("Cardio"),
    Pilates("Cardio");

    private String type;

    GymClassCategory(String type) {
        this.type = type;
    }

    public static GymClassCategory fromType(String type) {
        return Arrays.stream(GymClassCategory.values())
                .filter(category -> category.getType().equalsIgnoreCase(type))
                .findFirst().orElse(GymClassCategory.Strength);
    }

}
