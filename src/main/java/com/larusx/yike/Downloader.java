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
        
        // 如果有创建时间，将其添加到文件名中，以便按时间排序
        if (createTime > 0) {
            String timePrefix = formatTimePrefix(createTime);
            fileName = timePrefix + "_" + fileName;
        }
        
        InputStream content = httpResponse.getEntity().getContent();
        fileSink.write(content, fileName);
    }
    
    private String formatTimePrefix(long timestamp) {
        // 格式化时间为 YYYYMMDD_HHMMSS 格式，便于按时间排序
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date(timestamp * 1000)); // 假设时间戳是以秒为单位
    }
}
