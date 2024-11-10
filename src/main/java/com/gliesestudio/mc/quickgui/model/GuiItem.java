package com.gliesestudio.mc.quickgui.model;

import org.bukkit.Material;

public class GuiItem {

    private Material item;
    private String displayName;
    private String command;
    private int row;
    private int column;

    public GuiItem(Material item, String displayName, String command, int row, int column) {
        this.item = item;
        this.displayName = displayName;
        this.command = command;
        this.row = row;
        this.column = column;
    }

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "GuiItem{" +
                "item=" + item +
                ", displayName='" + displayName + '\'' +
                ", command='" + command + '\'' +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}