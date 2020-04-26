package host.kuro.kurotwitter.listeners;

import host.kuro.kurotwitter.KuroTwitter;
import host.kuro.kurotwitter.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import twitter4j.Status;
import twitter4j.StatusAdapter;

public class TwitterListener extends StatusAdapter {

    private KuroTwitter plugin;
    private int MessageMaxLength;
    private String title;

    public TwitterListener(KuroTwitter plugin) {
        this.plugin = plugin;
        this.MessageMaxLength = plugin.getConfig().getInt("Twitter.MessageMaxLength", 48);
        this.title = plugin.getConfig().getString("Twitter.TitleName", "TWITTER");
    }

    public void onStatus(Status status) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD);
        sb.append(ChatColor.BOLD);
        sb.append("[" + title + "] : ");
        String mes = status.getText();
        mes = mes.replace("\n\n", "\n");
        if (mes.length() > MessageMaxLength) {
            mes = mes.substring(0, MessageMaxLength);
        }
        sb.append(mes);
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + new String(sb));
    }
}
