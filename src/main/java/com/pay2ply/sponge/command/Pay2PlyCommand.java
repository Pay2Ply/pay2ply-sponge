package com.pay2ply.sponge.command;

import com.pay2ply.sponge.Pay2Ply;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class Pay2PlyCommand implements CommandExecutor {
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) {
    if (!src.hasPermission("pay2ply.command")) {
      src.sendMessage(Text.builder("Você não possui permissão para executar este comando.").color(TextColors.RED).build());
      return CommandResult.empty();
    }

    String token = args.getOne("token").get().toString().trim().toLowerCase();

    Pay2Ply.getInstance().getSdk().setToken(token);
    Pay2Ply.getInstance().getConfigurationNode().getNode("token").setValue(token);

    try {
      Pay2Ply.getInstance().getConfigurationLoader().save(Pay2Ply.getInstance().getConfigurationNode());
    } catch (IOException exception) {
      Pay2Ply.getInstance().getLogger().error(exception.getMessage());
    } finally {
      src.sendMessage(Text.builder(
        "Se o token do servidor estiver correto, a loja será vinculada em alguns instantes."
      ).color(TextColors.GREEN).build());
    }

    return CommandResult.success();
  }
}