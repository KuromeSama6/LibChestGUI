package moe.ku6.libchestgui.gui;

import lombok.Data;
import moe.ku6.libchestgui.util.IntPair;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data
public class Activity {
    private int layer;
    private InventoryType location;
    private final HashSet<Integer> occupancy = new HashSet<>();
    private IntPair start, end;
    private final Map<Integer, ActivitySlot> slots = new HashMap<>();
    private IActionHandler defaultHandler = IActionHandler.Deny();
    private IPostClickHandler postClickHandler;
    private boolean keepOriginal;

    public Activity(int layer, InventoryType location, int startX, int startY, int endX, int endY) {
        this.layer = layer;
        if (startX < 0 || startY < 0 || endX < 0 || endY < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative");
        }

        this.location = location;
        this.start = new IntPair(startX, startY);
        this.end = new IntPair(endX, endY);

        // populate slots
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                occupancy.add(GetSlot(new IntPair(x, y)));
            }
        }

        keepOriginal = location == InventoryType.PLAYER;
    }

    public int GetSlot(IntPair pos) {
        // figure out slots
        switch (location) {
            case CHEST -> {
                // regular gui, regular rows, 9 columns, starts at 0
                return pos.x() + (pos.y() * 9);
            }
            case PLAYER -> {
                if (pos.y() > 4) throw new IndexOutOfBoundsException();
                if (pos.y() == 4) {
                    if (pos.x() >= 4) throw new IndexOutOfBoundsException();
                    return 3 - pos.x();
                }

                return pos.x() + (pos.y() * 9);
            }

            default -> throw new IllegalArgumentException("Unknown location");
        }
    }

    public ActivitySlot Get(int slot) {
        return slots.get(slot);
    }
    public ActivitySlot Get(IntPair pos) {
        int slot = GetSlot(pos);
        return Get(slot);
    }

    public void Set(IntPair pos, ItemStack item) {
        Set(pos, item, defaultHandler);
    }
    public void Set(IntPair pos, ItemStack item, IActionHandler handler) {
        int slot = GetSlot(pos);
        Set(slot, item, handler);
    }
    public void Set(int slot, ItemStack item) {
        Set(slot, item, defaultHandler);
    }
    public void Set(int slot, ItemStack item, IActionHandler handler) {
        if (item == null) {
            Remove(slot);
            return;
        }
        slots.put(slot, new ActivitySlot(item, handler));
    }

    public int FirstFree() {
        return occupancy.stream()
                .filter(slot -> !slots.containsKey(slot))
                .min(Comparator.naturalOrder())
                .orElse(-1);
    }
    public List<ItemStack> Add(ItemStack... items) {
        var overflow = new ArrayList<ItemStack>();
        for (ItemStack item : items) {
            int slot = FirstFree();
            if (slot == -1) {
                overflow.add(item);
                continue;
            }
            Set(slot, item);
        }
        return overflow;
    }

    public boolean Add(ItemStack item, IActionHandler handler) {
        int slot = FirstFree();
        if (slot == -1) {
            return false;
        }
        Set(slot, item, handler);
        return true;
    }

    public void Fill(IntPair from, IntPair to, ItemStack item) {
        Fill(from, to, item, defaultHandler);
    }
    public void Fill(IntPair from, IntPair to, ItemStack item, IActionHandler handler) {
        if (from.x() > to.x() || from.y() > to.y()) {
            throw new IllegalArgumentException("Invalid range");
        }
        for (int x = from.x(); x <= to.x(); x++) {
            for (int y = from.y(); y <= to.y(); y++) {
                int slot = GetSlot(new IntPair(x, y));
                Set(slot, item, handler);
            }
        }
    }
    public void Fill(int from, int to, ItemStack item) {
        Fill(from, to, item, defaultHandler);
    }
    public void Fill(int from, int to, ItemStack item, IActionHandler handler) {
        if (from > to) {
            throw new IllegalArgumentException("Invalid range");
        }
        for (int i = from; i <= to; i++) {
            Set(i, item, handler);
        }
    }
    public void Fill(ItemStack item) {
        for (var slot : occupancy) {
            Set(slot, item);
        }
    }
    public void Fill(ItemStack item, IActionHandler handler) {
        for (var slot : occupancy) {
            Set(slot, item, handler);
        }
    }

    public void Remove(int slot) {
        slots.remove(slot);
    }
    public void Remove(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("Invalid range");
        }
        for (int i = from; i <= to; i++) {
            Remove(i);
        }
    }
    public void Remove(IntPair from, IntPair to) {
        if (from.x() > to.x() || from.y() > to.y()) {
            throw new IllegalArgumentException("Invalid range");
        }
        for (int x = from.x(); x <= to.x(); x++) {
            for (int y = from.y(); y <= to.y(); y++) {
                int slot = GetSlot(new IntPair(x, y));
                Remove(slot);
            }
        }
    }
    public void RemoveAt(IntPair pos) {
        int slot = GetSlot(pos);
        Remove(slot);
    }
    public void Clear() {
        slots.clear();
    }

}
