package wang.stan.nbtArena.client;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;

import java.awt.*;

public class NbtArenaClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(
            (commandDispatcher, commandRegistryAccess) -> {
                commandDispatcher.register(ClientCommandManager.literal("arena").executes(context -> {
                    FabricClientCommandSource source = context.getSource();
                    source.sendFeedback(Text.literal("Opening NBT Arena..."));

                    PlayerInventory playerInv = source.getPlayer().getInventory();
                    SimpleInventory inv = new SimpleInventory(54);

                    inv.addStack(new ItemStack(Items.DIAMOND_SWORD));

                    GenericContainerScreenHandler handler = GenericContainerScreenHandler.createGeneric9x6(0, playerInv, inv);

                    MinecraftClient client = source.getClient();
                    client.execute(() -> {
                        client.setScreen(new NBTArenaScreen(
                                handler, playerInv, Text.literal("NBT Arena")
                        ));
                    });

//                    source.getPlayer().openHandledScreen(
//                            new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> {
//                                return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory);
//                            }, Text.literal("NBT Arena"))
//                    );

                    return 1;
                }
                ));
            }
        );
    }
}
