package com.coreke.crawler;

import com.coreke.collectingData.CrawlStat;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.List;

/**
 * 爬虫控制器
 * @author
 *
 */
public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "d:/crawl"; // 定义爬虫数据存储位置
        int numberOfCrawlers = 7; // 定义7个爬虫，也就是7个线程

        CrawlConfig config = new CrawlConfig(); // 定义爬虫配置
        config.setCrawlStorageFolder(crawlStorageFolder); // 设置爬虫文件存储位置
        config.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
        /**
         * 实例化爬虫控制器
         */
        PageFetcher pageFetcher = new PageFetcher(config); // 实例化页面获取器
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig(); // 实例化爬虫机器人配置 比如可以设置 user-agent

        // 实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件 规定了该网站哪些页面可以爬，哪些页面禁止爬，该类是对robots.txt规范的实现
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        // 实例化爬虫控制器
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /**
         * 配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
         */
        controller.addSeed("https://zh.wikipedia.org/wiki/");
        controller.addSeed("https://zh.wikipedia.org/wiki/宝莲灯/");

        /**
         * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
         */
        controller.start(MyCrawler.class, numberOfCrawlers);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData(); // 当多个线程爬虫完成任务时，获取爬虫本地数据
        long totalLinks = 0;
        long totalTextSize = 0;
        int totalProcessedPages = 0;
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            totalLinks += stat.getTotalLinks();
            totalTextSize += stat.getTotalTextSize();
            totalProcessedPages += stat.getTotalProcessedPages();
        }

        // 打印数据
        System.out.println("统计数据：");
        System.out.println("总处理页面："+totalProcessedPages);
        System.out.println("总链接长度："+totalLinks);
        System.out.println("总文本长度："+totalTextSize);

        // 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置 无阻塞
        controller.startNonBlocking(MyCrawler.class, numberOfCrawlers);
        // 休息5秒
        Thread.sleep(10 * 1000);

        System.out.println("休息10秒");

        // 停止爬取
        controller.shutdown();
        System.out.println("停止爬取");

        // 等待结束任务
        controller.waitUntilFinish();
    }
}