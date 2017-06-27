package com.gmos.lab.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import java.io.IOException;

public class Utils {

    public static void doBulkLoad(Configuration conf, String hFilePath, String tableNameStr) throws Exception{
        FileSystem fs = FileSystem.newInstance(conf);
        chmod(new Path(hFilePath), fs);
        HBaseConfiguration.addHbaseResources(conf);
        LoadIncrementalHFiles loadFiles = new LoadIncrementalHFiles(conf);
        HTable table = new HTable(conf, tableNameStr);
        loadFiles.doBulkLoad(new Path(hFilePath), table);
    }

    public static void chmod(Path path, FileSystem fs) throws IOException {
        fs.setPermission(path, FsPermission.createImmutable((short) 0777));
        if (fs.getFileStatus(path).isFile()) {
            return;
        }
        RemoteIterator<LocatedFileStatus> fileStatuses = fs.listLocatedStatus(path);
        while(fileStatuses.hasNext()) {
            LocatedFileStatus status = fileStatuses.next();
            if (status != null) {
                fs.setPermission(status.getPath(), FsPermission.createImmutable((short) 0777));
                chmod(status.getPath(), fs);
            }
        }
    }

    public static void delete(Path path, FileSystem fs) throws IOException {
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
    }

}
