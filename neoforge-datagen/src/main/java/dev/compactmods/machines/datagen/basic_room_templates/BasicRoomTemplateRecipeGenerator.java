package dev.compactmods.machines.datagen.basic_room_templates;

import dev.compactmods.machines.api.CompactMachines;
import dev.compactmods.machines.api.room.template.RoomTemplate;
import dev.compactmods.machines.datagen.base.RecipeGenerator;
import dev.compactmods.machines.machine.Machines;
import dev.compactmods.machines.room.Rooms;
import dev.compactmods.machines.shrinking.Shrinking;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class BasicRoomTemplateRecipeGenerator extends RecipeGenerator {

    public BasicRoomTemplateRecipeGenerator(PackOutput packOut, CompletableFuture<HolderLookup.Provider> holders) {
        super(packOut, holders);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider provider) {
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("tiny"), Tags.Items.INGOTS_COPPER);
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("small"), Tags.Items.INGOTS_IRON);
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("normal"), Tags.Items.INGOTS_GOLD);
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("large"), Tags.Items.GEMS_DIAMOND);
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("giant"), Tags.Items.OBSIDIANS);
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("colossal"), Tags.Items.INGOTS_NETHERITE);

        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("soaryn"), Tags.Items.NETHER_STARS);
        addMachineRecipe(recipeOutput, provider, CompactMachines.modRL("farming"), Items.DIAMOND_HOE);
    }

    private void addMachineRecipe(RecipeOutput consumer, HolderLookup.Provider provider, ResourceLocation id, TagKey<Item> catalyst) {
        final var templateRef = provider.lookupOrThrow(RoomTemplate.REGISTRY_KEY)
                .getOrThrow(ResourceKey.create(RoomTemplate.REGISTRY_KEY, id));

        machineRecipeBuilder(consumer, templateRef, builder -> builder.define('P', catalyst));
    }

    private void addMachineRecipe(RecipeOutput consumer, HolderLookup.Provider provider, ResourceLocation id, ItemLike catalyst) {
        final var templateRef = provider.lookupOrThrow(RoomTemplate.REGISTRY_KEY)
                .getOrThrow(ResourceKey.create(RoomTemplate.REGISTRY_KEY, id));

        machineRecipeBuilder(consumer, templateRef, builder -> builder.define('P', catalyst));
    }

    protected void machineRecipeBuilder(RecipeOutput consumer, Holder.Reference<RoomTemplate> templateRef, UnaryOperator<ShapedRecipeBuilder> configure) {
        final var builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Machines.Items.forNewRoom(templateRef))
                .pattern("WWW")
                .pattern("EPS")
                .pattern("WWW")
                .define('W', Rooms.Items.BREAKABLE_WALL)
                .define('E', Shrinking.ENLARGING_MODULE)
                .define('S', Shrinking.SHRINKING_MODULE);

        configure.apply(builder);

        builder.unlockedBy("has_recipe", has(Rooms.Items.BREAKABLE_WALL));

        final var recipeId = CompactMachines.modRL("new_machine_" + templateRef.key().location().getPath());
        builder.save(consumer, recipeId);
    }
}
