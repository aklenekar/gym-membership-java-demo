package com.apexgym.entity;

import lombok.Getter;

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

}
