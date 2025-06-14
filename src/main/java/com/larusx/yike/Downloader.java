package com.larusx.yike;

import cn.hutool.json.JSONObject;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Downloader {

    private HttpAgent httpAgent;

    private FileSink fileSink;

    private String bdstoken;

    private static String url = "https://photo.baidu.com/youai/file/v2/download?clienttype=70&fsid=";

    public Downloader(HttpAgent httpAgent, String bdstoken, String targetRootPath) {
        this.httpAgent = httpAgent;
        this.bdstoken = bdstoken;
        if (targetRootPath.startsWith("smb://")) {
            this.fileSink = new SMBFileSink(targetRootPath);
        } else {
            this.fileSink = new LocalFileSink(targetRootPath);
        }
    }

    public String fetchDownloadUrl(String fsid) throws IOException {
        JSONObject result = httpAgent.doRequestAndParse(URLUtils.addBstoken(url + fsid, bdstoken));
        return result.getStr("dlink");
    }

    public void downloadFile(String dlink) throws IOException {
        downloadFile(dlink, 0);
    }

    public void downloadFile(String dlink, long createTime) throws IOException {
        HttpResponse httpResponse = httpAgent.doRequest(dlink);
        String fileName = URLUtils.getFileName(httpResponse);
        
        // 默认使用根路径
        String relativePath = "";
        
        // 如果有创建时间，创建按年月的目录结构，并修改文件名前缀
        if (createTime > 0) {
            // 生成年份和月份目录
            Date date = new Date(createTime * 1000); // 假设时间戳是以秒为单位
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            
            String year = yearFormat.format(date);
            String month = monthFormat.format(date);
            
            // 设置相对路径为 YYYY/MM/
            relativePath = year + "/" + month + "/";
            
            // 生成文件名前缀 MMDD_HHMMSS_
            SimpleDateFormat fileNameFormat = new SimpleDateFormat("MMdd_HHmmss");
            String filePrefix = fileNameFormat.format(date) + "_";
            
            // 更新文件名
            fileName = filePrefix + fileName;
        }
        
        InputStream content = httpResponse.getEntity().getContent();
        // 写入文件，带上子路径
        fileSink.write(content, relativePath + fileName);
    }
}
