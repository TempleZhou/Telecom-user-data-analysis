![mark](http://ovi2a27q9.bkt.clouddn.com/blog/171219/93A1H0HcDg.png?imageslim)
> 这里使用不同的文件夹模拟不同的ftp服务器

系统特性：
* 未使用flume，借助zookeeper创建高可用采集程序
* 每天一个文件夹，一小时一个文件
* 4台机器并行采集
* 4台采集机器作为整体，任意机器宕机，其他机器会接管宕机机器的采集任务，宕机机器恢复后会继续采集 --高可用
