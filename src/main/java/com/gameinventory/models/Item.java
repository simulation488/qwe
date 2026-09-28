package com.gameinventory.models;

/** Предмет из справочника. Соответствует таблице item. */
public class Item {

    private long itemId;
    private String itemName;
    private ItemType itemType;
    private Quality quality;
    private int requiredLevel;
    private int maxStack;
    private boolean equippable;
    private EquipSlot equipSlot;   // null, если предмет не экипируется

    public Item(long itemId, String itemName, ItemType itemType, Quality quality,
                int requiredLevel, int maxStack, boolean equippable, EquipSlot equipSlot) {
        if (itemName == null || itemName.isBlank())
            throw new IllegalArgumentException("Название предмета обязательно");
        if (itemType == null) throw new IllegalArgumentException("Тип обязателен");
        if (quality == null) throw new IllegalArgumentException("Качество обязательно");
        if (requiredLevel < 1)
            throw new IllegalArgumentException("Требуемый уровень должен быть >= 1");
        if (maxStack < 1)
            throw new IllegalArgumentException("Максимальный размер стака должен быть >= 1");
        if (equippable && equipSlot == null)
            throw new IllegalArgumentException(
                    "Экипируемый предмет должен иметь слот экипировки");

        this.itemId = itemId;
        this.itemName = itemName.trim();
        this.itemType = itemType;
        this.quality = quality;
        this.requiredLevel = requiredLevel;
        this.maxStack = maxStack;
        this.equippable = equippable;
        this.equipSlot = equipSlot;
    }

    public long getItemId() { return itemId; }
    public void setItemId(long itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public ItemType getItemType() { return itemType; }
    public Quality getQuality() { return quality; }
    public int getRequiredLevel() { return requiredLevel; }
    public int getMaxStack() { return maxStack; }
    public boolean isEquippable() { return equippable; }
    public EquipSlot getEquipSlot() { return equipSlot; }

    @Override
    public String toString() { return itemName; }
}