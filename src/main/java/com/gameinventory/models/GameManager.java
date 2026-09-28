package com.gameinventory.models;

import com.gameinventory.dao.*;

import java.util.List;

/**
 * Фасад бизнес-логики. Singleton.
 * Хранит текущий аккаунт, делегирует работу в DAO.
 * UI общается только с GameManager, а не с DAO напрямую.
 */
public class GameManager {

    private static GameManager instance;

    private final AccountDAO       accountDAO   = new AccountDAO();
    private final CharacterDAO     characterDAO = new CharacterDAO();
    private final InventoryDAO     inventoryDAO = new InventoryDAO();
    private final InventorySlotDAO slotDAO      = new InventorySlotDAO();
    private final ItemDAO          itemDAO      = new ItemDAO();

    private Account currentAccount;

    private GameManager() {}

    public static synchronized GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    // -------- Аккаунт --------

    /** Попытка входа. Возвращает аккаунт или null. */
    public Account login(String email, String password) {
        Account acc = accountDAO.findByEmailAndPassword(email, password);
        if (acc != null) currentAccount = acc;
        return acc;
    }

    public void logout() { currentAccount = null; }

    public Account getCurrentAccount() { return currentAccount; }

    // -------- Персонажи --------

    public List<GameCharacter> getCharactersOfCurrentAccount() {
        if (currentAccount == null) return List.of();
        return characterDAO.findByAccountId(currentAccount.getAccountId());
    }

    // -------- Инвентарь --------

    /** Найти или создать инвентарь персонажа. */
    public Inventory getInventoryFor(GameCharacter character) {
        Inventory inv = inventoryDAO.findByCharacterId(character.getCharacterId());
        if (inv == null) {
            inv = inventoryDAO.create(new Inventory(character, 20));
        }
        return inv;
    }

    public List<InventorySlot> getSlots(Inventory inventory) {
        return slotDAO.findByInventoryId(inventory.getInventoryId());
    }

    public InventorySlot saveSlot(InventorySlot slot) {
        return slotDAO.save(slot);
    }

    public boolean removeSlot(InventorySlot slot) {
        return slotDAO.delete(slot.getSlotId());
    }

    public List<Item> getItemCatalog() {
        return itemDAO.findAll();
    }
}