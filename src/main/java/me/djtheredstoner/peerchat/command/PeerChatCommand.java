package me.djtheredstoner.peerchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.djtheredstoner.peerchat.PeerChatMod;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static dev.xpple.clientarguments.arguments.CEntityArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class PeerChatCommand {
    private static final SimpleCommandExceptionType PLAYER_IS_YOU = new SimpleCommandExceptionType(Text.literal("You cannot start a session with yourself"));

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            literal("peerchat")
                .then(literal("start")
                    .then(argument("player", player())
                        .executes(PeerChatCommand::start)
                    )
                )
                .then(literal("send")
                    .then(argument("message", greedyString())
                        .executes(PeerChatCommand::send)
                    )
                )
        );
    }

    private static int start(CommandContext<FabricClientCommandSource> ctx) throws CommandSyntaxException {
        var player = getCPlayer(ctx, "player");

        if (player == ctx.getSource().getPlayer()) {
            throw PLAYER_IS_YOU.create();
        }

        PeerChatMod.getInstance().start(player);

        return 0;
    }

    private static int send(CommandContext<FabricClientCommandSource> ctx) {
        var message = getString(ctx, "message");

        PeerChatMod.getInstance().getLastConnection().send(message);

        return 0;
    }

}
