package me.thejokerdev.frozzcore.discord;

import lombok.Getter;
import me.thejokerdev.frozzcore.BungeeMain;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;


@Getter
public class Bot implements Listener {
    private BungeeMain plugin;
    private JDA discord;
    private String token;
    private String guild;
    private boolean enabled = false;
    private ScheduledTask task;

    public Bot(BungeeMain plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        sendMSG(" &7● &9Discord&7:");

        boolean b = true;
        String msg = null;

        token = plugin.getConfig().getString("discord.token");
        guild = plugin.getConfig().getString("discord.guild");

        if (token == null || token.equals(" ")) {
            msg = "&c¡Debes ingresar un token para activar el módulo de Discord!";
            b = false;
        }

        if (token.contains("YOUR")) {
            msg = "&cVe a config.yml y cambia el token del bot de Discord.";
            b = false;
        }

        try {
            discord = JDABuilder.createDefault(token).build();
            msg = "&aActivo &7(&a✔&7)";
            discord.awaitReady();
        } catch (LoginException ignored) {
            b = false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (b) {
            if (guild == null) {
                msg = "&cVe a config.yml y cambia la guild del servidor";
                b = false;
            }

            if (guild != null && discord.getGuildCache().getElementById(guild) == null) {
                msg = "&cLa Guild proporcionada no es correcta.";
                b = false;
            }
        }


        sendMSG("   &fEstado: " + (b ? msg : "&cError &7(&c✘&7)"), "   &fInformación:");
        if (b){
            sendMSG("    &fToken: &7"+token);
            sendMSG("    &fServidor: &7#"+discord.getGuildById(guild).getName());
            enabled = true;
            //plugin.getProxy().getPluginManager().registerListener(plugin, this);
            task();
        } else {
            sendMSG("    &fError: "+msg);
        }

        sendMSG(" ");
    }

    public void stop(){
        task.cancel();

        int max = plugin.getProxy().getConfigurationAdapter().getListeners().iterator().next().getMaxPlayers();
        if (getPlayersInfoChannel()!=null){
            String msg = plugin.getConfig().getString("discord.settings.player-info.msg");
            msg = msg.replace("{online}", 0+"").replace("{max}", ""+max);
            getPlayersInfoChannel().getManager().setName(msg).queue();
        }
    }

    void task(){
        task = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            int i = plugin.getProxy().getOnlineCount();
            int max = plugin.getProxy().getConfigurationAdapter().getListeners().iterator().next().getMaxPlayers();

            if (getPlayersInfoChannel()!=null){
                if (getPlayersInfoChannel().getName().split("/")[0].contains(String.valueOf(i))){
                    return;
                }
                String msg = plugin.getConfig().getString("discord.settings.player-info.msg");
                msg = msg.replace("{online}", i+"").replace("{max}", ""+max);
                try {
                    getPlayersInfoChannel().getManager().setName(msg).queue();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 60, 1, TimeUnit.SECONDS);
    }

    public VoiceChannel getPlayersInfoChannel(){
        return discord.getVoiceChannelById(getPlugin().getConfig().getString("discord.settings.player-info.channel"));
    }


    public void sendMSG(String... in){
        plugin.getClassManager().getUtils().sendMSG(in);
    }
}
