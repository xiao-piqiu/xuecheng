package com.xuecheng.media.service.impl;

import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    public List<MediaProcess> getMediaProcessList(int shardIndex,int shardTotal,int count){
       return mediaProcessMapper.selectListByShardIndex(shardTotal,shardIndex,count);
    }

    @Override
    public boolean startTask(long id) {
        int i = mediaProcessMapper.startTask(id);
        return i<=0?false:true;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess==null){
            return;
        }
        //如果任务失败
        if (status.equals("3")){
            mediaProcess.setStatus("3");
            mediaProcess.setFailCount(mediaProcess.getFailCount()+1);
            mediaProcess.setErrormsg(errorMsg);
            mediaProcessMapper.updateById(mediaProcess);
            return;
        }
        //如果任务成功
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        //更新url
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);
        //更新状态
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);
        //更新到历史记录表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //删除任务
        mediaProcessMapper.deleteById(taskId);
    }
}
