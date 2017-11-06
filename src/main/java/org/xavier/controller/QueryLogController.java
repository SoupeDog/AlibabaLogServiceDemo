package org.xavier.controller;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetHistogramsRequest;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.request.ListLogStoresRequest;
import com.aliyun.openservices.log.response.GetHistogramsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xavier.config.AliyunProperties;

import java.util.ArrayList;

/**
 * 描述信息：<br/>
 *
 * @author Xavier
 * @version 1.0
 * @date 2017.11.06
 * @since Jdk 1.8
 */
@RestController
public class QueryLogController {
    @Autowired
    AliyunProperties aliyunProperties;
    @Autowired
    Client client;
    @Autowired
    ObjectMapper mapper;

    @GetMapping("/get")
    public Object getLog() throws LogException {
        String project = aliyunProperties.getProject(); // 上面步骤创建的项目名称
        String logstore = "acslog-device-service-dev-lw"; // 上面步骤创建的日志库名称
        int offset = 0;
        int size = 100;
        String logStoreSubName = "";
        ListLogStoresRequest req1 = new ListLogStoresRequest(project, offset, size, logStoreSubName);
        ArrayList<String> logStores = client.ListLogStores(req1).GetLogStores();
        System.out.println("ListLogs:" + logStores.toString() + "\n");
        return logStores;
    }

    @GetMapping("/get2")
    public Object getLog2() throws LogException, InterruptedException, JsonProcessingException {
        String project = aliyunProperties.getProject(); // 上面步骤创建的项目名称
        String logstore = "czstore"; // 上面步骤创建的日志库名称
        String query = "level:info";
        int from = 1508860800;
        int to = 1508947200;
        GetHistogramsRequest req3 = new GetHistogramsRequest(project, logstore, "", query, from, to);
        GetHistogramsResponse res3 = client.GetHistograms(req3);
        long total_log_lines = res3.GetTotalCount();
        int log_offset = 0;
        int log_line = 10;
        while (log_offset <= total_log_lines) {
            GetLogsResponse res4 = null;
            // 对于每个 log offset,一次读取 10 行 log，如果读取失败，最多重复读取 3 次。
            for (int retry_time = 0; retry_time < 3; retry_time++) {
                GetLogsRequest req4 = new GetLogsRequest(project, logstore, from, to, "", query, log_offset,
                        log_line, false);
                res4 = client.GetLogs(req4);
                if (res4 != null && res4.IsCompleted()) {
                    break;
                }
                Thread.sleep(200);
            }
            System.out.println("Read log count:" + String.valueOf(res4.GetCount()));
            System.out.println(mapper.writeValueAsString(res4.GetLogs()));
            log_offset += log_line;
        }
        return null;
    }
}
