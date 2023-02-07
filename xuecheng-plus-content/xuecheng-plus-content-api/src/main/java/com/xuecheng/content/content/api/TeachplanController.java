package com.xuecheng.content.content.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.content.service.impl.CourseTeacherServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Api(value = "课程计划管理相关接口",tags = "课程计划管理相关接口")
public class TeachplanController {

    @Resource
    TeachplanService teachplanService;

    @Resource
    CourseTeacherMapper teacherMapper;

    @Resource
    CourseTeacherServiceImpl courseTeacherService;


    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType
            = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplayTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> queryTeacher(@PathVariable Long courseId){
        LambdaQueryWrapper<CourseTeacher> qw = new LambdaQueryWrapper<>();
        qw.eq(CourseTeacher::getCourseId,courseId);

        return teacherMapper.selectList(qw);
    }

    @PostMapping("/courseTeacher")
    public CourseTeacher save(@RequestBody CourseTeacher courseTeacher){
        Long id = courseTeacher.getId();
        if (id == null) {
            courseTeacher.setCreateDate(LocalDateTime.now());
        }

        boolean b = courseTeacherService.saveOrUpdate(courseTeacher);
        if(!b){
            XueChengPlusException.cast("网络问题,保存失败");
        }
        return courseTeacher;

    }
}
