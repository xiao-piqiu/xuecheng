package com.xuecheng.content.feignclient;

import com.xuecheng.search.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/26 14:08
 * @Description:
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引熔断{},异常{}",courseIndex,throwable.toString());
                return false;
            }
        };
    }
}
