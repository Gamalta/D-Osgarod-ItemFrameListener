package fr.gamalta.osgarod.itemframe;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    String parentFileName = "ItemFrameListener";
    Configuration settingsCFG = new Configuration(this, parentFileName, "Settings");
    Configuration playersCFG = new Configuration(this, parentFileName, "Players");

    @Override
    public void onEnable() {

        settingsCFG.loadConfig();
        playersCFG.loadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("OpenItemFrame").setExecutor(this);
        getCommand("OpenItemFrame").setTabCompleter(this);

    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        Entity entity = event.getRightClicked();

        if (event.getRightClicked() instanceof ItemFrame) {

            ItemFrame itemFrame = (ItemFrame) entity;

            if (!player.isSneaking()) {

                Block block = itemFrame.getLocation().getBlock().getRelative(itemFrame.getAttachedFace());

                if (block.getState() instanceof Container) {

                    if (playersCFG.contains("ItemFrameListener." + uuid) && playersCFG.getBoolean("ItemFrameListener." + uuid)) {

                        event.setCancelled(true);
                        player.openInventory(((Container) block.getState()).getInventory());

                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();

            if (args.length >= 1) {

                switch (args[0].toLowerCase()) {

                    case "enable":

                        playersCFG.set("ItemFrameListener." + uuid, true);
                        player.spigot().sendMessage(new Messages(this, settingsCFG, "Enable").create());

                        break;

                    case "disable":

                        playersCFG.set("ItemFrameListener." + uuid, false);
                        player.spigot().sendMessage(new Messages(this, settingsCFG, "Disable").create());

                        break;
                }
            } else {

                if (playersCFG.contains("ItemFrameListener." + uuid)) {

                    if (playersCFG.getBoolean("ItemFrameListener." + uuid)) {

                        playersCFG.set("ItemFrameListener." + uuid, false);
                        player.spigot().sendMessage(new Messages(this, settingsCFG, "Disable").create());

                    } else {

                        playersCFG.set("ItemFrameListener." + uuid, true);
                        player.spigot().sendMessage(new Messages(this, settingsCFG, "Enable").create());
                    }
                } else {

                    playersCFG.set("ItemFrameListener." + uuid, true);
                    player.spigot().sendMessage(new Messages(this, settingsCFG, "Enable").create());
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        ArrayList<String> tabComplete = new ArrayList<>();

        if (args.length == 1) {

            if (args[0].equals("")) {

                tabComplete.add("enable");
                tabComplete.add("disable");

            } else {

                if ("enable".startsWith(args[0].toLowerCase())) {

                    tabComplete.add("enable");
                }

                if ("disable".startsWith(args[0].toLowerCase())) {

                    tabComplete.add("disable");
                }
            }
        }

        Collections.sort(tabComplete);

        return tabComplete;

    }
}
