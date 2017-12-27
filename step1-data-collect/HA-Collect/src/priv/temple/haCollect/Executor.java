package priv.temple.haCollect;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class Executor
        implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    String znode;

    DataMonitor dm;

    ZooKeeper zk;

    String filename;

    String exec[];

    Process child;

    static final List<String> nodeList = Arrays.asList("collector1", "collector2", "collector3", "collector4");
    static final List<String> ftpList = Arrays.asList("ftp1", "ftp2", "ftp3", "ftp4");

    public Executor(String hostPort, String znode) throws KeeperException, IOException {
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        if (args.length < 2) {
            System.err.println("USAGE: Executor hostPort localhostName");
            System.exit(2);
        }
        String hostPort = args[0];
        String localhostName = args[1];
        //keep alive
        new Thread(new KeepAlive(hostPort, localhostName)).start();
        Thread.sleep(1000);
        for (String nodeName : nodeList) {
            try {
                System.out.println("detecting " + nodeName + " status...");
                new Thread(new Executor(hostPort, "/keep-alive/" + nodeName)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void process(WatchedEvent event) {
        dm.process(event);
    }

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

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    static class StreamWriter extends Thread {
        OutputStream os;

        InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        public void run() {
            byte b[] = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
            } catch (IOException e) {
            }

        }
    }

    public void exists(byte[] data) {
        if (data == null) {
            if (child != null) {
                System.out.println("Killing process");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                }
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("node-status: " + new String(data));
            try {
                System.out.println("Starting child");
                child = Runtime.getRuntime().exec("echo job");
                new StreamWriter(child.getInputStream(), System.out);
                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}