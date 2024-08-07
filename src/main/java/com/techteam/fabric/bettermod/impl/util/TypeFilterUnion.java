package com.techteam.fabric.bettermod.impl.util;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.util.TypeFilter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @param <B> The base class shared between possible union inputs. You should make this as specific as possible.
 * @param <T> A common class shared between possible union outputs. You should make this as specific as possible.
 */
public final class TypeFilterUnion<B, T extends B> implements TypeFilter<B, T> {
	private final TypeFilter<B, ? extends T>[] filters;
	private final Class<B> klass;
	private final int size;

	@Contract(pure = true)
	private TypeFilterUnion(Class<B> klass, TypeFilter<B, ? extends T> @NotNull [] filters) {
		this.filters = filters;
		this.klass = klass;
		this.size = filters.length;
	}

	@Override
	public @Nullable T downcast(final B ent) {
		for (int i = 0; i < size; i++) {
			T cur = filters[i].downcast(ent);
			if (cur != null) {
				return cur;
			}
		}
		return null;
	}

	@Contract(pure = true)
	@Override
	public Class<? extends B> getBaseClass() {
		return klass;
	}

	public static final class Builder<B, T extends B> {
		private final ReferenceArrayList<TypeFilter<B, ? extends T>> filters = new ReferenceArrayList<>();
		private final Class<B> klass;

		private Builder(final Class<B> klass) {
			this.klass = klass;
		}

		@Contract(value = "_ -> new",
		          pure = true)
		public static <B, T extends B> @NotNull Builder<B, T> create(final Class<B> klass) {
			return new Builder<>(klass);
		}

		@Contract(value = "_ -> this")
		public <C extends T> Builder<B, T> add(final TypeFilter<B, C> filter) {
			filters.add(filter);
			return this;
		}

		@Contract(" -> new")
		public @NotNull TypeFilterUnion<B, T> build() {
			return new TypeFilterUnion<>(klass, filters.elements());
		}
	}
}
