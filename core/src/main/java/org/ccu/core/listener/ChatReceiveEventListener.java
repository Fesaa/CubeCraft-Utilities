package org.ccu.core.listener;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.entity.player.GameMode;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.ccu.core.CCU;
import org.ccu.core.config.imp.GameStatsTracker;
import org.ccu.core.config.internal.CCUinternalConfig;
import org.ccu.core.config.subconfig.EndGameSubConfig;
import org.ccu.core.config.subconfig.StatsTrackerSubConfig;
import org.ccu.core.utils.EggWarsMapInfo;

public class ChatReceiveEventListener {

  private final CCU addon;
  private final Pattern playerElimination = Pattern.compile("([a-zA-Z0-9_]{2,16}) has been eliminated from the game.");
  private final Pattern mightBeKillMessage = Pattern.compile(this.userNameRegex + ".{6,55}" + this.userNameRegex + ".{1,34}" + this.assistRegex);
  private final String userNameRegex = "([a-zA-Z0-9_]{2,16})";
  private final String assistRegex = "(\\s{0,5}\\(\\+\\d{1,2} assists?\\))?";
  private final List<Pattern> killMessages = new ArrayList<>();

  @Inject
  public ChatReceiveEventListener(CCU addon) {
    this.addon = addon;

    this.killMessages.add(Pattern.compile(this.userNameRegex + " shoved " + this.userNameRegex + " into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " thought they could survive in the void while escaping " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " slapped " + this.userNameRegex + " to death!" + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " showcased their weapon to " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was annihilated by " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was murdered by " + this.userNameRegex + "!" + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " \"accidentally\" tripped " + this.userNameRegex + " into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " couldn't fly while escaping " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was slain by " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " destroyed " + this.userNameRegex + " into oblivion\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " used " + this.userNameRegex + " as a punching bag\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " died in the void while escaping " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " betrayed " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was bombarded by " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " brawled " + this.userNameRegex + " to death!" + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " turned " + this.userNameRegex + " into a spooky ghost!" + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " flabbergasted " + this.userNameRegex + " with death\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " scared " + this.userNameRegex + " into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " tried to escape " + this.userNameRegex + " by jumping into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " yeeted " + this.userNameRegex + "into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was slapped into the the void by " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " tried being a ninja against " + this.userNameRegex + " but it didn't turn out so well\\.\\.\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " tripped " + this.userNameRegex + " into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " tripped " + this.userNameRegex + " into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " killed " + this.userNameRegex + "\\. LOL!" + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was sucked into a black hole while " + this.userNameRegex +  " watched\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was annihilated by " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " kicked " + this.userNameRegex + " into the abyss\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " made " + this.userNameRegex + " jump into the void\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was forced to kill " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was too scared to fight " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " was smacked into next week by " + this.userNameRegex + "\\." + this.assistRegex));
    this.killMessages.add(Pattern.compile(this.userNameRegex + " kicked " + this.userNameRegex + " into the void\\." + this.assistRegex));
  }

  @Subscribe
  public void onChatReceiveEvent(ChatReceiveEvent chatReceiveEvent) {
    String msg = chatReceiveEvent.chatMessage().getPlainText();
    ClientPlayer clientPlayer = this.addon.labyAPI().minecraft().clientPlayer();
    String userName = clientPlayer.getName();

    // Win Streak Counter
    StatsTrackerSubConfig statsTrackerSubConfig = this.addon.configuration().getStatsTrackerSubConfig();
    if (statsTrackerSubConfig.isEnabled()) {
      if (msg.equals("Congratulations, you win!")) {
        GameStatsTracker gameStatsTracker = statsTrackerSubConfig.getGameStatsTrackers().get(CCUinternalConfig.name);
        if (gameStatsTracker != null) {
          gameStatsTracker.registerWin();
        } else {
          gameStatsTracker = new GameStatsTracker();
          gameStatsTracker.registerWin();
          statsTrackerSubConfig.getGameStatsTrackers().put(CCUinternalConfig.name, gameStatsTracker);
        }
        CCUinternalConfig.won = true;
      }
    }

    // Auto GG
    EndGameSubConfig config = this.addon.configuration().getEndGameSubConfig();
    if (config.isEnabled().get() && !CCUinternalConfig.hasSaidGG) {
      String eliminationMessage = this.addon.labyAPI().minecraft().clientPlayer().getName() + " has been eliminated from the game.";
      if (msg.equals("Congratulations, you win!") || (msg.equals(eliminationMessage) && config.getOnElimination().get())) {
        this.addon.labyAPI().minecraft().chatExecutor().chat(config.getGameEndMessage().get().msg, false);
        if (!config.getCustomMessage().isDefaultValue()) {
          this.addon.labyAPI().minecraft().chatExecutor().chat(config.getCustomMessage().get(), false);
        }
        CCUinternalConfig.hasSaidGG = true;
        return;
      }
    }

    // Friend Message Sound
    if (this.addon.configuration().friendMessageSound().get()) {
      if (msg.matches("\\[Friend\\] ([a-zA-Z0-9_]{2,16}) -> Me: .*")) {
        this.addon.labyAPI().minecraft().sounds().playSound(
            ResourceLocation.create("minecraft", "entity.experience_orb.pickup"), 1000, 1);
        return;
      }
    }

    // Start of game
    if (msg.equals("Let the games begin!")) {
      CCUinternalConfig.inPreLobby = false;
      Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
        if (this.addon.configuration().getEggWarsMapInfoSubConfig().isEnabled().get()) {
          CCUinternalConfig.updateTeamColour(this.addon);
          EggWarsMapInfo.eggWarsMapInfo(this.addon);
        }
        if (this.addon.configuration().getAutoVoteSubConfig().isEnabled()) {
          //AutoVote.vote(this.addon);
          this.addon.logger().info("Tried to auto vote");
        }
        this.addon.rpcManager.startOfGame();
        this.addon.rpcManager.updateRPC();
      },100, TimeUnit.MILLISECONDS);
      return;
    }

    // RPC
    Matcher matcher = playerElimination.matcher(msg);
    if (matcher.matches()) {
      this.addon.rpcManager.registerDeath(matcher.group(1));
      return;
    }

    // Party Tracker
    if (msg.matches("You have joined [a-zA-Z0-9_]{2,16}'s party!")) {
      CCUinternalConfig.partyStatus = true;
      return;
    }
    if (msg.matches("You have left your party!")
        || msg.matches("You were kicked from your party!")
        || msg.matches("The party has been disbanded!")) {
      CCUinternalConfig.partyStatus = false;
      return;
    }

    if (msg.matches("[a-zA-Z0-9_]{2,16} joined the party!")) {
      CCUinternalConfig.partyStatus = true;
    }
    if (msg.contains(userName) && this.mightBeKillMessage.matcher(msg).matches()) {
      for (Pattern killMessagePattern : this.killMessages) {
        Matcher killMessageMatcher = killMessagePattern.matcher(msg);
        if (killMessageMatcher.matches()) {

          String other;

          String userOne = killMessageMatcher.group(1);
          String userTwo = killMessageMatcher.group(2);

          if (userOne.equals(userName)) {
            other = userTwo;
          } else if (userTwo.equals(userName)) {
            other = userOne;
          } else {
            return;
          }

          if (clientPlayer.networkPlayerInfo().gameMode().equals(GameMode.SPECTATOR)) {
            this.addon.labyAPI().minecraft().chatExecutor().displayClientMessage(userName + " killed by " + other);

            GameStatsTracker gameStatsTracker = this.addon.configuration().getStatsTrackerSubConfig().getGameStatsTrackers().get(CCUinternalConfig.name);
            if (gameStatsTracker != null) {
              gameStatsTracker.registerDeath(other);
            } else {
              gameStatsTracker = new GameStatsTracker();
              gameStatsTracker.registerDeath(other);
              this.addon.configuration().getStatsTrackerSubConfig().getGameStatsTrackers().put(CCUinternalConfig.name, gameStatsTracker);
            }

          } else {
            this.addon.labyAPI().minecraft().chatExecutor().displayClientMessage(other + " killed by " + userName);

            GameStatsTracker gameStatsTracker = this.addon.configuration().getStatsTrackerSubConfig().getGameStatsTrackers().get(CCUinternalConfig.name);
            if (gameStatsTracker != null) {
              gameStatsTracker.registerKill(other);
            } else {
              gameStatsTracker = new GameStatsTracker();
              gameStatsTracker.registerKill(other);
              this.addon.configuration().getStatsTrackerSubConfig().getGameStatsTrackers().put(CCUinternalConfig.name, gameStatsTracker);
            }
          }
          return;
        }
      }
    }

  }
}
