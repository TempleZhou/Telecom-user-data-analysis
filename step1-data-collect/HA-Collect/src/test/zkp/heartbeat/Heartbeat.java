package test.zkp.heartbeat;

/**
 * Created by Temple.Zhou on 12/26/17
 */

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

public class Heartbeat implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("172.17.0.2:2181", 5000, new Heartbeat());
        System.out.println(zookeeper.getState());
        connectedSemaphore.await();
        String path1 = "";
        try {
            path1 = zookeeper.create("/keep-alive", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException.NodeExistsException e) {
            System.out.println("/keep-alive already exists...");
        }
        System.out.println("Success create znode: " + path1);
        String node1 = zookeeper.create("/keep-alive/node1", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        String node2 = zookeeper.create("/keep-alive/node2", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        String node3 = zookeeper.create("/keep-alive/node3", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        String node4 = zookeeper.create("/keep-alive/node4", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("Success create znode: " + node1 + " " + node2 + " " + node3 + " " + node4);
        List<String> nodeList = zookeeper.getChildren("/keep-alive", true);
        for (String node : nodeList) {
            System.out.println(node);
        }
        Thread.sleep(5000);
    }

    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}