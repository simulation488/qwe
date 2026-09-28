package com.gameinventory.models;

/**
 * Слот инвентаря. Может быть пустым (item = null).
 *
 * Инварианты:
 *  - quantity >= 0;
 *  - quantity <= item.maxStack, если предмет задан;
 *  - isEquipped = true только если item != null и item.isEquippable().
 */
public class InventorySlot {

    private long slotId;
    private Inventory inventory;
    private int slotNumber;
    private Item item;
    private int quantity;
    private boolean equipped;

    public InventorySlot(long slotId, Inventory inventory, int slotNumber,
                         Item item, int quantity, boolean equipped) {
        if (inventory == null) throw new IllegalArgumentException("Инвентарь обязателен");
        if (slotNumber < 0) throw new IllegalArgumentException("Номер слота >= 0");
        if (quantity < 0) throw new IllegalArgumentException("Количество >= 0");
        if (item != null && quantity > item.getMaxStack())
            throw new IllegalArgumentException(
                    "Количество превышает максимальный стак (" + item.getMaxStack() + ")");
        if (equipped && (item == null || !item.isEquippable()))
            throw new IllegalArgumentException(
                    "Экипировать можно только экипируемый предмет");

        this.slotId = slotId;
        this.inventory = inventory;
        this.slotNumber = slotNumber;
        this.item = item;
        this.quantity = quantity;
        this.equipped = equipped;
    }

    /** Пустой слот. */
    public InventorySlot(Inventory inventory, int slotNumber) {
        this(0, inventory, slotNumber, null, 0, false);
    }

    /** Положить предмет в слот. */
    public void putItem(Item item, int quantity) {
        if (item == null) throw new IllegalArgumentException("Предмет обязателен");
        if (quantity < 0) throw new IllegalArgumentException("Количество >= 0");
        if (quantity > item.getMaxStack())
            throw new IllegalArgumentException(
                    "Количество превышает максимальный стак (" + item.getMaxStack() + ")");
        this.item = item;
        this.quantity = quantity;
        if (!item.isEquippable()) this.equipped = false;
    }

    /** Очистить слот. */
    public void clear() {
        this.item = null;
        this.quantity = 0;
        this.equipped = false;
    }

    public void setEquipped(boolean equipped) {
        if (equipped && (item == null || !item.isEquippable()))
            throw new IllegalArgumentException(
                    "Экипировать можно только экипируемый предмет");
        this.equipped = equipped;
    }

    public long getSlotId() { return slotId; }
    public void setSlotId(long slotId) { this.slotId = slotId; }
    public Inventory getInventory() { return inventory; }
    public int getSlotNumber() { return slotNumber; }
    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
    public boolean isEquipped() { return equipped; }

    @Override
    public String toString() {
        if (item == null) return "Слот " + slotNumber + ": пусто";
        return "Слот " + slotNumber + ": " + item.getItemName()
                + " ×" + quantity + (equipped ? " [экип.]" : "");
    }
}