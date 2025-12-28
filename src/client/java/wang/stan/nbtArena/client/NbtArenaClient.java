package wang.stan.nbtArena.client;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class NbtArenaClient implements ClientModInitializer {

    public static int addHandItem(CommandContext<FabricClientCommandSource> context) {
        ItemStack to_add = context.getSource().getPlayer().getMainHandStack();
        context.getSource().sendFeedback(
                Text.literal("Adding ").append(to_add.toHoverableText())
        );
        NBTArena.addItemSafe(to_add);

        var registryManager = context.getSource().getWorld().getRegistryManager();
        var networkHandler = context.getSource().getPlayer().networkHandler;
        NBTArena.saveToFile("arena.dat", registryManager);
        NBTArena.refreshCreativeTabs(registryManager, networkHandler);
        return 1;
    }

    public static int addHotbar(CommandContext<FabricClientCommandSource> context) {
        PlayerInventory inventory = context.getSource().getPlayer().getInventory();
        ArrayList<ItemStack> hotbarItems = new ArrayList<>(9);

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                hotbarItems.add(stack);
            }
        }

        for (ItemStack item : hotbarItems) {
            context.getSource().sendFeedback(
                    Text.literal("Adding ").append(item.toHoverableText())
            );
        }

        NBTArena.addAll(hotbarItems);

        var registryManager = context.getSource().getWorld().getRegistryManager();
        var networkHandler = context.getSource().getPlayer().networkHandler;
        NBTArena.saveToFile("arena.dat", registryManager);
        NBTArena.refreshCreativeTabs(registryManager, networkHandler);
        return 1;
    }


    public static int addInventory(CommandContext<FabricClientCommandSource> context) {
        PlayerInventory inventory = context.getSource().getPlayer().getInventory();
        NBTArena.addAll(inventory.getMainStacks());

        context.getSource().sendFeedback(
                Text.literal("Added inventory!")
        );

        var registryManager = context.getSource().getWorld().getRegistryManager();
        var networkHandler = context.getSource().getPlayer().networkHandler;
        NBTArena.saveToFile("arena.dat", registryManager);
        NBTArena.refreshCreativeTabs(registryManager, networkHandler);
        return 1;
    }

    @Override
    public void onInitializeClient() {
        NBTArena.init();

        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> {
            commandDispatcher.register(
                    ClientCommandManager.literal("arena")
                    .then(ClientCommandManager.literal("hand").executes(NbtArenaClient::addHandItem))
                    .then(ClientCommandManager.literal("hotbar").executes(NbtArenaClient::addHotbar))
                    .then(ClientCommandManager.literal("inventory").executes(NbtArenaClient::addInventory))
            );
        }));
    }
}
