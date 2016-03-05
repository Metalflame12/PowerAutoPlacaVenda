package com.mrpowergamerbr.powerautoplacavenda.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.mrpowergamerbr.powerautoplacavenda.PowerAutoPlacaVenda;

public class PowerConfig {
	PowerAutoPlacaVenda m = null;
	String configName;

	public PowerConfig(PowerAutoPlacaVenda m, String configName) {
		this.m = m;
		this.configName = configName;
		setupPlayerData();
	}

	public FileConfiguration userfile;
	private File userfiled;

	public Server getServer()
	{
		return Bukkit.getServer();
	}

	public FileConfiguration getConfig()
	{
		return getPlugin().getConfig();
	}

	public Plugin getPlugin()
	{
		return m;
	}

	public Logger getLogger()
	{
		return Bukkit.getLogger();
	}

	public void saveConfig()
	{
		getPlugin().saveConfig();
		return;
	}

	public void reloadConfig()
	{
		getPlugin().reloadConfig();
		return;
	}

	public File getDataFolder()
	{
		return getPlugin().getDataFolder();
	}

	public void updateConfig() {
		savePlayerData();
	}
	
	public void reloadMe()
	{
		reloadConfig();
	}

	public void setupPlayerData() {
		if (!m.getDataFolder().exists()) {
			m.getDataFolder().mkdir();
		}
		this.userfiled = new File(m.getDataFolder(), configName);
		if (!this.userfiled.exists()) {
			try {
				this.userfiled.createNewFile();
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage("§cNão foi possível criar o " + configName + "!");
			}
		}
		this.userfile = YamlConfiguration.loadConfiguration(this.userfiled);
	}

	public FileConfiguration getPlayerData()
	{
		return this.userfile;
	}

	public void savePlayerData()
	{
		try
		{
			this.userfile.save(this.userfiled);
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("§cNão foi possível salvar o " + configName + ".yml!");
		}
	}
}
