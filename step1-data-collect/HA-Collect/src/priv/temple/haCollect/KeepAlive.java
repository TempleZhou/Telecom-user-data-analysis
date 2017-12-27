package priv.temple.haCollect;

import org.apache.zookeeper.*;
import java.io.IOException;
/**
 * Created by Temple.Zhou on 12/27/17
 */
public class KeepAlive implements Watcher, Runnable{
    ZooKeeper zk;
    private String localhostName;

    public KeepAlive(String hostPort, String localhostName) throws KeeperException, IOException {
        zk = new ZooKeeper(hostPort, 3000, this);
        this.localhostName = localhostName;
    }
    @Override
    public void run(){
        try {
            String path = zk.create("/keep-alive/"+localhostName, "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(path+" is keeping alive...");
            Thread.sleep(Integer.MAX_VALUE);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {

    }
}