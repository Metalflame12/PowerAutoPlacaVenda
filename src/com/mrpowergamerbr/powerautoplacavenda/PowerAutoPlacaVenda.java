package com.mrpowergamerbr.powerautoplacavenda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mrpowergamerbr.powerautoplacavenda.commands.ComprarPlacaCommand;
import com.mrpowergamerbr.powerautoplacavenda.listeners.SignListener;
import com.mrpowergamerbr.powerautoplacavenda.utils.AsrielConfig;
import com.mrpowergamerbr.powerautoplacavenda.utils.PowerCommandUtils;
import com.mrpowergamerbr.powerautoplacavenda.utils.PowerConfig;
import com.mrpowergamerbr.powerautoplacavenda.utils.TemmieUpdater;

import net.milkbowl.vault.economy.Economy;

public class PowerAutoPlacaVenda extends JavaPlugin {
	public ArrayList<Location> signs = new ArrayList<Location>();

	public AsrielConfig asriel;

	public PowerConfig dreemurr;

	public static final String pluginName = "PowerAutoPlacaVenda";
	public static final String pluginVersion = "v1.0.1";

	public static Economy econ = null;

	@Override
	public void onEnable() {
		/*
		 * PowerAutoPlacaVenda precisa do Vault para funcionar
		 */
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			Bukkit.getPluginManager().disablePlugin(this);
			Bukkit.getLogger().warning("[PowerAutoPlacaVenda] Vault não encontrado!");
			return;
		}
		/*
		 * Configurar a Economia do Vault
		 */
		setupEconomy();

		saveDefaultConfig();
		/*
		 * Ativar o AsrielConfig
		 */
		asriel = new AsrielConfig(this);
		/*
		 * Criar a config "dreemurr.yml", aonde nós iremos salvar as localizações das placas
		 */
		dreemurr = new PowerConfig(this, "dreemurr.yml");
		/*
		 * Criar comando de reload genérico
		 */
		new PowerCommandUtils(this, "powerautoplacavenda");
		/*
		 * Ativar comando para poder comprar as placas
		 */
		new ComprarPlacaCommand(this);
		/*
		 * Começar a verificação de placas
		 */
		runSigns();
		/*
		 * Ativar o Listener
		 */
		new SignListener(this);

		load();

		new BukkitRunnable() {
			public void run() {
				Thread t = new Thread(new Runnable() {
					public void run() {
						save();
					}
				});
				t.start();
			}
		}.runTaskTimer(this, 54000L, 54000L);

		if ((boolean) asriel.get("TemmieUpdater.VerificarUpdates")) {
			new TemmieUpdater(this);
		}
	}

	@Override
	public void onDisable() {
		save();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}


	public void runSigns() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					ArrayList<Location> toRemove = new ArrayList<Location>();

					for (Location l : signs) {
						if (l.getBlock().getType() != Material.WALL_SIGN) {
							toRemove.add(l);
							continue;
						}

						Sign s = (Sign) l.getBlock().getState().getData();

						org.bukkit.block.Sign sb = (org.bukkit.block.Sign) l.getBlock().getState();

						BlockFace directionFacing = s.getFacing();

						if (l.getBlock().getRelative(directionFacing.getOppositeFace()).getType() != Material.CHEST && l.getBlock().getRelative(directionFacing.getOppositeFace()).getType() != Material.TRAPPED_CHEST) {
							toRemove.add(l);
							continue;
						}

						if (!sb.getLine(0).equals(asriel.getChanged("NomeDaPlaca"))) {
							/*
							 * Se o nome da placa não é o que está na config, então não é uma placa de venda.
							 */
							toRemove.add(l);
							continue;
						}
						Chest c = (Chest) l.getBlock().getRelative(directionFacing.getOppositeFace()).getState();

						calculateSellingItemsFor(c, sb.getLine(1));
					}

					for (Location l : toRemove) {
						signs.remove(l);
					}
				} catch (Exception e) {

				}
				new BukkitRunnable() {
					public void run() {
						runSigns();
					}
				}.runTaskLater(getMe(), (int) asriel.get("TempoDeVerificacao") * 20);
			}
		});
		t.start();
	}

	public void calculateSellingItemsFor(Chest c, String player) {
		for (ItemStack is : c.getInventory()) {
			if (is != null) {
				/*
				 * http://i.imgur.com/DVYLNRS.png
				 */
				HashMap<Material, Double> hollaHollaGetDolla = new HashMap<Material, Double>();
				ArrayList<String> fromConfig = (ArrayList<String>) asriel.get("ItensVendidos");
				for (String undyne : fromConfig) {
					hollaHollaGetDolla.put(Material.valueOf(undyne.split(": ")[0]), Double.parseDouble(undyne.split(": ")[1]));
				}
				for (Entry<Material, Double> dreemurr : hollaHollaGetDolla.entrySet()) {
					if (is.getType() == dreemurr.getKey()) {
						/*
						 * Tá tranquilo tá favorável
						 */
						if (is.getAmount() >= 2) {
							is.setAmount(is.getAmount() - 1);
						} else {
							/*
							 * Your Best Nightmare
							 */
							c.getInventory().removeItem(is);
						}
						econ.depositPlayer(player, dreemurr.getValue());
						break;
					}
				}
			}
		}
	}

	public void save() {
		ArrayList<String> serializedLocations = new ArrayList<String>();

		for (Location l : signs) {
			serializedLocations.add(serializeLocation(l));
		}

		dreemurr.getPlayerData().set("Placas", null);

		dreemurr.getPlayerData().set("Placas", serializedLocations);

		dreemurr.savePlayerData();
	}

	public void load() {
		if (dreemurr.getPlayerData().contains("Placas")) {
			ArrayList<String> wow = (ArrayList<String>) dreemurr.getPlayerData().getStringList("Placas");

			ArrayList<Location> signsFromTheConfig = new ArrayList<Location>();

			for (String s : wow) {
				signsFromTheConfig.add(deserializeLocation(s));
			}

			signs = signsFromTheConfig;
		}
	}

	public PowerAutoPlacaVenda getMe() {
		return this;
	}

	public static String serializeLocation(Location l) {
		String s = "";
		s += "@w;" + l.getWorld().getName();
		s += ":@x;" + l.getBlockX();
		s += ":@y;" + l.getBlockY();
		s += ":@z;" + l.getBlockZ();
		s += ":@p;" + l.getPitch();
		s += ":@ya;" + l.getYaw();
		return s;
	}

	public static Location deserializeLocation(String s) {
		try {
			Location l = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
			String[] att = s.split(":");
			for (String attribute : att) {
				String[] split = attribute.split(";");
				if (split[0].equalsIgnoreCase("@w"))
					l.setWorld(Bukkit.getWorld(split[1]));
				if (split[0].equalsIgnoreCase("@x"))
					l.setX(Double.parseDouble(split[1]));
				if (split[0].equalsIgnoreCase("@y"))
					l.setY(Double.parseDouble(split[1]));
				if (split[0].equalsIgnoreCase("@z"))
					l.setZ(Double.parseDouble(split[1]));
				if (split[0].equalsIgnoreCase("@p"))
					l.setPitch(Float.parseFloat(split[1]));
				if (split[0].equalsIgnoreCase("@ya"))
					l.setYaw(Float.parseFloat(split[1]));
			}
			return l;
		} catch (Exception e) {
			return null;
		}
	}
}

