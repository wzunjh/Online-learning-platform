package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.base.model.PageParams;
import com.xuecheng.content.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketMapper courseMarketMapper;

    @Resource
    CourseCategoryMapper courseCategoryMapper;


    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        //根据课程名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());

        //课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());

        //课程发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        //分页参数
        Page<CourseBase> page = new Page<>(params.getPageNo(),params.getPageSize());


        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //获取数据
        List<CourseBase> records = pageResult.getRecords();
        //总条数
        long total = pageResult.getTotal();

        return new PageResult<>(records, total, params.getPageNo(), params.getPageSize());
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

//        //合法性校验
//        if (StringUtils.isBlank(dto.getName())) {
//            XueChengPlusException.cast("课程名称为空");
//        }
//        if (StringUtils.isBlank(dto.getMt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//        if (StringUtils.isBlank(dto.getSt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//        if (StringUtils.isBlank(dto.getGrade())) {
//            XueChengPlusException.cast("课程等级为空");
//        }
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            XueChengPlusException.cast("教育模式为空");
//        }
//        if (StringUtils.isBlank(dto.getUsers())) {
//            XueChengPlusException.cast("适应人群为空");
//        }
//        if (StringUtils.isBlank(dto.getCharge())) {
//            XueChengPlusException.cast("收费规则为空");
//        }

        CourseBase courseBase = new CourseBase();

        BeanUtils.copyProperties(dto,courseBase);

        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");

        int insert = courseBaseMapper.insert(courseBase);

        Long courseId = courseBase.getId();

        CourseMarket courseMarket = new CourseMarket();

        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(courseId);
        //如果课程收费
        String charge = dto.getCharge();
        if (charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice() <=0){
                XueChengPlusException.cast("课程为收费,价格必须输入且大于0");
            }
        }

        int insert1 = courseMarketMapper.insert(courseMarket);

        if (insert<=0||insert1<=0){
            XueChengPlusException.cast("添加课程失败");
        }

        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        return courseBaseInfo;
    }


    public CourseBaseInfoDto getCourseBaseInfo(long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
//查询分类名称
        CourseCategory courseCategoryBySt =
                courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt =
                courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;
    }
}
