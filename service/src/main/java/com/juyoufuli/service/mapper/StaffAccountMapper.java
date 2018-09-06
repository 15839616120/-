package com.juyoufuli.service.mapper;

import com.juyoufuli.entity.StaffAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StaffAccountMapper {

    /**
     * 插入
     * @param record
     * @return
     */
    int insert(StaffAccount record);

    /**
     * 批量插入
     * @param accounts
     * @return
     */
    int insertBatch(@Param("accounts") List<StaffAccount> accounts);

    /**
     * 更新
     * @param record
     * @return
     */
    int update(StaffAccount record);

    /**
     * 根据工号查询帐号
     * @param staffId
     * @return
     */
    List<String> getAccountByStaffId(int staffId);

    /**
     * 根据工号查询帐号
     * @param staffId
     * @return
     */
    List<StaffAccount> selectByStaffId(@Param("staffId") int staffId);
}