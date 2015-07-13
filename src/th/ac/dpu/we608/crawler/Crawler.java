package th.ac.dpu.we608.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Crawler {

    public static void main(String[] args) throws SQLException {

        String seedURL = "http://www.dpu.ac.th/";

        Map<String, String> db_config = new HashMap<>();
        db_config.put("hostname", "localhost");
        db_config.put("port", "3306");
        db_config.put("database_name", "we_ir");
        db_config.put("frontier_table_name", "we_frontier");

        db_config.put("username", "root");
        db_config.put("password", null);

        Connection db = DriverManager.getConnection("jdbc:mysql://"
                        + db_config.get("hostname")
                        + ":" + db_config.get("port")
                        + "/" + db_config.get("database_name")
                        + "?useUnicode=true&characterEncoding=UTF-8",
                db_config.get("username"), db_config.get("password"));

        Frontier frontier = new Frontier(db, db_config.get("frontier_table_name"));
        WebCrawler crawler = new WebCrawler(frontier);

        int sleepTime = 1;
        int maxUrl = 10000;

        if (!"".equals(seedURL)) {
            frontier.addToFrontier(seedURL);
        }

        while (maxUrl > 0) {

            String nextURL = frontier.getNextURL();
            crawler.crawl(nextURL);

            // send to sleep
            for (int i = 0; i < sleepTime; i++) {
                try {
                    System.out.print(".");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            maxUrl--;
        }

    }
}
