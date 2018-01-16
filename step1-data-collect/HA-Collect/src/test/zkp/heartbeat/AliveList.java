package test.zkp.heartbeat;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * Created by Temple.Zhou on 1/16/18
 */
public class AliveList {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("172.17.0.2:2181", 5000, new Heartbeat());
        System.out.println(zk.getState());
        List<String> children = zk.getChildren("/keep-alive", false);
        for (String child : children) {
            System.out.println(child);
        }
    }
}
