package com.techteam.fabric.bettermod.mappings;

import com.techteam.fabric.bettermod.util.Mapping;
import net.minecraft.util.math.Vec3f;

/* * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ├─────────────────────╴0x20╶────────────────────┤ *
 * ┏━━━━━━╸0xC╺━━━━━━┳━━━━━━━━━━━━╸0x14╺━━━━━━━━━━━┓ *
 * ┃                 ┃           MEMBERS           ┃ *
 * ┃                 ┣╸0x4╺┳━━━╸0x8╺━━━┳━━━╸0x8╺━━━┫ *
 * ┃                 ┃     ┃   f32x2   ┃   f32x2   ┃ *
 * ┃                 ┃     ┠╴0x4╶┬╴0x4╶╂╴0x4╶┬╴0x4╶┨ *
 * ┃  OBJECT HEADER  ┃ PAD ┃  x  │  y  ┃  z  │ PAD ┃ *
 * ┗━━━━━━━━━━━━━━━━━┻━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┛ *
 * * * * * * * * * * * * * * * * * * * * * * * * * * */
@Mapping(Vec3f.class)
public class Vec3fMapping {
	static {
	}

}
