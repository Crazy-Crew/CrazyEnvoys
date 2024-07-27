package com.badbones69.crazyenvoys.config.beans;

public class GuiProperty {

    private String title;
    private int size;

    public GuiProperty(final String title, final int size) {
        this.title = title;
        this.size = size;
    }

    public GuiProperty() {
        this.title = "&cEnvoy Drops";
        this.size = 27;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public final String getTitle() {
        return this.title;
    }

    public final int getSize() {
        return this.size;
    }
}