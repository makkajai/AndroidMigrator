package com.makkajai.migrator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Set;
import java.util.TreeSet;

/**
 * The migration manager which will help us migrate the .
 */
public class MigrationManager {

    private static final MigrationManager INSTANCE = new MigrationManager();
    private static final String TAG = "MigrationManager";
    public static final String MIGRATION_KEY = TAG + "_version";

    private final TreeSet<MigrateToVersionTask> tasks = new TreeSet<MigrateToVersionTask>();

    private MigrationManager() {

    }

    public static MigrationManager getMigrationManager() {
        return INSTANCE;
    }

    public void migrate(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        int latestVersion = prefs.getInt(MIGRATION_KEY, 0);
        Log.w(TAG, "Latest migrated version: " + latestVersion);
        SharedPreferences.Editor editor = prefs.edit();

        if(isFirstInstall(activity, latestVersion)) {
            //Need to put the last version in the migration into the preferences;
            if(tasks.size() > 0) {
                editor.putInt(MIGRATION_KEY, tasks.last().getVersion());
                editor.apply();
            }
            Log.w(TAG, "Looks like this is a fresh install no need to run any migrations");
            return;
        }


        for (MigrateToVersionTask task : tasks) {
            Log.w(TAG, "Checking if the latest version is less than the task version: " + latestVersion + " Task Version: " + task.getVersion());
            if(task.getVersion() <= latestVersion) continue;

            Log.w(TAG, "Executing the task now at version: " + task.getVersion());
            task.execute();

            Log.w(TAG, "Done migration at version: " + task.getVersion());
            // mark first time has runned.
            editor.putInt(MIGRATION_KEY, task.getVersion());
            latestVersion = task.getVersion();
        }
        editor.apply();
    }

    public boolean isFirstInstall(Activity activity, int latestVersion) {
        try {
            long firstInstallTime =   activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).firstInstallTime;
            long lastUpdateTime = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).lastUpdateTime;
            Log.w(TAG, "First Install Time: " + firstInstallTime + " Last update time: " + lastUpdateTime);
            return latestVersion <= 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addMigration(MigrateToVersionTask migrateToVersionTask) {
        tasks.add(migrateToVersionTask);
    }
}
