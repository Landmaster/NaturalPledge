package shadowfox.botanicaladdons.api;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import shadowfox.botanicaladdons.api.lib.LibMisc;
import shadowfox.botanicaladdons.api.sapling.IIridescentSaplingVariant;
import shadowfox.botanicaladdons.api.sapling.SaplingGrowthRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * @author WireSegal
 *         Created at 3:11 PM on 5/14/16.
 */
public final class SaplingVariantRegistry {
    @Nonnull
    private static final HashBiMap<String, IIridescentSaplingVariant> variantRegistry = HashBiMap.create();

    @Nonnull
    private static final ArrayList<SaplingGrowthRecipe> fakeRecipeRegistry = Lists.newArrayList();

    @Nonnull
    public static HashBiMap<String, IIridescentSaplingVariant> getVariantRegistry() {
        return variantRegistry;
    }

    @Nonnull
    public static ArrayList<SaplingGrowthRecipe> getSaplingRecipeRegistry() {
        return fakeRecipeRegistry;
    }

    @Nullable
    public static IIridescentSaplingVariant registerVariant(@Nonnull String name, @Nonnull IIridescentSaplingVariant variant) {
        return registerVariant(name, variant, false);
    }

    @Nullable
    public static IIridescentSaplingVariant registerVariant(@Nonnull String name, @Nonnull IIridescentSaplingVariant variant, boolean force) {
        String modId = Loader.instance().activeModContainer().getModId();
        String transformedName = name;
        if (!modId.equals(LibMisc.MOD_ID))
            transformedName = modId + ":" + name;

        if (variantRegistry.containsKey(transformedName) && !force)
            return null;
        variantRegistry.put(transformedName, variant);
        return variant;
    }

    @Nullable
    public static SaplingGrowthRecipe registerRecipe(@Nonnull ItemStack sapling, @Nullable ItemStack soil, @Nonnull ItemStack wood, @Nonnull ItemStack leaves) {
        return registerRecipe(new SaplingGrowthRecipe(sapling, soil, wood, leaves));
    }

    @Nullable
    public static SaplingGrowthRecipe registerRecipe(SaplingGrowthRecipe recipe) {
        fakeRecipeRegistry.add(recipe);
        return recipe;
    }

    @Nullable
    public static IIridescentSaplingVariant getVariant(@Nonnull IBlockState soil) {
        for (IIridescentSaplingVariant variant : variantRegistry.values())
            if (variant.matchesSoil(soil))
                return variant;
        return null;
    }
}
