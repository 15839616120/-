package com.juyoufuli.service.mapper;

import com.juyoufuli.entity.StaffInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StaffInfoMapper {

    /**
     * 插入
     * @param record
     * @return
     */
    int insert(StaffInfo record);

    /**
     * 更新
     * @param record
     * @return
     */
    int update(StaffInfo record);

    /**
     * 查询
     * @param staffIds
     * @return
     */
    List<StaffInfo> selectByIds(@Param("staffIds") String[] staffIds);

    /**
     * 根据编号查询总数
     * @param staffId
     * @return
     */
    int selectCount(@Param("staffId") Integer staffId);

    /**
     * 根据编号查询员工
     * @param staffId
     * @return
     */
    StaffInfo selectById(@Param("staffId") Integer staffId);

    /**
     * 分页查询
     * @param start 开始
     * @param end 结束
     * @return
     */
    List<StaffInfo> selectLimit(@Param("start")int start, @Param("end")int end);
}