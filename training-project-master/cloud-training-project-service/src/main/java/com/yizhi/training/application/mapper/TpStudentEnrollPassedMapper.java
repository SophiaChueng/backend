package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpStudentEnrollPassed;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 学生参与培训项目记录（只针对需要报名的培训项目，不需要报名的该表不记录） Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-28
 */
public interface TpStudentEnrollPassedMapper extends BaseMapper<TpStudentEnrollPassed> {

    /**
     * 查询已经报名的培训项目id
     *
     * @param accountId
     * @return
     */
    @Select("select training_project_id from tp_student_enroll_passed " + "where account_id = #{accountId}")
    List<Long> selectTpIds(@Param("accountId") Long accountId);

    /**
     * 查询培训项目是否报名通过
     *
     * @param accountId
     * @return
     */
    @Select(
        "select id from tp_student_enroll_passed " + "where account_id = #{accountId} and site_id = #{siteId} and " + "training_project_id = #{tpProjrctId}")
    Long selectTpIdByCondition(@Param("tpProjrctId") Long tpProjrctId, @Param("accountId") Long accountId,
        @Param("siteId") Long siteId);
}
