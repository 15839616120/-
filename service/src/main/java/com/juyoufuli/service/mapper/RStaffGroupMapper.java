package com.juyoufuli.service.mapper;

import com.juyoufuli.entity.RStaffGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RStaffGroupMapper {

    /**
     * 新增
     * @param record
     * @return
     */
    int insert(RStaffGroup record);

    /**
     * 修改
     * @param record
     * @return
     */
    int update(RStaffGroup record);

    /**
     * 逻辑删除
     * @param staffId 员工编号
     * @param groupId 组编号
     * @return
     */
    int delete(@Param("staffId") Integer staffId,@Param("groupId") Long groupId);

    /**
     * 根据员工Id查询
     * @param staffId
     * @return
     */
    List<Long> selectBystaffId(@Param("staffId") Integer staffId);
}