package moe.ku6.libchestgui.gui;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
@Getter
public class ActionContext {
    private final Form form;
    private final Activity activity;
    private final InventoryClickEvent event;
    private final boolean left, right, shift, dropped;
    @Setter
    private boolean allowed;
    private boolean popNow, closeNow, rerenderNow, refreshNow;

    public void Allow() {
        allowed = true;
    }

    public void Deny() {
        allowed = false;
    }

    public void Pop() {
        popNow = true;

    }

    /**
     * Rerenders the current form. This will close the current inventory and open a new one with the updated form.
     * Use this if the title of the form or any of its otherwise unmutable properties have changed.
     */
    public void Rerender() {
        rerenderNow = true;
    }

    /**
     * Refreshes the contents of the current form without closing the inventory.
     */
    public void Refresh() {
        refreshNow = true;
    }

    public void CloseAll() {
        closeNow = true;
    }
}
