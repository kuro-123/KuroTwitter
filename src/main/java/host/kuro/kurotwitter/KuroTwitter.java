package host.kuro.kurotwitter;

import host.kuro.kurotwitter.lang.Language;
import host.kuro.kurotwitter.listeners.TwitterListener;
import host.kuro.kurotwitter.tasks.TwitterTask;
import host.kuro.kurotwitter.utils.ErrorUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class KuroTwitter extends JavaPlugin {

    public static boolean DEBUG;

    private Twitter twitter = null;
    private TwitterTask twitterTask;
    private BukkitTask taskid;

    @Override
    public void onEnable() {

        // language setup
        Language.load("UTF-8");
        getLogger().info(Language.translate("plugin.setup.language"));

        // directory setup
        getLogger().info(Language.translate("plugin.setup.directory"));
        if(!getDataFolder().exists()) { getDataFolder().mkdir(); }

        // load settings
        getLogger().info(Language.translate("plugin.setup.settings"));
        this.saveDefaultConfig();
        DEBUG = this.getConfig().getBoolean("debug", false);

        // regist twitter api
        if (!RegistTwitter()) {
            getLogger().warning(Language.translate("plugin.shutdown"));
            Bukkit.getServer().shutdown();
            return;
        }

        getLogger().info(Language.translate("plugin.loaded"));
    }

    private boolean RegistTwitter() {
        try {
            final String TWITTER_CONSUMER_KEY = this.getConfig().getString("Twitter.ConsumerKey");
            final String TWITTER_CONSUMER_SECRET = this.getConfig().getString("Twitter.ConsumerSecret");
            final String TWITTER_ACCESS_TOKEN = this.getConfig().getString("Twitter.AccessToken");
            final String TWITTER_ACCESS_TOKEN_SECRET = this.getConfig().getString("Twitter.AccessTokenSecret");
            final String TWITTER_BROADCAST_ID = this.getConfig().getString("Twitter.BroadcastID");
            if (TWITTER_CONSUMER_KEY.length() <= 0 ||
                TWITTER_CONSUMER_SECRET.length() <= 0 ||
                TWITTER_ACCESS_TOKEN.length() <= 0 ||
                TWITTER_ACCESS_TOKEN_SECRET.length() <= 0 ||
                TWITTER_BROADCAST_ID.length() <= 0) {
                return false;
            }

            // twitter api setting
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            builder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN);
            builder.setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
            Configuration conf = builder.build();

            // stream instance
            TwitterStream twitterStream = new TwitterStreamFactory(conf).getInstance();
            // listener regist
            twitterStream.addListener(new TwitterListener(this));
            // filter setting
            FilterQuery filter = new FilterQuery();
            long val = Long.parseLong(TWITTER_BROADCAST_ID);
            filter.follow(new long[]{val});
            twitterStream.filter(filter);
            // task make
            twitter = new TwitterFactory(conf).getInstance();
            twitterTask = new TwitterTask(this, twitter);
            int delay = this.getConfig().getInt("Twitter.TaskDelay", 100);
            int period = this.getConfig().getInt("Twitter.TaskPeriod", 72000);
            taskid = twitterTask.runTaskTimer(this, delay, period);
        } catch (Exception ex) {
            getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            return false;
        }
        return true;
    }

    @Override
    public void onDisable() {
        taskid.cancel();
        getLogger().info(Language.translate("plugin.unloaded"));
    }
}
