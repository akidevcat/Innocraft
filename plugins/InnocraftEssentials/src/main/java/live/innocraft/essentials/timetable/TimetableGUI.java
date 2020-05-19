package live.innocraft.essentials.timetable;

import live.innocraft.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class TimetableGUI implements Listener {

    private Essentials plugin;
    private Inventory inventory;
    private final Timetable timetable;

    public TimetableGUI (Essentials plugin, Timetable timetable) {
        this.plugin = plugin;
        this.timetable = timetable;

        inventory = Bukkit.createInventory(null, 9, "");

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void Update(Set<TimetableLesson> lessons) {
        inventory = Bukkit.createInventory(null, 9 * 3, ChatColor.translateAlternateColorCodes('&', timetable.GetGUIName()));

        TreeSet<TimetableLesson> lessonsSorted = new TreeSet<TimetableLesson>(lessons);

        int i = 1;
        for (TimetableLesson lesson : lessonsSorted) {
            final ItemStack item = new ItemStack(lesson.Icon, Math.max(1, lesson.TimeStart / 60));
            final ItemMeta meta = item.getItemMeta();
            List<String> loreFormat = timetable.GetGUILoreFormat();

            for (int e = 0; e < loreFormat.size(); e++) {
                String parsedLine = timetable.ApplyPlaceholders(loreFormat.get(e), lesson);
                if (parsedLine.contains("%skip%")) {
                    loreFormat.remove(e--);
                    continue;
                }
                loreFormat.set(e, ChatColor.translateAlternateColorCodes('&', parsedLine));
            }

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', lesson.Name));
            meta.setLore(loreFormat);

            item.setItemMeta(meta);

            inventory.addItem(item);

            i++;
        }
    }

    public void Open(final HumanEntity ent) {
        ent.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory))
            e.setCancelled(true);
    }

}
