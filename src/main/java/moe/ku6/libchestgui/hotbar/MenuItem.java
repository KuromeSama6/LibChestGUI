package moe.ku6.libchestgui.hotbar;

import lombok.Getter;
import lombok.Setter;
import moe.ku6.libchestgui.hotbar.handler.IMenuClickHandler;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class MenuItem {
    private ItemStack item;
    private IMenuClickHandler onLeftClick, onRightClick, onDrop;

    public MenuItem(final ItemStack item) {
        this.item = item;
    }


}
