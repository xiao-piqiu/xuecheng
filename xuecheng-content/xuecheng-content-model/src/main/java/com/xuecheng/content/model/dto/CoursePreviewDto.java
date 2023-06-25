package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/24 14:18
 * @Description:课程预览模型类
 */
@Data
public class CoursePreviewDto {
    private CourseBaseInfoDto courseBase;
    private List<TeachplanDto> teachplans;
}
