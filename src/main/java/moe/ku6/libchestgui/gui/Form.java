package moe.ku6.libchestgui.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class Form {
    private String title;
    private InventoryType location;
    private int rows;
    private final List<Activity> activities = new ArrayList<>();
    private IFormOpenHandler onOpen;
    private IFormCloseHandler onClose;

    public void EnsureValid() {
        if (activities.isEmpty()) throw new IllegalArgumentException("No activities");
        if (location == InventoryType.PLAYER)
            throw new IllegalArgumentException("Player location is not supported");
    }

    public InventoryType GetInventoryType() {
        EnsureValid();
        List<InventoryType> ret = new ArrayList<>();
        for (var activity : activities) {
            if (!ret.contains(activity.getLocation())) ret.add(activity.getLocation());
        }

        if (ret.size() == 1) return ret.get(0);
        if (ret.size() == 2) return ret.get(0) == InventoryType.PLAYER ? ret.get(1) : ret.get(0);

        throw new IllegalArgumentException("Multiple inventory types: %s".formatted(ret));
    }

    public void Render(PlayerInventory bottom, Inventory top) {
        activities.sort(Comparator.comparingInt(c -> -c.getLayer()));
        for (var activity : activities) {
            var slots = activity.getSlots();
            for (var slot : slots.entrySet()) {
                var item = slot.getValue().getItem();
                switch (activity.getLocation()) {
                    case PLAYER -> {
                        bottom.setItem(slot.getKey(), item);
                    }
                    case CHEST -> {
                        top.setItem(slot.getKey(), item);
                    }
                }
            }
        }
    }

    public ActionContext Handle(InventoryClickEvent e) {
        var view = e.getView();
        var inv = e.getClickedInventory();
        InventoryType location;

        if (inv == view.getBottomInventory()) location = InventoryType.PLAYER;
        else if (inv == view.getTopInventory()) {
            //TODO: More inventory types
            location = InventoryType.CHEST;
        } else {
            location = null;
        }

        var slot = e.getSlot();
        var activity = activities.stream()
                .sorted(Comparator.comparingInt(c -> -c.getLayer()))
                .filter(c -> c.getLocation() == location)
                .filter(a -> a.getOccupancy().contains(slot))
                .findFirst()
                .orElse(null);

        if (activity == null) {
            e.setCancelled(true);
            return null;
        }

        var clickType = e.getClick();
        var ctx = new ActionContext(
                this,
                activity,
                e,
                clickType == ClickType.LEFT,
                clickType == ClickType.RIGHT,
                clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT,
                e.getAction() == InventoryAction.DROP_ALL_CURSOR || e.getAction() == InventoryAction.DROP_ONE_CURSOR || e.getAction() == InventoryAction.DROP_ALL_SLOT || e.getAction() == InventoryAction.DROP_ONE_SLOT
        );

        var activitySlot = activity.Get(slot);
        if (activitySlot != null) {
            activitySlot.getHandler().Handle(ctx);
            if (!ctx.isAllowed()) {
                e.setCancelled(true);
            }

        } else {
            if (!activity.isMutable()) e.setCancelled(true);
        }

        return ctx;
    }
}
