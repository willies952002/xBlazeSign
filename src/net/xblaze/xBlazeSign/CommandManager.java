package net.xblaze.xBlazeSign;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.xblaze.xBlazeCore.api.nms.NmsManager;
import net.xblaze.xBlazeCore.api.util.InventoryManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class CommandManager implements CommandExecutor {
	//	private BlazeSign plugin;
	private BookMeta bookmeta;
	private String buffer;
	private ItemStack newbook;
	private InventoryManager invman;
	private NmsManager nmsman;
	private Plugin pl;

	public CommandManager(BlazeSign pl) {
		this.nmsman = pl.nmsman;
		this.invman = pl.invman;
		this.pl = pl;
	}

	public void sendHelp(Player p, String command, String usage, String desc) {
		if (usage != null) {
			usage = " " + usage;
			this.nmsman.newFancyMessage("")
			.then(command + ChatColor.GRAY + usage)
			.color(ChatColor.GOLD)
			.suggest(command + usage)
			.tooltip("§bClick for Example!")
			.then(ChatColor.DARK_GRAY + " - ")
			.then(desc)
			.color(ChatColor.GREEN)
			.send(p);
		} else {
			this.nmsman.newFancyMessage(ChatColor.GOLD + command + ChatColor.DARK_GRAY + " - ").command(command).tooltip("§bClick to Execute!").then(desc).color(ChatColor.GREEN).send(p);
		}
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("newbook")) {
			if (player.hasMetadata("sign-index")) {
				int index = player.getMetadata("sign-index").get(0).asInt();
				for (int i = 0; i < index; i++) {
					player.removeMetadata("sign-storage-" + i, pl);
				}
				player.removeMetadata("sign-index", pl);
			}
			if ((Bukkit.getServer().getPluginManager().isPluginEnabled("xBlazeBand")) && (player.getInventory().getHeldItemSlot() == 8)){
				player.sendMessage(ChatColor.RED + "You have a magic band in this slot. You are not permitted to remove it!");
				return true;
			}
			if (player.getEnderChest().contains(Material.WRITTEN_BOOK)) {
				ListIterator<ItemStack> hi = player.getEnderChest().iterator();
				while (hi.hasNext()) {
					ItemStack item = (ItemStack)hi.next();
					try {
						BookMeta bm = (BookMeta)item.getItemMeta();
						if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
							Player owner = Bukkit.getPlayer(bm.getAuthor());
							player.getEnderChest().remove(item);
							owner.getInventory().addItem(new ItemStack[] { item });
							this.invman.hold(owner, item);
							player.sendMessage(ChatColor.GREEN + "Your Autograph book was located. If you would like a new one, please get rid of this one.");
							return true;
						}
					}
					catch (ClassCastException localClassCastException) {}catch (NullPointerException localNullPointerException) {}
				}
			}
			if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
				ListIterator<ItemStack> hi = player.getInventory().iterator();
				while (hi.hasNext()) {
					ItemStack item = (ItemStack)hi.next();
					try {
						BookMeta bm = (BookMeta)item.getItemMeta();
						if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
							Player owner = Bukkit.getPlayer(bm.getAuthor());
							player.getInventory().remove(item);
							this.invman.hold(owner, item);
							player.sendMessage(ChatColor.GREEN + "Your Autograph book was located. If you would like a new one, please get rid of this one.");
							return true;
						}
					}
					catch (ClassCastException localClassCastException1) {}catch (NullPointerException localNullPointerException1) {}
				}
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getInventory().contains(Material.WRITTEN_BOOK)) {
					ListIterator<ItemStack> inv = p.getInventory().iterator();
					while (inv.hasNext()) {
						ItemStack item = (ItemStack)inv.next();
						try {
							BookMeta bm = (BookMeta)item.getItemMeta();
							if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book")))
							{
								p.getInventory().remove(item);
								this.invman.hold(player, item);
								player.sendMessage(ChatColor.GREEN + "Your Autograph book was located. If you would like a new one, please get rid of this one.");
								return true;
							}
						}
						catch (ClassCastException localClassCastException2) {}catch (NullPointerException localNullPointerException2) {}
					}
					ListIterator<ItemStack> ender = p.getEnderChest().iterator();
					while (ender.hasNext()) {
						ItemStack item = (ItemStack)ender.next();
						try {
							BookMeta bm = (BookMeta)item.getItemMeta();
							if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
								p.getEnderChest().remove(item);
								this.invman.hold(player, item);
								player.sendMessage(ChatColor.GREEN + "Your Autograph book was located. If you would like a new one, please get rid of this one.");
								return true;
							}
						}
						catch (ClassCastException localClassCastException3) {}catch (NullPointerException localNullPointerException3) {}
					}
				}
			}
			this.newbook = new ItemStack(Material.WRITTEN_BOOK);
			this.bookmeta = ((BookMeta)this.newbook.getItemMeta());
			this.bookmeta.setAuthor(player.getName());
			this.bookmeta.setTitle("Autograph Book");
			this.bookmeta.setDisplayName(ChatColor.DARK_AQUA + "Autograph Book");
			List<String> pages = new ArrayList<String>();
			pages.add("This is the autograph book of " + player.getDisplayName() + "§0. \nTo learn more about autograph books, please type: \n /signing");
			this.bookmeta.setPages(pages);
			this.newbook.setItemMeta(this.bookmeta);
			this.invman.hold(player, this.newbook);
			player.getInventory().getItemInHand().setAmount(1);
		}
		if (cmd.getName().equalsIgnoreCase("sign")) {
			if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
				if (args.length < 1) {
					player.sendMessage(ChatColor.GREEN + "You forgot to add message! Please type /signing for help");
					return false;
				}
				boolean multiple = true;
				String storage = "";
				for (String argument : args) {
					storage += argument + " ";
				}
				if (storage.trim().matches("^.*[^;]$")) multiple = false;
				if (multiple) {
					int index = 0;
					storage = storage.replaceAll("&", "§");
					if(player.hasMetadata("sign-index")) index = player.getMetadata("sign-index").get(0).asInt();
					player.setMetadata("sign-storage-" + index, new FixedMetadataValue(pl, storage));
					index = index+1;
					player.setMetadata("sign-index", new FixedMetadataValue(pl, index));
					player.sendMessage(ChatColor.GREEN + "You added a line to be signed to the book. Total: " + index);
					return true;
				} else {
					if (player.hasMetadata("sign-index")) {
						int index = player.getMetadata("sign-index").get(0).asInt();
						String output = "";
						for (int i = 0; i < index; i++) {
							try {
							  output += player.getMetadata("sign-storage-" + i).get(0).asString().replace(";", "") + " ";
							} catch (Exception ignore) {
								
							}
						}
						this.bookmeta = ((BookMeta)player.getItemInHand().getItemMeta());
						if ((this.bookmeta.hasTitle()) && (this.bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
							this.bookmeta.getPages();
							this.buffer = "";
							for (String str : args) {
								this.buffer = (this.buffer + str.replaceAll("&", "§") + " ");
							}
							this.bookmeta.addPage(new String[] { output + this.buffer + "\n§0" + "-" + player.getDisplayName() });
							player.getItemInHand().setItemMeta(this.bookmeta);
							if (player.hasMetadata("sign-index")) {
								int indx = player.getMetadata("sign-index").get(0).asInt();
								for (int i = 0; i < indx; i++) {
									player.removeMetadata("sign-storage-" + indx, pl);
								}
								player.removeMetadata("sign-index", pl);
							}
							return true;
						}
					} else {
						this.bookmeta = ((BookMeta)player.getItemInHand().getItemMeta());
						if ((this.bookmeta.hasTitle()) && (this.bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
							this.bookmeta.getPages();
							this.buffer = "";
							for (String str : args) {
								this.buffer = (this.buffer + str.replaceAll("&", "§") + " ");
							}
							this.bookmeta.addPage(new String[] { this.buffer + "\n§0" + "-" + player.getDisplayName() });
							player.getItemInHand().setItemMeta(this.bookmeta);
							return true;
						}
					}
				}
			}
			player.sendMessage(ChatColor.RED + "You need to have an autograph book in hand!");
			player.sendMessage(ChatColor.AQUA + "If you don't have one yet, do /newbook!");
			player.sendMessage(ChatColor.AQUA + "If someone else has your book, please do /regain");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("csign")) {
			if (!player.hasPermission("xblaze.charactersign")) {
				player.sendMessage(ChatColor.RED + "Only Characters may use this command!");
				player.sendMessage(ChatColor.AQUA + "If you wish to become a character, please visit the forums:");
				player.sendMessage(ChatColor.AQUA + "http://mcmagic.us/topic/character-claiming-open-from-216-223/");
				return true;
			}
			if (args.length < 2) {
				player.sendMessage(ChatColor.RED + "You forgot to add message! Please type /signing for help");
				return false;
			}
			if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
				this.bookmeta = ((BookMeta)player.getItemInHand().getItemMeta());
				if ((this.bookmeta.hasTitle()) && (this.bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))){
					this.bookmeta.getPages();
					this.buffer = "";
					String character = args[0];
					args[0] = "";
					player.sendMessage(ChatColor.AQUA + "You are signing this book as: " + character);
					for (String str : args)
					{
						this.buffer = (this.buffer + str.replaceAll("&", "§") + " ");
					}
					this.bookmeta.addPage(new String[] { this.buffer + "\n§0" + "-" + character.replaceAll("&", "§") });
					player.getItemInHand().setItemMeta(this.bookmeta);
					return true;
				}
			}
			player.sendMessage(ChatColor.RED + " You need an autograph book! Please type /signing ");
			player.sendMessage(ChatColor.AQUA + "If you don't have one yet, do /newbook!");
			player.sendMessage(ChatColor.AQUA + "If someone else has your book, please do /regain");
			return true;
		}
		ItemStack thebook;
		if (cmd.getName().equalsIgnoreCase("rmpage")) {
			player.sendMessage(ChatColor.RED + "This command is currently under development.");
			player.sendMessage(ChatColor.RED + "It will not work at this time, sorry!");
			if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
				this.bookmeta = ((BookMeta)player.getItemInHand().getItemMeta());
				if ((this.bookmeta.hasTitle()) && (this.bookmeta.getTitle().equalsIgnoreCase("Autograph Book"))) {
					if (this.bookmeta.getAuthor().equals(player.getName())) {
						if ((this.bookmeta.getPageCount() == 1) || (args[0].equalsIgnoreCase("1"))) {
							player.sendMessage(ChatColor.RED + "The cover page is not removeable.");
							return true;
						}
						List<String> scanner = this.bookmeta.getPages();
						List<String> printer = new ArrayList<String>();
						try {
							for (int i = 0; i < scanner.size(); i++) {
								if (i == Integer.getInteger(args[0])) {
									i++;
								} else {
									printer.add(scanner.get(i));
								}
							}
						} catch (NullPointerException err) { // This may be thrown if a non integer is given.
							player.sendMessage(ChatColor.RED + "I had some trouble making " + args[0] + " into a number. Book not changed!");
							return true;
						}
						this.bookmeta.setPages(printer);
						player.getItemInHand().setItemMeta(this.bookmeta);
						player.sendMessage(ChatColor.AQUA + "Modified your book. Take a look! :)");
						return true;
					}
					player.sendMessage(ChatColor.RED + "You are not allowed to remove signatures from other's books");
					String BookAuthor = this.bookmeta.getAuthor();

					thebook = player.getItemInHand();
					Bukkit.getOfflinePlayer(BookAuthor).getPlayer().getInventory().addItem(new ItemStack[] { thebook });
					player.getInventory().remove(thebook);
					Bukkit.getOfflinePlayer(BookAuthor).getPlayer().sendMessage(ChatColor.AQUA + "You have gotten your book back. Someone tried to make edits to it.");
				}
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("regain")) {
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
								p.sendMessage(ChatColor.AQUA + player.getName() + " has taken back their Autograph Book.");
								player.sendMessage(ChatColor.AQUA + "Autograph book retrieved from " + p.getName());
							}
						}
						catch (ClassCastException localClassCastException4) {}catch (NullPointerException localNullPointerException4) {}
					}
				}
				if (p.getEnderChest().contains(Material.WRITTEN_BOOK)) {
					ListIterator<ItemStack> hi = p.getEnderChest().iterator();
					while (hi.hasNext()) {
						ItemStack item = (ItemStack)hi.next();
						try {
							BookMeta bm = (BookMeta)item.getItemMeta();
							if ((bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
								p.getEnderChest().remove(item);
								player.getInventory().addItem(new ItemStack[] { item });
								p.sendMessage(ChatColor.AQUA + player.getName() + " has taken back their Autograph Book.");
								player.sendMessage(ChatColor.AQUA + "Autograph book retrieved from " + p.getName());
							}
						}
						catch (ClassCastException localClassCastException5) {}catch (NullPointerException localNullPointerException5) {}
					}
				}
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("return")) {
			if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
				ListIterator<ItemStack> hi = player.getInventory().iterator();
				while (hi.hasNext()) {
					ItemStack item = (ItemStack)hi.next();
					try {
						BookMeta bm = (BookMeta)item.getItemMeta();
						if ((!bm.getAuthor().equalsIgnoreCase(player.getName())) && (bm.getTitle().equalsIgnoreCase("Autograph Book"))) {
							Player owner = Bukkit.getPlayer(bm.getAuthor());
							player.getInventory().remove(item);
							owner.getInventory().addItem(new ItemStack[] { item });
						}
					}
					catch (ClassCastException localClassCastException6) {}catch (NullPointerException localNullPointerException6) {}
				}
				player.sendMessage(ChatColor.AQUA + "You have returned all the books you have to their rightful owners.");
				return true;
			}
			player.sendMessage(ChatColor.AQUA + "You do not have anyone else's autograph book.");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("colors")) {
			if ((Bukkit.getServer().getPluginManager().isPluginEnabled("xBlazeBand")) && (player.getInventory().getHeldItemSlot() == 8)) {
				player.sendMessage(ChatColor.RED + "You have a magic band in this slot. You are not permitted to remove it!");
				return true;
			}
			this.newbook = new ItemStack(Material.WRITTEN_BOOK);
			this.bookmeta = ((BookMeta)this.newbook.getItemMeta());
			this.bookmeta.setAuthor(player.getName());
			this.bookmeta.setTitle("Color Reference");
			this.bookmeta.setDisplayName(ChatColor.RED + "C" + ChatColor.GOLD + "o" + ChatColor.YELLOW + "l" + ChatColor.GREEN + "o" + ChatColor.BLUE + "r" + ChatColor.DARK_PURPLE + " Book");
			List<String> pages = new ArrayList<String>();
			pages.add("\n\n\n\n§1&1 §2&2 §3&3 §4&4 §5&5 §6&6 §7&7 §8&8 §9&9 §0&0 §a&a §b&b §c&c §d&d §e&e §0§l&l§r §0§m&m§r §0§n&n§r §0§o&o§r §r\n\nTo get rid of this book, please just drop it!");
			this.bookmeta.setPages(pages);
			this.newbook.setItemMeta(this.bookmeta);
			this.invman.hold(player, this.newbook);
			player.getInventory().getItemInHand().setAmount(1);
		}
		if (cmd.getName().equalsIgnoreCase("helpsign")) {
			player.sendMessage(ChatColor.BLUE + "We are in the progress of creating a video.");
			player.sendMessage(ChatColor.BLUE + "We thank you for your paitents.");
//			player.sendMessage(ChatColor.BLUE + "This video may help you out!");
//			player.sendMessage(ChatColor.BLUE + "https://www.youtube.com/watch?v=XTTELd-rsUw");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("signing")) {
			player.sendMessage(ChatColor.GOLD + "=============== " + ChatColor.YELLOW + "xBlazeSign" + ChatColor.GOLD + " ===============");
			player.sendMessage(ChatColor.AQUA + "Commands in this menu are CLICKABLE! Try it out! ;)");
			sendHelp(player, "/helpsign", null, "View a video tutorial about signing!");
			sendHelp(player, "/newbook", null, "Get new Autograph Book.");
			sendHelp(player, "/sign", "<message>", "Sign Autograph Book.");
			if (player.hasPermission("xblaze.character")) {
				sendHelp(player, "/csign", "<name> <message>", "Sign book as Character!");
			}
			sendHelp(player, "/rmpage", "<page#>", "Remove a page.");
			sendHelp(player, "/regain", null, "Get your autograph books back.");
			sendHelp(player, "/return", null, "Return autograph books to owner.");
			sendHelp(player, "/colors", null, "Learn more about color codes!");
			return true;
		}
		return false;
	}
}