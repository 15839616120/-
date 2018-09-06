package com.juyoufuli.service.mapper;

import com.juyoufuli.entity.StaffGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Mapper
@Component
public interface StaffGroupMapper {
    int deleteByPrimaryKey(Long groupId);
    
    List<StaffGroup> sortStaffList();
    
    int insert(StaffGroup record);

    int insertSelective(StaffGroup record);

    StaffGroup selectByPrimaryKey(Long groupId);

    int updateByPrimaryKey(StaffGroup record);

	int updateByPrimaryKeySelective(Map<String, Object> map);

    List<StaffGroup> selectByIds(@Param("groupIds") Long[] groupIds);

    int selectCount();

    List<StaffGroup> selectAll();

    List<StaffGroup> selectLimit(@Param("start")int start, @Param("end")int end);

    long selectMaxId();
}