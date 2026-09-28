package com.gameinventory.models;

/** Слот экипировки (голова, грудь, оружие...). */
public class EquipSlot {

    private long slotId;
    private String slotName;

    public EquipSlot(long slotId, String slotName) {
        if (slotName == null || slotName.isBlank())
            throw new IllegalArgumentException("Название слота обязательно");
        this.slotId = slotId;
        this.slotName = slotName.trim();
    }

    public long getSlotId() { return slotId; }
    public void setSlotId(long slotId) { this.slotId = slotId; }
    public String getSlotName() { return slotName; }

    @Override
    public String toString() { return slotName; }
}