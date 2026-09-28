package com.gameinventory.controllers;

import com.gameinventory.models.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер экрана инвентаря.
 * Работает с таблицей слотов и кнопками действий.
 *
 * Особенность: таблица показывает ВСЕ слоты инвентаря (0..maxSlots-1),
 * даже если в БД для них ещё нет строк. Пустые строки — это временные
 * объекты с slotId=0; при сохранении они пойдут через INSERT.
 */
public class CharacterController {

    @FXML private Label headerLabel;
    @FXML private TableView<InventorySlot> slotsTable;
    @FXML private TableColumn<InventorySlot, Number> slotNumberColumn;
    @FXML private TableColumn<InventorySlot, String> itemNameColumn;
    @FXML private TableColumn<InventorySlot, String> qualityColumn;
    @FXML private TableColumn<InventorySlot, Number> quantityColumn;
    @FXML private TableColumn<InventorySlot, String> equippedColumn;
    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField quantityField;
    @FXML private Label messageLabel;

    private final GameManager manager = GameManager.getInstance();
    private GameCharacter character;
    private Inventory inventory;

    @FXML
    public void initialize() {
        // Привязка колонок таблицы к полям объекта InventorySlot
        slotNumberColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getSlotNumber()));

        itemNameColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getItem() == null
                        ? "—" : c.getValue().getItem().getItemName()));

        qualityColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getItem() == null
                        ? "" : c.getValue().getItem().getQuality().getQualityName()));

        quantityColumn.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getQuantity()));

        equippedColumn.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isEquipped() ? "да" : ""));
    }

    /** Вызывается из MainController перед показом экрана. */
    public void setCharacter(GameCharacter character) {
        this.character = character;
        headerLabel.setText(character.getName()
                + " — " + character.getCharacterClass().getClassName()
                + ", " + character.getLevel() + " ур.");

        this.inventory = manager.getInventoryFor(character);
        itemComboBox.setItems(FXCollections.observableArrayList(manager.getItemCatalog()));
        refreshSlots();
    }

    /**
     * Обновляет таблицу слотов.
     * Строит полный список 0..maxSlots-1, подставляя данные из БД там,
     * где слот уже существует, и создавая пустую «заглушку» там, где нет.
     */
    private void refreshSlots() {
        // 1. Загружаем существующие слоты из БД
        List<InventorySlot> existing = manager.getSlots(inventory);

        // 2. Индексируем их по номеру слота
        Map<Integer, InventorySlot> byNumber = new HashMap<>();
        for (InventorySlot s : existing) {
            byNumber.put(s.getSlotNumber(), s);
        }

        // 3. Строим полный список слотов 0..maxSlots-1
        List<InventorySlot> full = new ArrayList<>();
        for (int i = 0; i < inventory.getMaxSlots(); i++) {
            InventorySlot s = byNumber.get(i);
            if (s == null) {
                s = new InventorySlot(inventory, i);  // slotId = 0 → INSERT при сохранении
            }
            full.add(s);
        }

        slotsTable.setItems(FXCollections.observableArrayList(full));
    }

    @FXML
    private void onAddItem() {
        messageLabel.setText("");
        InventorySlot selected = slotsTable.getSelectionModel().getSelectedItem();
        Item item = itemComboBox.getValue();
        if (selected == null) { messageLabel.setText("Выберите слот"); return; }
        if (item == null)     { messageLabel.setText("Выберите предмет"); return; }

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            messageLabel.setText("Количество должно быть числом");
            return;
        }

        try {
            selected.putItem(item, qty);
            manager.saveSlot(selected);
            refreshSlots();
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML private void onEquip()   { toggleEquip(true); }
    @FXML private void onUnequip() { toggleEquip(false); }

    private void toggleEquip(boolean equip) {
        messageLabel.setText("");
        InventorySlot selected = slotsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { messageLabel.setText("Выберите слот"); return; }
        try {
            selected.setEquipped(equip);
            manager.saveSlot(selected);
            refreshSlots();
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onClear() {
        messageLabel.setText("");
        InventorySlot selected = slotsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { messageLabel.setText("Выберите слот"); return; }
        selected.clear();
        manager.saveSlot(selected);
        refreshSlots();
    }

    @FXML
    private void onDelete() {
        messageLabel.setText("");
        InventorySlot selected = slotsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { messageLabel.setText("Выберите слот"); return; }
        manager.removeSlot(selected);
        refreshSlots();
    }

    @FXML
    private void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/gameinventory/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) headerLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.setTitle("Учёт игрового инвентаря");
        } catch (IOException e) {
            messageLabel.setText("Ошибка возврата: " + e.getMessage());
        }
    }
}