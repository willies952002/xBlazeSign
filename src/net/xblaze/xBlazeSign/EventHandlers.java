package net.xblaze.xBlazeSign;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.xblaze.xBlazeCore.api.util.InventoryManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

public class EventHandlers implements Listener {
	public InventoryManager invman = new InventoryManager();
	public static ItemStack i;
	public static Player p;
	public Plugin pl;
	
	public EventHandlers(Plugin pl) {
		this.pl = pl;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (player.hasMetadata("sign-index")) {
			int index = player.getMetadata("sign-index").get(0).asInt();
			for (int i = 0; i < index; i++) {
				player.removeMetadata("sign-storage-" + i, pl);
			}
			player.removeMetadata("sign-index", pl);
		}
		if (player.getEnderChest().contains(Material.WRITTEN_BOOK)) {
			ListIterator<ItemStack> hi = player.getEnderChest().iterator();
			while (hi.hasNext()) {
				ItemStack item = (ItemStack)hi.next();
				try {
					BookMeta bm = (BookMeta)item.getItemMeta();
					if ((!bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
						Player owner = Bukkit.getPlayer(bm.getAuthor());
						player.getEnderChest().remove(item);
						owner.getInventory().addItem(new ItemStack[] { item });
					}
				} catch (ClassCastException ignore) {
					
				} catch (NullPointerException ignore) {
					
				}
			}
		}
		Player owner;
		if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
			ListIterator<ItemStack> hi = player.getInventory().iterator();
			while (hi.hasNext()) {
				ItemStack item = (ItemStack)hi.next();
				try {
					BookMeta bm = (BookMeta)item.getItemMeta();
					if ((!bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
						owner = Bukkit.getPlayer(bm.getAuthor());
						player.getInventory().remove(item);
						owner.getInventory().addItem(new ItemStack[] { item });
					}
				}
				catch (ClassCastException localClassCastException1) {}catch (NullPointerException localNullPointerException1) {}
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getInventory().contains(Material.WRITTEN_BOOK)) {
				ListIterator<ItemStack> hi = p.getInventory().iterator();
				while (hi.hasNext()) {
					ItemStack item = (ItemStack)hi.next();
					try {
						BookMeta bm = (BookMeta)item.getItemMeta();
						if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
							p.getInventory().remove(item);
							player.getInventory().addItem(new ItemStack[] { item });
						}
					}
					catch (ClassCastException localClassCastException2) {}catch (NullPointerException localNullPointerException2) {}
				}
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getEnderChest().contains(Material.WRITTEN_BOOK)) {
				ListIterator<ItemStack> hi = p.getEnderChest().iterator();
				while (hi.hasNext()) {
					ItemStack item = (ItemStack)hi.next();
					try {
						BookMeta bm = (BookMeta)item.getItemMeta();
						if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
							p.getEnderChest().remove(item);
							player.getInventory().addItem(new ItemStack[] { item });
						}
					} catch (ClassCastException ignore) {} catch (NullPointerException ignore) {}
				}
			}
		}
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		ItemStack i = e.getItemDrop().getItemStack();
		if ((Bukkit.getServer().getPluginManager().isPluginEnabled("xBlazeBand")) && (i.getType() == Material.PAPER)) {
			return;
		}
		if (i.getType().equals(Material.WRITTEN_BOOK)) {
			try {
				BookMeta bm = (BookMeta)i.getItemMeta();
				if (bm.getTitle().equalsIgnoreCase("Color Book")) {
					e.getItemDrop().remove();
				}
			}
			catch (ClassCastException localClassCastException) {}
		}
		List<Entity> nearbyE = e.getPlayer().getNearbyEntities(8.0D, 8.0D, 8.0D);
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();
		for (Entity ent : nearbyE) {
			if ((ent instanceof LivingEntity)) {
				livingE.add((LivingEntity)ent);
			}
		}
		BlockIterator it = new BlockIterator(e.getPlayer(), 10);
		for (LivingEntity ent : livingE) {
			Block block = it.next();
			int bx = block.getX();
			int by = block.getY();
			int bz = block.getZ();

			Location loc = ent.getLocation();
			double ex = loc.getX();
			double ey = loc.getY();
			double ez = loc.getZ();
			if ((bx - 0.75D <= ex) && (ex <= bx + 1.75D) && (bz - 0.75D <= ez) && (ez <= bz + 1.75D) && (by - 1 <= ey) && (ey <= by + 2.5D)) {
				if ((ent instanceof Player)) {
					Player target = (Player)ent;
					if (isOnline(target)) {
						if (!((p.isSneaking() && target.isSneaking()) && !(target.hasMetadata("dropper")))) return;
						if (target.getInventory().getHeldItemSlot() == 8) {
							this.invman.replace(target, 7, e.getItemDrop().getItemStack());
						} else {
							this.invman.hold(target, e.getItemDrop().getItemStack());
						}
						e.getItemDrop().remove();
						return;
					}
					if (e.getItemDrop().getItemStack().getType().equals(Material.WRITTEN_BOOK)) {
						e.getPlayer().sendMessage(ChatColor.GRAY + target.getName() + " has signed your book with a test message.");
						e.getPlayer().sendMessage(ChatColor.GRAY + "If you wish to remove the test signature, please use /rmpage");
						this.invman.hold(target, e.getItemDrop().getItemStack());
						BookMeta bookmeta = (BookMeta)target.getItemInHand().getItemMeta();
						if ((bookmeta.hasTitle()) && (bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
							bookmeta.getPages();
							String buffer = "This is a test message.";
							bookmeta.addPage(new String[] { buffer + "\n§0" + "-" + target.getName() });
							target.getItemInHand().setItemMeta(bookmeta);
							target.getWorld().dropItemNaturally(target.getLocation(), target.getItemInHand());
							target.setItemInHand(new ItemStack(Material.AIR));
							e.getItemDrop().remove();
							return;
						}
					}
				}
			}
		}
	}

	public boolean isOnline(Player p) {
		for (Player pl : Bukkit.getOnlinePlayers() ) {
			if (p.equals(pl)) {
				return true;
			}
		}
		return false;
	}
}
