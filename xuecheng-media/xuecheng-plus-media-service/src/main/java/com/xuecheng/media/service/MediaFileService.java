package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {
 public File downloadFileFromMinIO(String bucket, String objectName);
 public boolean addMediaFilesToMinio(String localFilePath,String mimeType,String bucket, String objectName);

 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamDto uploadFileParamDto, String localFilePath);

 MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamDto uploadFileParamDto, String bucket_mediafiles, String objectName);
 public RestResponse<Boolean> checkFile(String fileMd5);

 public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
 public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);
 public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamDto uploadFileParamsDto);

 MediaFiles getFileById(String mediaId);
}
