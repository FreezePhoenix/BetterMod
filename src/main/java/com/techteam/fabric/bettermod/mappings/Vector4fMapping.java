package com.techteam.fabric.bettermod.mappings;

import com.techteam.fabric.bettermod.util.Mapping;
import net.minecraft.util.math.Vector4f;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ├──────────────────────0x20─────────────────────┤     ├──────────────────────0x20─────────────────────┤ *
 * ┏━━━━━━━0xC━━━━━━━┳━━━━━━━━━━━━━0x14━━━━━━━━━━━━┓     ┏━━━━━━━━━━0x10━━━━━━━━━┳━━━━━━━━━━0x10━━━━━━━━━┓ *
 * ┃                 ┃           MEMBERS           ┃     ┃                       ┃        MEMBERS        ┃ *
 * ┃                 ┣━0x4━┳━━━━0x8━━━━┳━━━━0x8━━━━┫     ┃                       ┣━━━━0x8━━━━┳━━━━0x8━━━━┫ *
 * ┃                 ┃     ┃   f32x2   ┃   f32x2   ┃     ┃                       ┃   f32x2   ┃   f32x2   ┃ *
 * ┃                 ┃     ┠─0x4─┬─0x4─╂─0x4─┬─0x4─┨     ┃                       ┠─0x4─┬─0x4─╂─0x4─┬─0x4─┨ *
 * ┃  OBJECT HEADER  ┃ PAD ┃ f32 │ f32 ┃ f32 │ f32 ┃     ┃     OBJECT HEADER     ┃ f32 │ f32 ┃ f32 │ f32 ┃ *
 * ┗━━━━━━━━━━━━━━━━━┻━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┛     ┗━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┛ *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
@Mapping(Vector4f.class)
public class Vector4fMapping {

}
