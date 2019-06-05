package com.coreke.crawler;

import org.apache.http.HttpStatus;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * 这个类演示了crawler4j如何爬取一个网页的数据
 * 以及抽取出标题和文本信息
 */
public class Downloader {

    private final Parser parser;
    private final PageFetcher pageFetcher;

    public Downloader() throws Exception {
        // 实例化爬虫配置
        CrawlConfig config = new CrawlConfig();
        // 实例化解析器
        parser = new Parser(config);
        // 实例化页面获取器
        pageFetcher = new PageFetcher(config);
    }

    public static void main(String[] args) throws Exception {
        Downloader downloader = new Downloader();
        downloader.processUrl("http://www.java1234.com/a/yuanchuang/j2sev2/2016/0606/6221.html");
        downloader.processUrl("http://blog.java1234.com/blog/articles/103.html");
    }

    public void processUrl(String url) {
        System.out.println("处理url:"+url);
        Page page = download(url);
        if (page != null) {
            // 获取解析数据
            ParseData parseData = page.getParseData();
            if (parseData != null) {
                // 假如是html数据类型
                if (parseData instanceof HtmlParseData) {
                    // 获取数据
                    HtmlParseData htmlParseData = (HtmlParseData) parseData;

                    System.out.println("标题: " + htmlParseData.getTitle());
                    System.out.println("纯文本长度: " + htmlParseData.getText().length());
                    System.out.println("html长度: " + htmlParseData.getHtml().length());
                }
            } else {
                System.out.println("不能解析该页面");
            }
        } else {
            System.out.println("不能获取页面的内容");
        }
    }

    /**
     * 下载指定Url的页面内容
     * @param url
     * @return
     */
    private Page download(String url) {
        // 实例化weburl
        WebURL curURL = new WebURL();
        // 设置url
        curURL.setURL(url);
        PageFetchResult fetchResult = null;
        try {
            // 获取爬取结果
            fetchResult = pageFetcher.fetchPage(curURL);
            // 判断http状态是否是200
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
                // 封装Page
                Page page = new Page(curURL);
                // 设置内容
                fetchResult.fetchContent(page,10000);
                // 解析page
                parser.parse(page, curURL.getURL());
                // 返回page
                return page;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 假如jvm没有回收 用代码回收对象 防止内存溢出
            if (fetchResult != null) {
                // 销毁获取内容
                fetchResult.discardContentIfNotConsumed();
            }
        }
        return null;
    }
}