package com.mrpowergamerbr.powerautoplacavenda.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mrpowergamerbr.powerautoplacavenda.PowerAutoPlacaVenda;

public class PowerCommandUtils implements CommandExecutor {
	String cmd;
	PowerAutoPlacaVenda psap;
	
	public PowerCommandUtils(PowerAutoPlacaVenda psap, String cmd) {
		psap.getServer().getPluginCommand(cmd).setExecutor(this);
		
		this.cmd = cmd;
		this.psap = psap;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (arg1.getName().equalsIgnoreCase(cmd)) {
			if (arg0.hasPermission(PowerAutoPlacaVenda.pluginName + ".ReloadConfig")) {
				psap.asriel.resetToReload();
				arg0.sendMessage("�aConfig Recarregada!");
				return true;
			} else {
				arg0.sendMessage("�e�l" + PowerAutoPlacaVenda.pluginName + " �6" + PowerAutoPlacaVenda.pluginVersion + " �8- �7Criado por �b�lMrPowerGamerBR");
				arg0.sendMessage("�7Website:�3 http://mrpowergamerbr.com/");
				arg0.sendMessage("�7SparklyPower:�3 http://sparklypower.net/");
				arg0.sendMessage("");
				arg0.sendMessage("�a\"Howdy!\" -Asriel Dreemurr");
				return true;
			}
		}
		return false;
	}
}
