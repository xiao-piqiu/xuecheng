package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exeception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamDto queryCourseParamDto) {
        LambdaQueryWrapper<CourseBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(queryCourseParamDto.getCourseName()),CourseBase::getName,queryCourseParamDto.getCourseName());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamDto.getAuditStatus());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamDto.getPublishStatus());
        Page<CourseBase> courseBasePage = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(courseBasePage, lambdaQueryWrapper);
        return new PageResult<CourseBase>(pageResult.getRecords(), pageResult.getTotal(),pageParams.getPageNo(), pageParams.getPageSize());
    }
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto dto) {
        if (StringUtils.isBlank(dto.getName())) {
            throw new XueChengPlusException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new XueChengPlusException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new XueChengPlusException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new XueChengPlusException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new XueChengPlusException("收费规则为空");
        }
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        int insert=courseBaseMapper.insert(courseBase);
        if(insert<=0){
            throw new XueChengPlusException("新增课程基本信息失败");
        }
        CourseMarket courseMarket = new CourseMarket();
        Long courseBaseId = courseBase.getId();
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(courseBaseId);
        int i=saveCourseMarket(courseMarket);
        if(i<=0){
            throw new XueChengPlusException("保存课程营销信息失败");
        }
        return getCourseBaseInfo(courseBaseId);
    }
    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            XueChengPlusException.cast("课程不存在");
        }
        if(!courseBase.getCompanyId().equals(courseId)){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }
        //封装基本信息的数据
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        //更新课程基本信息
        courseBaseMapper.updateById(courseBase);
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        return this.getCourseBaseInfo(courseId);
    }

    public CourseBaseInfoDto getCourseBaseInfo(Long courseBaseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseBaseId);
        if (courseBase==null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseBaseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //设置分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new XueChengPlusException("收费规则没有选择");
        }
        if(charge.equals("201001")){
            if(courseMarket.getPrice()==null || courseMarket.getPrice().floatValue()<=0){
                throw new XueChengPlusException("课程为收费价格不能为空且必须大于0");
            }
        }
        //判断表中是否有此数据
        CourseMarket market = courseMarketMapper.selectById(courseMarket.getId());
        //没有数据--插入数据
        if(market==null){
            return courseMarketMapper.insert(courseMarket);
        //有数据--更新数据
        }else {
            BeanUtils.copyProperties(courseMarket,market);
            market.setId(courseMarket.getId());
            return courseMarketMapper.updateById(market);
        }
    }
}
