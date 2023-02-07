package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Resource
    TeachplanMapper teachplanMapper;


    @Override
    public List<TeachplanDto> findTeachplayTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }
}
