package moe.ku6.libchestgui.gui;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
@Getter
public class ActionContext {
    private final Form form;
    private final Activity activity;
    private final InventoryClickEvent event;
    private final boolean left, right, shift, dropped;
    private boolean allowed, popNow, closeNow;

    public void Allow() {
        allowed = true;
    }

    public void Deny() {
        allowed = false;
    }

    public void Pop() {
        popNow = true;

    }
    public void CloseAll() {
        closeNow = true;
    }
}
