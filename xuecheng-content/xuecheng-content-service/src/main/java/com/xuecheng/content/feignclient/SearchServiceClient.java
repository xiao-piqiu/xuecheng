package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.search.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/26 14:05
 * @Description:
 */
@FeignClient(value = "search",configuration = {MultipartSupportConfig.class},fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {
    @PostMapping("/search/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex) ;
}
