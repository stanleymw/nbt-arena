package wang.stan.nbtArena.client;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTArena {
    private static final Logger log = LoggerFactory.getLogger(NBTArena.class);
    public static List<ItemStack> ARENA_ITEMS = new ArrayList<>();
    public static final RegistryKey<ItemGroup> ARENA_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of("nbtarena", "items")
    );
    public static final ItemGroup ARENA_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.TRAPPED_CHEST))
            .displayName(Text.literal("NBT Arena"))
            .build();

    public static void saveToFile(String name) {
        var cfgPath = FabricLoader.getInstance().getConfigDir();
        var f = cfgPath.resolve(name);

        try {
            NbtIo.writeCompressed(serialize(), f);

            log.info("NBT Arena Data saved to {}", name);
        } catch (java.io.IOException e) {
            log.error("Unable to serialize nbt arena file!", e);
        }

    }

    public static void loadFromFile(String name) {
        var cfgPath = FabricLoader.getInstance().getConfigDir();
        var f = cfgPath.resolve(name);

        NbtCompound res = null;
        try {
            res = NbtIo.readCompressed(f, NbtSizeTracker.ofUnlimitedBytes());

            log.info("NBT Arena Data loaded from {}", name);

            deserialize(res);
        } catch (java.io.IOException e) {
            log.error("Unable to LOAD nbt arena file!", e);
        }
    }

    public static NbtCompound serialize() {
        NbtCompound root = new NbtCompound();
        var serializer = ItemStack.CODEC.listOf();
        root.put("ARENA_ITEMS", serializer.encodeStart(NbtOps.INSTANCE, ARENA_ITEMS).getOrThrow());
        return root;
    }

    public static void deserialize(NbtCompound ser) {
        NbtElement elem = ser.get("ARENA_ITEMS");
        var serializer = ItemStack.CODEC.listOf();
        ARENA_ITEMS = new ArrayList<>(serializer.decode(NbtOps.INSTANCE, elem).getOrThrow().getFirst());
    }

    public static boolean addItemSafe(ItemStack it) {
        if (it.isEmpty()) {
            return false;
        }
        ARENA_ITEMS.add(it.copyWithCount(1));
        return true;
    }

    public static void addAll(Collection<ItemStack> items) {
        for (ItemStack i : items) {
            addItemSafe(i);
        }
    }

    public static void init() {
        loadFromFile("arena.dat");
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
