package com.gameinventory.models;

/** Тип предмета (оружие, броня, зелье...). */
public class ItemType {

    private long itemTypeId;
    private String typeName;

    public ItemType(long itemTypeId, String typeName) {
        if (typeName == null || typeName.isBlank())
            throw new IllegalArgumentException("Название типа обязательно");
        this.itemTypeId = itemTypeId;
        this.typeName = typeName.trim();
    }

    public long getItemTypeId() { return itemTypeId; }
    public void setItemTypeId(long itemTypeId) { this.itemTypeId = itemTypeId; }
    public String getTypeName() { return typeName; }

    @Override
    public String toString() { return typeName; }
}