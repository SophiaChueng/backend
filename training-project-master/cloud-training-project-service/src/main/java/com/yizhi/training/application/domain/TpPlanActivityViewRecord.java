package com.yizhi.training.application.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TpPlanActivityViewRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户id
     */
    private Long accountId;

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 用户所在部门id
     */
    private Long orgId;

    /**
     * 发生时间
     */
    private Date time;

    /**
     * 培训项目 id
     */
    private Long trainingProjectId;

    /**
     * 点击的活动
     */
    private Long tpPlanActivityId;

    /**
     * 活动关联的业务
     */
    private Long tpPlanActivityRelationId;

    /**
     * 业务类型
     */
    private Integer tpPlanActivityType;

}
