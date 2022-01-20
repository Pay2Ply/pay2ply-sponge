package com.pay2ply.sponge;

import com.google.inject.Inject;
import com.pay2ply.sdk.SDK;
import com.pay2ply.sdk.dispense.Dispense;
import com.pay2ply.sponge.command.Pay2PlyCommand;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Plugin(
  id = "pay2ply",
  name = "Pay2Ply",
  version = "1.0-SNAPSHOT",
  description = "Um plugin de ativação de VIPs da Pay2Ply.",
  url = "https://pay2ply.com",
  authors = {
    "MrDrawn"
  }
)
public class Pay2Ply {
  private static Pay2Ply instance;

  public static Pay2Ply getInstance() {
    return instance;
  }

  private final SDK sdk = new SDK();

  public SDK getSdk() {
    return sdk;
  }

  @Inject
  private Game game;

  @Inject
  private Logger logger;

  public Logger getLogger() {
    return logger;
  }

  @Inject
  @DefaultConfig(sharedRoot = true)
  private File configuration = null;

  @Inject
  @DefaultConfig(sharedRoot = true)
  ConfigurationLoader<CommentedConfigurationNode> configurationLoader = null;

  CommentedConfigurationNode configurationNode = null;

  public CommentedConfigurationNode getConfigurationNode() {
    return configurationNode;
  }

  public ConfigurationLoader<CommentedConfigurationNode> getConfigurationLoader() {
    return configurationLoader;
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) {
    this.loadConfig();

    instance = this;

    sdk.setToken(this.configurationNode.getNode("token").getString());

    CommandSpec pay2plyCommand = CommandSpec.builder()
      .description(Text.of("Defina o token do servidor."))
      .arguments(GenericArguments.string(Text.of("token")))
      .executor(new Pay2PlyCommand())
      .build();

    game.getCommandManager().register(this, pay2plyCommand, "pay2ply");

    Task.Builder taskBuilder = Task.builder();
    taskBuilder.execute(() -> {
      Dispense[] dispenses = null;

      try {
        dispenses = sdk.getDispenses();
      } catch (Exception exception) {
        logger.warn(exception.getMessage());
      }

      if (dispenses == null) {
        return;
      }

      for (Dispense dispense : dispenses) {
        Task.Builder taskBuilder1 = Task.builder();
        taskBuilder1.execute(() -> {
          if (!Sponge.getServer().getPlayer(dispense.getUsername()).isPresent()) {
            return;
          }

          try {
            sdk.update(dispense.getUsername(), dispense.getId());
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), dispense.getCommand());

            if (this.configurationNode.getNode("messages").getBoolean()) {
              this.logger.info(String.format("O produto de %s foi ativo.", dispense.getUsername()));
            }
          } catch (Exception exception) {
            logger.warn(exception.getMessage());
          }
        }).submit(getInstance());
      }
    }).async().delay(60, TimeUnit.SECONDS).interval(20, TimeUnit.SECONDS).submit(this);
  }

  private void loadConfig() {
    try {
      this.saveDefaultConfig();
      this.configurationNode = this.configurationLoader.load();
    } catch (IOException exception) {
      this.logger.error(String.format("Falha ao tentar carregar as configurações do plugin: %s", exception.getMessage()));
    }
  }

  private void saveDefaultConfig() throws IOException {
    if (this.configuration.exists()) {
      return;
    }

    if (!this.configuration.createNewFile()) {
      throw new IOException("Algo não saiu como esperado ao tentar criar aquivo de configurações");
    }

    this.configurationNode = this.configurationLoader.load();

    this.configurationNode.getNode("token").setValue("");
    this.configurationNode.getNode("messages").setValue(false);

    this.configurationLoader.save(this.configurationNode);
  }
}