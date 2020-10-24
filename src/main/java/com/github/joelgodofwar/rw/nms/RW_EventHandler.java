package com.github.joelgodofwar.rw.nms;

import org.bukkit.event.Listener;

import com.github.joelgodofwar.rw.RotationalWrench;

public class RW_EventHandler {
	/**
    1.8		1_8_R1		1.8.3	1_8_R2
	1.8.8 	1_8_R3
	1.9		1_9_R1		1.9.4	1_9_R2	
	1.10	1_10_R1
	1.11	1_11_R1
	1.12	1_12_R1
	1.13	1_13_R1		1.13.1	1_13_R2
	1.14	1_14_R1
	1.15	1_15_R1
	1.16.1	1_16_R1		1.16.2	1_16_R2
	 * @return 
	*/
	public static Listener getHandler(String string, RotationalWrench plugin ){
		if(string == "1_16_R2" || string == "1_16_R1"){
			return new RW_1_16_R2(plugin);
		}else if(string == "1_15_R1" || string == "1_14_R1" || string == "1_13_R2" || string == "1_13_R1" ){
			return new RW_1_14_R1(plugin);
		}
		return plugin;
	}
}
