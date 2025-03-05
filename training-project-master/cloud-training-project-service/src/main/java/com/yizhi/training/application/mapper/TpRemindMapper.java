package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpRemind;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 培训项目提醒 物理删除 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TpRemindMapper extends BaseMapper<TpRemind> {

    /**
     * 批量插入提醒
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpRemind> list);

    /**
     * 根据培训项目id批量删除
     *
     * @param tpIds
     * @return
     */
    Integer batchDeleteByTpIds(@Param("tpIds") List<Long> tpIds);

}
