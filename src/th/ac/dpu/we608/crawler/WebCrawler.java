package th.ac.dpu.we608.crawler;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class WebCrawler {

    private String userAgent = "GoogleBot";
    private String url = "";
    private Frontier frontier;

    private Connection conn;

    public WebCrawler() {

    }

    public WebCrawler(Frontier frontier) {
        this.frontier = frontier;
    }

    public void crawl(String url) {
        System.out.println("Crawling to " + url);

        this.setURL(url);
        this.setConnection();

        try {
            Connection.Response response = conn.execute();
            Document document = response.parse();
            int statusCode = response.statusCode();

            Element body = document.body();
            frontier.addContentBlock(url, body.toString(), statusCode);
            for (Element i : body.getElementsByTag("a")) {
                String href = i.attr("abs:href");

                if (checkURL(href)) {
                    if (!frontier.isDuplicate(href)) {
                        frontier.addToFrontier(href, url);
                    }
                }
            }
        } catch (HttpStatusException e) {
            frontier.addContentBlock(url, null, e.getStatusCode());
        } catch (SocketTimeoutException | UnknownHostException e) {
            frontier.addContentBlock(url, null, 1);
        } catch (MalformedURLException | UnsupportedMimeTypeException e) {
            // MalformedURLException - if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
            // UnsupportedMimeTypeException - if the response mime type is not supported and those errors are not ignored
        } catch (IOException e) {
            // if any error, skipp this url
            frontier.addContentBlock(url, null, 1);
            e.printStackTrace();
        }
    }

    private boolean checkURL(String url) {
        boolean check = false;

        if ((url.endsWith(".html") || url.endsWith(".htm")
                || url.endsWith(".asp") || url.endsWith(".aspx")
                || url.endsWith(".php") || url.endsWith(".jsp") || url.endsWith(".gs")
                || url.endsWith(".net") || url.endsWith(".th") || url.endsWith(".com") || url.endsWith("/")
                || url.contains(".aspx?") || url.contains(".php?"))
                && !url.endsWith("#") && !url.startsWith("mailto")) {
            check = true;
        }

        return check;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setFrontier(Frontier frontier) {
        this.frontier = frontier;
    }

    public void setConnection() {
        conn = Jsoup.connect(this.url);
        conn.userAgent(this.userAgent);
        conn.followRedirects(true);
        conn.timeout(3000);
    }


}
