package com.gameinventory.models;

/** Класс персонажа (воин, маг, лучник и т.п.). */
public class CharacterClass {

    private long classId;
    private String className;
    private String description;

    public CharacterClass(long classId, String className, String description) {
        if (className == null || className.isBlank())
            throw new IllegalArgumentException("Название класса обязательно");
        this.classId = classId;
        this.className = className.trim();
        this.description = description;
    }

    public long getClassId() { return classId; }
    public void setClassId(long classId) { this.classId = classId; }
    public String getClassName() { return className; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return className; }
}