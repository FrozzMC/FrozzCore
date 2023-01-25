package me.thejokerdev.frozzcore.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.thejokerdev.frozzcore.SpigotMain;
import me.thejokerdev.frozzcore.enums.Modules;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class JumpPadsListener implements Listener {
    private SpigotMain plugin;

    public JumpPadsListener(SpigotMain plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL) && plugin.getUtils().isWorldProtected(e.getPlayer().getWorld(), Modules.JUMPPADS)) {
            Block b = e.getClickedBlock();
            Material m = b.getType();
            XMaterial mat = XMaterial.valueOf(plugin.getConfig().getString("jumppads.material", "OAK_PRESSURE_PLATE").toUpperCase());
            if (mat.parseMaterial().equals(m)) {
                Player p = e.getPlayer();

                double height = plugin.getConfig().getDouble("jumppads.height", 1.0);
                double length = plugin.getConfig().getDouble("jumppads.length", 1.0);
                XSound sound = XSound.valueOf(plugin.getConfig().getString("jumppads.sound", "BLOCK_WOOL_BREAK").toUpperCase());

                p.setVelocity(p.getLocation().getDirection().multiply(length));
                p.setVelocity(new Vector(p.getVelocity().getX(), height, p.getVelocity().getZ()));
                sound.play(p, 1.5f, 1f);

                e.setCancelled(true);
            }
        }
    }
}