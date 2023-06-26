package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exeception.XueChengPlusException;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.search.po.CourseIndex;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/25 15:14
 * @Description: 课程发布任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;
    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex,shardTotal,"course_publish",30,60);
    }
    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mqMessage获取课程id
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        //课程静态话上传到minio
        generateCourseHtml(mqMessage,courseId);
        //向elasticsearch写索引
        saveCourseIndex(mqMessage,courseId);
        //向redis写缓存
        saveCourseCache(mqMessage,courseId);
        return true;
    }
    //课程静态话上传到minio
    private void generateCourseHtml(MqMessage mqMessage,long courseId){
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if(stageOne>0){
            log.info("课程静态化任务已完成");
            return;
        }
        //进行课程静态化
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file==null){
            XueChengPlusException.cast("生产的静态文件为空");
        }
        coursePublishService.uploadCourseHtml(courseId,file);
        //将状态修改为已完成
        mqMessageService.completedStageOne(taskId);
    }
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二阶段状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        //任务幂等性处理
        if (stageTwo>0){
            log.info("课程索引信息写入已完成");
            return;
        }
        //查询课程信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        //调用添加索引服务
        Boolean aBoolean = searchServiceClient.add(courseIndex);
        if(!aBoolean){
            XueChengPlusException.cast("远程课程索引添加失败");
        }
        mqMessageService.completedStageTwo(taskId);
    }
    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId) {
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二阶段状态
        int stageThree = mqMessageService.getStageThree(taskId);
        //任务幂等性处理
        if (stageThree>0){
            log.info("课程缓存已完成");
            return;
        }
        //调用redis服务
        mqMessageService.completedStageThree(taskId);
    }
}
