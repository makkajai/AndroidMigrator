package com.makkajai.migrator;

/**
 * The interface that will need to be implemented which represents the sequence of steps needed to run the migration.
 */
public class MigrateToVersionTask implements Comparable {
    private int version;

    public MigrateToVersionTask(int version) {

        this.version = version;
    }

    @Override
    public int compareTo(Object another) {
        return Integer.valueOf(version).compareTo(((MigrateToVersionTask)another).getVersion());
    }

    public int getVersion() {
        return version;
    }

    public void execute() {

    }
}
