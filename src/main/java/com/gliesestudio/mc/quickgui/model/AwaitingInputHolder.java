package com.gliesestudio.mc.quickgui.model;

import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.inventory.SystemGuiHolder;

import java.io.Serial;
import java.io.Serializable;

public class AwaitingInputHolder implements Serializable {

    @Serial
    private static final long serialVersionUID = -1261471195084887227L;

    private AwaitingInputType inputType;
    private SystemGuiHolder systemGuiHolder;

    public AwaitingInputHolder inputType(AwaitingInputType inputType) {
        this.inputType = inputType;
        return this;
    }

    public AwaitingInputHolder systemGuiHolder(SystemGuiHolder systemGuiHolder) {
        this.systemGuiHolder = systemGuiHolder;
        return this;
    }

    public AwaitingInputType getInputType() {
        return inputType;
    }

    public SystemGuiHolder getSystemGuiHolder() {
        return systemGuiHolder;
    }

}
