package com.coreke.crawler;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import com.coreke.collectingData.CrawlStat;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * 自定义爬虫类需要继承WebCrawler类，决定哪些url可以被爬以及处理爬取的页面信息
 * @author
 *
 */
public class MyCrawler extends WebCrawler {

    CrawlStat myCrawlStat; // 定义爬虫状态对象，用户统计和分析

    /**
     * 正则匹配指定的后缀文件
     */
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");

    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息
     * 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();  // 得到小写的url
        return !FILTERS.matcher(href).matches()   // 正则匹配，过滤掉我们不需要的后缀文件
                && href.startsWith("https://zh.wikipedia.org/wiki/");  // url必须是https://zh.wikipedia.org/wiki/开头，规定站点
    }

    /**
     * 当我们爬到我们需要的页面，这个方法会被调用，我们可以尽情的处理这个页面
     * page参数封装了所有页面信息
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();  // 获取url
        int docid = page.getWebURL().getDocid(); // 获取docid url的唯一识别 类似主键
        int parentDocid = page.getWebURL().getParentDocid(); // 获取上级页面的docId

        System.out.println("docId:"+docid);
        System.out.println("url:"+url);
        System.out.println("上级页面docId:"+parentDocid);

        if (page.getParseData() instanceof HtmlParseData) {  // 判断是否是html数据
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); // 强制类型转换，获取html数据对象
            String text = htmlParseData.getText();  // 获取页面纯文本（无html标签）
            String html = htmlParseData.getHtml();  // 获取页面Html
            Set<WebURL> links = htmlParseData.getOutgoingUrls();  // 获取页面输出链接

            myCrawlStat.incTotalLinks(links.size()); // 总链接加link.size个
            try {
                myCrawlStat.incTotalTextSize(htmlParseData.getText().getBytes("UTF-8").length); // 文本长度增加
            } catch (UnsupportedEncodingException ignored) {
                // Do nothing
            }
            System.out.println("纯文本长度: " + text.length());
            System.out.println("html长度: " + html.length());
            System.out.println("输出链接个数: " + links.size());
        }
        // 每获取3个页面数据 我们处理下数据
        if ((myCrawlStat.getTotalProcessedPages() % 3) == 0) {
            dumpMyData();
        }
    }

    /**
     * 获取下爬虫状态
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * 当任务完成时调用
     */
    @Override
    public void onBeforeExit() {
        dumpMyData(); // 处理处理
    }

    /**
     * 处理数据
     */
    public void dumpMyData() {
        int id = getMyId();
        System.out.println("当前爬虫实例id:"+id);
        System.out.println("总处理页面："+myCrawlStat.getTotalProcessedPages());
        System.out.println("总链接长度："+myCrawlStat.getTotalLinks());
        System.out.println("总文本长度："+myCrawlStat.getTotalTextSize());
    }
}