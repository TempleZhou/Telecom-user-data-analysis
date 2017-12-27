package test.mysql;

/**
 * Created by Temple.Zhou on 12/27/17
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        Connection con;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://172.17.0.3:3306/data_collect";
        String user = "root";
        String password = "root";
        //遍历查询结果集
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");

            Statement statement = con.createStatement();
            String sql = "select * from finished_file_list";

            ResultSet rs = statement.executeQuery(sql);
            System.out.println("-----------------");
            System.out.println("执行结果如下所示:");
            System.out.println("-----------------");
            System.out.println("filename" + "\t" + "finished_time");
            System.out.println("-----------------");

            String filename = null;
            String finished_time = null;
            while (rs.next()) {
                //获取stuname这列数据
                filename = rs.getString("filename");
                //获取stuid这列数据
                finished_time = rs.getString("finished_time");

                //输出结果
                System.out.println(filename + "\t" + finished_time);
            }
            rs.close();
            con.close();
        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            System.out.println("数据库数据成功获取！！");
        }
    }

}