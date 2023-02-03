package com.xuecheng.content;

import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ContentServiceApplicationTests {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Test
    void contextLoads() {

        CourseBase courseBase = courseBaseMapper.selectById(22);
        System.out.println(courseBase);

    }

}
