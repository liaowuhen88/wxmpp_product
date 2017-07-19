package com.baodanyun.wxmpp.test;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by MissDan on 2017/6/27.
 */
public class TestMd4 {

    @Test
    public void main() throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/openfiremd4" +
                "";
       /* String url = "jdbc:mysql://localhost:3306/openfiremd4" +
                "?characterEncoding=utf8&amp;allowMultiQueries=true";*/

        String user = "root";
        String password = "liaowuhen";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);

        Statement statement = conn.createStatement();
        String sql = "select * from ofmucroom";

        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            String content = rs.getString("description");
            System.out.println("结果: " + content);
        }

        rs.close();
        conn.close();

    }

    @Test
    public void insert() throws Exception {
        String driver = "com.mysql.jdbc.Driver";
       /* String url = "jdbc:mysql://localhost:3306/openfiremd4" +
                "";*/
        String url = "jdbc:mysql://localhost:3306/openfiremd4" +
                "?characterEncoding=utf8&amp;allowMultiQueries=true";

        String user = "root";
        String password = "liaowuhen";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, user, password);

        Statement statement = conn.createStatement();
        String sql = "\n" +
        "INSERT INTO `ofmucroom` VALUES ('0', '0', '', '', '', '', '₁₈₂₅₁₈₈₀₈₈₁', '', null, '0', '0', '0', '0', '0', '0', null, '0', '0', null, '0', '0', '0', '0', null);\n";

        statement.execute(sql);

        conn.close();

    }


}

