package com.gameinventory.models;

/** Качество предмета (обычный, редкий, эпический...). */
public class Quality {

    private long qualityId;
    private String qualityName;
    private String colorCode;

    public Quality(long qualityId, String qualityName, String colorCode) {
        if (qualityName == null || qualityName.isBlank())
            throw new IllegalArgumentException("Название качества обязательно");
        // Цвет, если задан, должен быть в формате #RRGGBB
        if (colorCode != null && !colorCode.matches("^#[0-9A-Fa-f]{6}$"))
            throw new IllegalArgumentException("Цвет должен быть в формате #RRGGBB");
        this.qualityId = qualityId;
        this.qualityName = qualityName.trim();
        this.colorCode = colorCode;
    }

    public long getQualityId() { return qualityId; }
    public void setQualityId(long qualityId) { this.qualityId = qualityId; }
    public String getQualityName() { return qualityName; }
    public String getColorCode() { return colorCode; }

    @Override
    public String toString() { return qualityName; }
}