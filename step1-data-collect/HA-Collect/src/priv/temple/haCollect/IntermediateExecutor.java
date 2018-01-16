package priv.temple.haCollect;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by Temple.Zhou on 1/16/18
 */
public class IntermediateExecutor implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    DataMonitor dm;
    ZooKeeper zk;
    String nodeName;

    public IntermediateExecutor(String hostPort, String znode, String nodeName) throws IOException {
        this.nodeName = nodeName;
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void process(WatchedEvent event) {
        dm.process(event);
    }

    @Override
    public void exists(byte[] data) throws KeeperException, InterruptedException {
        if (data != null) {
            System.out.println("heartbeat:" + this.nodeName + " is recover");
        }
    }

    @Override
    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }
}
