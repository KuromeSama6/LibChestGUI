package moe.ku6.libchestgui.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import moe.ku6.libchestgui.util.IntPair;
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
    private IFormUpdateHandler onUpdate;
    private IDropItemHandler onDropItem;
    private IPostDropItemHandler onPostDropItem;

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

    public void Render(PlayerInventory inventory, Inventory top) {
        activities.sort(Comparator.comparingInt(c -> -c.getLayer()));
        for (var activity : activities) {
            Render(inventory, top, activity);
        }
    }

    public void Render(PlayerInventory bottom, Inventory top, Activity activity) {
        var slots = activity.getSlots();
        var inventory = activity.getLocation() == InventoryType.PLAYER ? bottom : top;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!activity.getOccupancy().contains(i)) continue;

            var slot = slots.get(i);
            if (slot == null) {
                if (!activity.isKeepOriginal()) {
                    inventory.setItem(i, null);
                }
            } else {
                inventory.setItem(i, slot.getItem());
            }
        }
    }

    public Activity GetActivityAt(InventoryType type, int x, int y) {
        return activities.stream()
                .sorted(Comparator.comparingInt(c -> -c.getLayer()))
                .filter(c -> c.getLocation() == type)
                .filter(c -> c.getOccupancy().contains(c.GetSlot(new IntPair(x, y))))
                .findFirst()
                .orElse(null);
    }

    public Activity GetActivity(InventoryType type, int startX, int startY, int endX, int endY) {
        return activities.stream()
                .sorted(Comparator.comparingInt(c -> -c.getLayer()))
                .filter(c -> c.getLocation() == type)
                .filter(c -> c.getStart().equals(new IntPair(startX, startY)) && c.getEnd().equals(new IntPair(endX, endY)))
                .findFirst()
                .orElse(null);
    }

    public ActionContext Handle(InventoryClickEvent e) {
        var view = e.getView();
        var inv = e.getClickedInventory();
        InventoryType location;


        var slot = e.getSlot();
        if (e.getSlot() < 0) {
            // dropped
            if (onDropItem != null) {
                onDropItem.Handle(e);
                return null;
            }
        }

        if (inv == view.getBottomInventory()) location = InventoryType.PLAYER;
        else if (inv == view.getTopInventory()) {
            //TODO: More inventory types
            location = InventoryType.CHEST;
        } else {
            location = null;
        }

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
//        System.out.println("click=%s, action=%s, slot=%s, activitySlot=%s".formatted(e.getClick(), e.getAction(), slot, activitySlot));
        var handler = activitySlot == null ? activity.getDefaultHandler() : activitySlot.getHandler();
        handler.Handle(ctx);
//        System.out.println("allowed=%s".formatted(ctx.isAllowed()));
        if (!ctx.isAllowed()) {
            e.setCancelled(true);
        }

        return ctx;
    }
}
