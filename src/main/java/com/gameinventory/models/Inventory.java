package com.gameinventory.models;

/** Инвентарь персонажа (1:1). Соответствует таблице inventory. */
public class Inventory {

    private long inventoryId;
    private GameCharacter character;
    private int maxSlots;

    public Inventory(long inventoryId, GameCharacter character, int maxSlots) {
        if (character == null) throw new IllegalArgumentException("Персонаж обязателен");
        if (maxSlots <= 0)
            throw new IllegalArgumentException("Число слотов должно быть положительным");
        this.inventoryId = inventoryId;
        this.character = character;
        this.maxSlots = maxSlots;
    }

    public Inventory(GameCharacter character, int maxSlots) {
        this(0, character, maxSlots);
    }

    public long getInventoryId() { return inventoryId; }
    public void setInventoryId(long inventoryId) { this.inventoryId = inventoryId; }
    public GameCharacter getCharacter() { return character; }
    public int getMaxSlots() { return maxSlots; }

    @Override
    public String toString() {
        return "Инвентарь " + character.getName() + " (" + maxSlots + " слотов)";
    }
}