package wang.stan.nbtArena.client;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.mixin.itemgroup.CreativeModeTabAccessor;
import net.fabricmc.fabric.mixin.itemgroup.CreativeModeTabsMixin;
import net.fabricmc.fabric.mixin.itemgroup.client.CreativeModeInventoryScreenMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class NBTArena {
    public static final ArrayList<ItemStack> ARENA_ITEMS = new ArrayList<ItemStack>();
    public static final RegistryKey<ItemGroup> ARENA_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of("nbtarena", "items")
    );
    public static final ItemGroup ARENA_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.TRAPPED_CHEST))
            .displayName(Text.literal("NBT Arena"))
            .build();

    public static void addItem(ItemStack it) {
        ARENA_ITEMS.add(it);
    }

    public static void addItems(Collection<ItemStack> items) {
        ARENA_ITEMS.addAll(items);
    }

    public static void register() {
        ARENA_ITEMS.add(new ItemStack(Items.DIAMOND_SWORD));
        Registry.register(Registries.ITEM_GROUP, ARENA_KEY, ARENA_ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(NBTArena.ARENA_KEY).register((entries -> {
            entries.addAll(ARENA_ITEMS);
        }));

    }

    public static void refreshCreativeTabs() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.networkHandler != null) {
            // This triggers the internal logic that calls modifyEntriesEvent again
            assert client.world != null;
            ItemGroups.updateDisplayContext(
                    client.player.networkHandler.getEnabledFeatures(),
                    true, // Searchable
                    client.world.getRegistryManager()
            );
        }
    }
}
