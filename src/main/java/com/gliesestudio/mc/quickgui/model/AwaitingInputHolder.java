package com.gliesestudio.mc.quickgui.model;

import com.gliesestudio.mc.quickgui.enums.AwaitingInputType;
import com.gliesestudio.mc.quickgui.gui.SystemGuiHolder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class AwaitingInputHolder implements Serializable {

    @Serial
    private static final long serialVersionUID = -1261471195084887227L;

    private AwaitingInputType inputType;
    private SystemGuiHolder systemGuiHolder;
    private Integer editLorePosition;

    public AwaitingInputHolder inputType(AwaitingInputType inputType) {
        this.inputType = inputType;
        return this;
    }

    public AwaitingInputHolder systemGuiHolder(SystemGuiHolder systemGuiHolder) {
        this.systemGuiHolder = systemGuiHolder;
        return this;
    }

    public AwaitingInputHolder editLorePosition(int editLorePosition) {
        this.editLorePosition = editLorePosition;
        return this;
    }

}
