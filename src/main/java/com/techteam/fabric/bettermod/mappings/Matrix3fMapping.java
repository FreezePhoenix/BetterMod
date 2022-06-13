package com.techteam.fabric.bettermod.mappings;

import com.techteam.fabric.bettermod.util.Mapping;
import net.minecraft.util.math.Matrix3f;
import sun.misc.Unsafe;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ├──────────────────────────────────────────────0x38─────────────────────────────────────────────┤ *
 * ┏━━━━━━━0xC━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━0x2C━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓ *
 * ┃  object header  ┃                                      MEMBERS                                ┃ *
 * ┃                 ┣━0x4━┳━━━━0x8━━━━┳━━━━0x8━━━━┳━━━━0x8━━━━┳━━━━0x8━━━━┳━━━━0x8━━━━┳━━━━0x8━━━━┫ *
 * ┃                 ┃ PAD ┃   f32x2   ┃   f32x2   ┃   f32x2   ┃   f32x2   ┃   f32x2   ┃   f32x2   ┃ *
 * ┃                 ┃     ┠─0x4─┬─0x4─╂─0x4─┬─0x4─╂─0x4─┬─0x4─╂─0x4─┬─0x4─╂─0x4─┬─0x4─╂─0x4─┬─0x4─┨ *
 * ┃                 ┃     ┃ A00 │ A01 ┃ A02 │ PAD ┃ A10 │ A11 ┃ A12 │ PAD ┃ A20 │ A21 ┃ A22 │ PAD ┃ *
 * ┗━━━━━━━━━━━━━━━━━┻━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┻━━━━━┷━━━━━┛ *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
@Mapping(Matrix3f.class)
public final class Matrix3fMapping {
	private Matrix3fMapping() {
	}
}
