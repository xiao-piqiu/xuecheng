package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/18 17:55
 * @Description:
 */
@RestController
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;
    @ApiOperation("查询课程教师信息")
    @GetMapping("courseTeacher/list/{courseId}")
    public List<CourseTeacher> selectTeacherInfo(@PathVariable Long courseId){
        return courseTeacherService.getTeacherInfo(courseId);
    }
    @ApiOperation("修改课程教师信息")
    @PostMapping("courseTeacher")
    public CourseTeacher addAndUpdateTeacherInfo(@RequestBody CourseTeacher courseTeacher){
        return courseTeacherService.addAndUpdateTeacherInfo(courseTeacher);
    }
    @ApiOperation("删除课程教师信息")
    @DeleteMapping("courseTeacher/course/{courseId}}/{teacherId}")
    public void updataTeacherInfo(@PathVariable Long courseId,@PathVariable Long teacherId){
        courseTeacherService.deleteTeacherInfo(courseId,teacherId);
    }
}
