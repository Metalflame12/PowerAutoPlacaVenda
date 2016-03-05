package com.mrpowergamerbr.powerautoplacavenda.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mrpowergamerbr.powerautoplacavenda.PowerAutoPlacaVenda;

public class SignListener implements Listener {
	/*
	 * asriel
	 * http://i.imgur.com/NFhpkTx.jpg
	 */
	PowerAutoPlacaVenda m;
	
	public SignListener(PowerAutoPlacaVenda m) {
		Bukkit.getServer().getPluginManager().registerEvents(this, m);
		this.m = m;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSign(BlockPlaceEvent e) {
		if (e.getItemInHand() != null && e.getItemInHand().getType() == Material.SIGN && e.getItemInHand().getItemMeta().hasLore() && e.getItemInHand().getItemMeta().getLore().get(0).equals("§6§lPlaca de Venda Automática!")) {						
			if (!e.getPlayer().hasPermission("PowerAutoPlacaVenda.ColocarPlacas")) {
				e.getPlayer().sendMessage(m.asriel.getChanged("Mensagens.Prefixo") + m.asriel.getChanged("Mensagens.SemPermissao"));
				return;
			}
			Sign s = (Sign) e.getBlockPlaced().getState();
			
			s.setLine(0, m.asriel.getChanged("NomeDaPlaca"));
			
			s.setLine(1, e.getPlayer().getName());
			
			s.update();
			
			e.getPlayer().sendMessage(m.asriel.getChanged("Mensagens.Prefixo") + m.asriel.getChanged("Mensagens.PlacaCriada"));
		
			m.signs.add(e.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignEdit(SignChangeEvent e) {
		if (m.signs.contains(e.getBlock().getLocation())) {
			e.setCancelled(true);
			return;
		}
		
		if (e.getLine(0).equals(m.asriel.getChanged("NomeDaPlaca"))) {
			e.setCancelled(true);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.SIGN));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST) {
			Sign s = (Sign) e.getBlock().getState();
			
			if (s.getLine(0).equals( m.asriel.getChanged("NomeDaPlaca"))) {
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
				
				ItemStack is = new ItemStack(Material.SIGN);
				ItemMeta im = is.getItemMeta();
				im.setLore(Arrays.asList("§6§lPlaca de Venda Automática!"));
				is.setItemMeta(im);
				
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), is);
			}
		}
	}
}
