package net.xblaze.xBlazeSign;

import net.xblaze.xBlazeCore.api.nms.NmsManager;
import net.xblaze.xBlazeCore.api.types.ConsoleMessageType;
import net.xblaze.xBlazeCore.api.util.ConsoleManager;
import net.xblaze.xBlazeCore.api.util.InventoryManager;
import net.xblaze.xBlazeCore.api.util.ItemManager;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlazeSign extends JavaPlugin implements Listener {
	public ConsoleManager console = new ConsoleManager(this);
	public InventoryManager invman = new InventoryManager();
	public ItemManager itemman = new ItemManager();
	public NmsManager nmsman = new NmsManager();
	public CommandExecutor ce = new CommandManager(this);

	public void onEnable() {
		this.console.log(ConsoleMessageType.INFO, " has been enabled sucessfully!");
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("sign").setExecutor(this.ce);
		getCommand("csign").setExecutor(this.ce);
		getCommand("newbook").setExecutor(this.ce);
		getCommand("rmpage").setExecutor(this.ce);
		getCommand("return").setExecutor(this.ce);
		getCommand("regain").setExecutor(this.ce);
		getCommand("signing").setExecutor(this.ce);
		getCommand("colors").setExecutor(this.ce);
		getCommand("helpsign").setExecutor(this.ce);
		getCommand("toggledrop").setExecutor(this.ce);
		getServer().getPluginManager().registerEvents(new EventHandlers(this), this);
	}

	public void onDisable() {
		this.console.log(ConsoleMessageType.INFO, " has been disabled sucessfully!");
	}
}