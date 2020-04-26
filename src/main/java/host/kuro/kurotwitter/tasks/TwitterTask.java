package host.kuro.kurotwitter.tasks;

import host.kuro.kurotwitter.KuroTwitter;
import host.kuro.kurotwitter.lang.Language;
import host.kuro.kurotwitter.utils.ErrorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import twitter4j.Status;
import twitter4j.Twitter;

import java.text.SimpleDateFormat;
import java.util.List;

public class TwitterTask extends BukkitRunnable {

    private final KuroTwitter plugin;
    private final Twitter tw;
    private SimpleDateFormat sdf;
    private int UserNameMaxLength;
    private int MessageMaxLength;
    private int LatestCount;
    private String title;

    public TwitterTask(KuroTwitter plugin, Twitter tw) {
        this.plugin = plugin;
        this.tw = tw;
        this.sdf = new SimpleDateFormat(plugin.getConfig().getString("Twitter.DateFormat", "y/M/d (E) H:m"));
        this.UserNameMaxLength = plugin.getConfig().getInt("Twitter.UserNameMaxLength", 16);
        this.MessageMaxLength = plugin.getConfig().getInt("Twitter.MessageMaxLength", 48);
        this.LatestCount = plugin.getConfig().getInt("Twitter.LatestCount", 5);
        this.title = plugin.getConfig().getString("Twitter.TitleName", "TWITTER");
    }

    @Override
    public void run() {
        try {
            List<Status> statuses = tw.getHomeTimeline();
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(ChatColor.BOLD);
            sb.append(ChatColor.GOLD);
            sb.append("[");
            sb.append(title);
            sb.append("]\n");
            int count = 0;
            for (Status status : statuses) {
                if (count > LatestCount) break;
                String mes = status.getText();
                String sub = mes.substring(0, 2);
                if (sub.equals("RT")) continue;

                mes = mes.replace("\n\n", "\n");
                if (mes.length() > MessageMaxLength) {
                    mes = mes.substring(0, MessageMaxLength);
                }

                String name = status.getUser().getName();
                if (name.length() > UserNameMaxLength) {
                    name = name.substring(0, UserNameMaxLength);
                }
                sb.append(ChatColor.UNDERLINE);
                sb.append(ChatColor.GREEN);
                sb.append(name);
                sb.append(" - ");
                sb.append(sdf.format(status.getCreatedAt().getTime()));
                sb.append("\n");
                sb.append(ChatColor.WHITE);
                sb.append(mes);
                count++;
            }
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + new String(sb));

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
    }
}
