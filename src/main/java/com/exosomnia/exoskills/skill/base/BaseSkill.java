package com.exosomnia.exoskills.skill.base;

public abstract class BaseSkill {

    private final int id;

    public BaseSkill(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
