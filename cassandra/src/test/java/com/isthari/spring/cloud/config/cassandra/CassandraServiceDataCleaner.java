package com.isthari.spring.cloud.config.cassandra;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.io.util.FileUtils;

/**
 * A cleanup utility that wipes the cassandra data directories.
 *
 * @author Ran Tavory (rantav@gmail.com)
 *
 */
public class CassandraServiceDataCleaner {

    /**
     * Creates all data dir if they don't exist and cleans them
     * @throws IOException
     */
    public void prepare() throws IOException {
        makeDirsIfNotExist();
        cleanupDataDirectories();
    }

    /**
     * Deletes all data from cassandra data directories, including the commit log.
     * @throws IOException in case of permissions error etc.
     */
    public void cleanupDataDirectories() throws IOException {
        for (String s: getDataDirs()) {
            cleanDir(s);
        }
    }
    /**
     * Creates the data diurectories, if they didn't exist.
     * @throws IOException if directories cannot be created (permissions etc).
     */
    public void makeDirsIfNotExist() throws IOException {
        for (String s: getDataDirs()) {
            mkdir(s);
        }
    }

    /**
     * Collects all data dirs and returns a set of String paths on the file system.
     *
     * @return
     */
    private Set<String> getDataDirs() {
        Set<String> dirs = new HashSet<String>();
        for (String s : DatabaseDescriptor.getAllDataFileLocations()) {
            dirs.add(s);
        }
        dirs.add(DatabaseDescriptor.getCommitLogLocation());
        return dirs;
    }
    /**
     * Creates a directory
     *
     * @param dir
     * @throws IOException
     */
    private void mkdir(String dir) throws IOException {
        FileUtils.createDirectory(dir);
    }

    /**
     * Removes all directory content from the file system
     *
     * @param dir
     * @throws IOException
     */
    private void cleanDir(String dir) throws IOException {
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            for (File f : dirFile.listFiles()) {
                FileUtils.deleteRecursive(f);
            }
        }
    }
}