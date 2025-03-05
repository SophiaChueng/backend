package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpViewRecord;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.v2.MyTpUnFinishedVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
public interface TpViewRecordMapper extends BaseMapper<TpViewRecord> {

    /**
     * @param trainingProject
     * @return
     */
    @Select(
        "select count(1) from " + "(select tb.id " + "from tp_view_record tb " + "where tb.training_project_id = " +
            "#{trainingProject.id} " + "and tb.company_id = #{trainingProject.companyId} " + "and tb.site_id = " +
            "#{trainingProject.siteId} " + "group by tb.account_id) tb")
    Integer getViewNum(@Param("trainingProject") TrainingProject trainingProject);

    /**
     * @param trainingProject
     * @return
     */
    @Select(
        "SELECT count(1) from (select tb.account_id\n" + "from (\n" + "select tb.account_id,tb.org_id\n" + "from " +
            "tp_view_record tb \n" + "where tb.training_project_id = #{trainingProject.id} \n" + "and tb.company_id " + "=" + " #{trainingProject.companyId}\n" + "and tb.site_id = #{trainingProject.siteId} \n" + "group by tb" + ".account_id) tb\n" + "left join tp_authorization_range t on tb.account_id=t.relation_id \n" + "where t" + ".biz_id = #{trainingProject.id} \n" + "GROUP BY tb.account_id\n" + "UNION ALL \n" + "select tb" + ".account_id\n" + "from (\n" + "select tb.account_id,tb.org_id\n" + "from tp_view_record tb \n" + "where " + "tb.training_project_id = #{trainingProject.id} \n" + "and tb.company_id = #{trainingProject" + ".companyId}\n" + "and tb.site_id = #{trainingProject.siteId} \n" + "group by tb.account_id) tb\n" + "left join tp_authorization_range t on tb.org_id=t.relation_id \n" + "where t.biz_id = #{trainingProject" + ".id}\n" + "GROUP BY tb.account_id) a")
    Integer getViewNumRange(@Param("trainingProject") TrainingProject trainingProject);

    List<MyTpUnFinishedVO> getTpProMaxViewTime(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds, @Param("tpProIdList") List<Long> tpProIdList);

    List<MyTpUnFinishedVO> getTpProMaxEnrollTime(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds, @Param("tpProIdList") List<Long> tpProIdList);

    Integer getLearningCount(@Param("tpId") Long tpId, @Param("companyId") Long companyId,
        @Param("siteId") Long siteId);
}
