package com.gameinventory.models;

import java.time.LocalDateTime;

/**
 * Игровой персонаж. Соответствует строке таблицы game_character.
 * Ссылается на Account и CharacterClass — как FK в БД.
 */
public class GameCharacter {

    private long characterId;
    private Account account;
    private String name;
    private CharacterClass characterClass;
    private int level;
    private long experience;
    private LocalDateTime createdAt;

    public GameCharacter(long characterId, Account account, String name,
                         CharacterClass characterClass, int level, long experience,
                         LocalDateTime createdAt) {
        if (account == null)
            throw new IllegalArgumentException("Аккаунт обязателен");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Имя персонажа обязательно");
        if (characterClass == null)
            throw new IllegalArgumentException("Класс персонажа обязателен");
        if (level < 1 || level > 100)
            throw new IllegalArgumentException("Уровень должен быть в диапазоне 1..100");
        if (experience < 0)
            throw new IllegalArgumentException("Опыт не может быть отрицательным");

        this.characterId = characterId;
        this.account = account;
        this.name = name.trim();
        this.characterClass = characterClass;
        this.level = level;
        this.experience = experience;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /** Удобный конструктор для нового персонажа: level = 1, опыт = 0. */
    public GameCharacter(Account account, String name, CharacterClass characterClass) {
        this(0, account, name, characterClass, 1, 0, LocalDateTime.now());
    }

    public long getCharacterId() { return characterId; }
    public void setCharacterId(long characterId) { this.characterId = characterId; }

    public Account getAccount() { return account; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CharacterClass getCharacterClass() { return characterClass; }

    public int getLevel() { return level; }
    public void setLevel(int level) {
        if (level < 1 || level > 100)
            throw new IllegalArgumentException("Уровень должен быть в диапазоне 1..100");
        this.level = level;
    }

    public long getExperience() { return experience; }
    public void setExperience(long experience) { this.experience = experience; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return name + " (" + characterClass.getClassName() + ", " + level + " ур.)";
    }
}