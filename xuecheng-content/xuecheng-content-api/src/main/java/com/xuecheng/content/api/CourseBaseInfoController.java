package com.xuecheng.content.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exeception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "课程信息管理接口",tags = "课程信息管理接口")
@RestController
@RequestMapping("/course")
public class CourseBaseInfoController {
    @Autowired
    private CourseBaseService courseBaseService;
    @ApiOperation("课程查询接口")
    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams params, @RequestBody(required = false) QueryCourseParamDto queryCourseParamDto){
        return courseBaseService.queryCourseBaseList(params, queryCourseParamDto);
    }
    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/{id}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long id){
        return courseBaseService.getCourseBaseInfo(id);
    }
    @ApiOperation(("新增课程基础信息"))
    @PostMapping
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated({ValidationGroups.Inster.class}) AddCourseDto addCourseDto){
        Long companId=1232141425L;
        return courseBaseService.createCourseBase(companId,addCourseDto);
    }
    @ApiOperation("修改课程基本信息")
    @PutMapping
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        Long companyId=1232141425L;
        return courseBaseService.updateCourseBase(companyId,editCourseDto);
    }
}
