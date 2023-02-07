package com.xuecheng.content.content.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
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

    @Resource
    TeachplanMapper teachplanMapper;




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

    @ApiOperation("查询课程计划教师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> queryTeacher(@PathVariable Long courseId){
        LambdaQueryWrapper<CourseTeacher> qw = new LambdaQueryWrapper<>();
        qw.eq(CourseTeacher::getCourseId,courseId);

        return teacherMapper.selectList(qw);
    }

    @ApiOperation("新增或修改课程计划教师")
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


    @ApiOperation("删除课程计划章节")
    @DeleteMapping("/teachplan/{teachPlanId}")
    public void delete(@PathVariable Long teachPlanId){

        LambdaQueryWrapper<Teachplan> qw = new LambdaQueryWrapper<>();
        qw.eq(Teachplan::getParentid,teachPlanId);
        List<Teachplan> list = teachplanMapper.selectList(qw);
        if(list.size() != 0) {
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            return;
        }
        teachplanMapper.deleteById(teachPlanId);

    }

    @ApiOperation("上下移动课程计划章节")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveup(@PathVariable Long id){
        XueChengPlusException.cast("前端传参有问题,功能暂未实现");
    }


    @ApiOperation("上下移动课程计划章节")
    @PostMapping("/teachplan/movedown/{id}")
    public void movedown(@PathVariable Long id){
        XueChengPlusException.cast("前端传参有问题,功能暂未实现");
    }


}
