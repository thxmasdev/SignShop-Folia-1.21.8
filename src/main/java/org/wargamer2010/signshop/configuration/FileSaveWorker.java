package org.wargamer2010.signshop.configuration;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.wargamer2010.signshop.SignShop;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class FileSaveWorker implements Runnable {

    File ymlfile;
    String fileName;
    private final LinkedBlockingQueue<FileConfiguration> saveQueue = new LinkedBlockingQueue<>();
    private ScheduledTask scheduledTask;

    public FileSaveWorker(File ymlfile) {
        this.ymlfile = ymlfile;
        this.fileName = ymlfile.getName();
    }

    @Override
    public void run() {
        if (!saveQueue.isEmpty()) {
            saveToFile(saveQueue.poll());
        }
    }

    public void start(Plugin plugin) {
        scheduledTask = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> run(), 50, 50, TimeUnit.MILLISECONDS);
    }

    public void queueSave(FileConfiguration config) {
        if (config == null)
            return;

        try {
            saveQueue.put(config);
        } catch (InterruptedException ex) {
            SignShop.log("Failed to save " + fileName, Level.WARNING);
        }
    }

    private void saveToFile(FileConfiguration config) {
        try {
            config.save(ymlfile);
        } catch (IOException ex) {
            SignShop.log("Failed to save " + fileName, Level.WARNING);
        }
    }

    public void stop() {
        try {
            if (!saveQueue.isEmpty()) {
                saveToFile(saveQueue.poll());
            }
            if (scheduledTask != null) {
                scheduledTask.cancel();
            }
            SignShop.log("Successfully cancelled async " + fileName + " save task", Level.INFO);
        } catch (Exception ex) {
            SignShop.log("Failed to cancel " + fileName + " save task because: " + ex.getMessage(), Level.WARNING);
        }
    }
}
