package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itcast
 * @since 2022-10-07
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> getTeacherInfo(Long courseId);

    CourseTeacher addAndUpdateTeacherInfo(CourseTeacher courseTeacher);

    void deleteTeacherInfo(Long courseId, Long teacherId);
}
