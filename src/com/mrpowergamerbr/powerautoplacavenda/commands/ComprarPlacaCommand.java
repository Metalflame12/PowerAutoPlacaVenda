package com.mrpowergamerbr.powerautoplacavenda.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mrpowergamerbr.powerautoplacavenda.PowerAutoPlacaVenda;

public class ComprarPlacaCommand implements CommandExecutor {
	PowerAutoPlacaVenda m;

	public ComprarPlacaCommand(PowerAutoPlacaVenda m) {
		Bukkit.getPluginCommand("comprarplaca").setExecutor(this);
		this.m = m;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if (arg1.getName().equalsIgnoreCase("comprarplaca")) {
			if (arg0 instanceof Player) {
				if (arg0.hasPermission("PowerAutoPlacaVenda.ComprarPlaca")) {
					Player p = (Player) arg0;
					int grana = (int) m.asriel.get("CustoDaPlaca");

					if (PowerAutoPlacaVenda.econ.has(p, grana)) {
						arg0.sendMessage(m.asriel.getChanged("Mensagens.Prefixo") + m.asriel.getChanged("Mensagens.PlacaComprada"));
						PowerAutoPlacaVenda.econ.withdrawPlayer(p, grana);
						
						ItemStack is = new ItemStack(Material.SIGN);
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§6§lPlaca de Venda Automática!"));
						is.setItemMeta(im);
						p.getInventory().addItem(is);
						return true;
					} else {
						arg0.sendMessage(m.asriel.getChanged("Mensagens.Prefixo") + m.asriel.getChanged("Mensagens.DinheiroInsuficiente"));
						return true;
					}
				} else {
					arg0.sendMessage(m.asriel.getChanged("Mensagens.Prefixo") + m.asriel.getChanged("Mensagens.SemPermissao"));
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

}
