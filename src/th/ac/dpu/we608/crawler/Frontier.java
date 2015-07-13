package th.ac.dpu.we608.crawler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Frontier {

    private Connection conn;
    private String tableName;

    public Frontier() {

    }

    public Frontier(Connection connection, String frontierTableName) throws SQLException {
        this.conn = connection;
        this.tableName = frontierTableName;
    }

    public String getNextURL() {
        String query = "SELECT * FROM " + tableName + " WHERE id = (SELECT min(id) FROM " + tableName + " WHERE status = 0)";
        PreparedStatement st;
        ResultSet rs;
        String foundUrl = "";

        try {
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            if (rs.next()) {
                foundUrl = rs.getString("url");
            }
        } catch (SQLException e) {
            System.err.println("ERROR getNextURL()");
            e.printStackTrace();
        }
        return foundUrl;
    }

    public boolean isDuplicate(String url) {
        String query = "SELECT * FROM " + tableName + " WHERE url = ?";
        PreparedStatement st;
        ResultSet rs;
        boolean check = false;

        try {
            st = conn.prepareStatement(query);
            st.setString(1, url);

            rs = st.executeQuery();

            if (rs.next()) {
                check = true;
            }

        } catch (SQLException e) {
            System.err.println("ERROR isDuplicate() - " + url);
            e.printStackTrace();
        }

        return check;
    }

    public void addContentBlock(String url, String content, int httpStatusCode) {
        String query = "UPDATE " + tableName + " SET content = ?, status = ? WHERE url = ?";
        PreparedStatement st;

        try {
            st = conn.prepareStatement(query);
            st.setString(1, content);
            st.setInt(2, httpStatusCode);
            st.setString(3, url);

            st.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR addContentBlock() - " + url);
            e.printStackTrace();
        }
    }

    public void addToFrontier(String url) {
        if (!this.isDuplicate(url)) {
            this.addToFrontier(url, null);
        }
    }

    public void addToFrontier(String url, String inboundLink) {
        String query = "INSERT INTO " + tableName + " (url, inbound_link) VALUES (?, ?)";
        PreparedStatement st;

        try {
            st = conn.prepareStatement(query);
            st.setString(1, url);
            st.setString(2, inboundLink);

            st.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR addToFrontier() - " + url);
            e.printStackTrace();
        }
    }
}
