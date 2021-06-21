package com.jamesr.namehistory;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = NameHistory.MODID, version = NameHistory.VERSION)
public class NameHistory {

	public static final String MODID = "namehistory";
	public static final String VERSION = "1.0.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(this);
    	ClientCommandHandler.instance.registerCommand(new NameHistoryCommand());
        return;
    }
}
