package moe.ku6.libchestgui.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class ActivitySlot {
    private final ItemStack item;
    @NonNull
    private IActionHandler handler;
}
