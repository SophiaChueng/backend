package com.yizhi.training.application.v2.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchTpConditionBO {

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 站点id
     */
    private Long siteId;

    /**
     * 筛选部门id
     */
    private List<Long> orgIds;

    /**
     * 筛选项目分类id
     */
    private Long tpClassificationId;

    /**
     * 筛选项目id
     */
    private List<Long> trainingProjectIds;

    /**
     * 筛选项目名称
     */
    private String name;

    /**
     * 筛选项目可见范围
     */
    private Integer visibleRange;

    private Integer enableEnroll;

    private Integer enablePay;

    private Integer status;

    /**
     * 项目进行状态（null:全部， 1：未开始， 2： 进行中， 3：已结束）
     *
     * @see com.yizhi.training.application.v2.enums.TpProcessStatusEnum
     */
    private Integer processStatus;

    /**
     * 筛选项目进行状态所需参数
     */
    private Date current;

    private Integer offset;

    private Integer pageSize;
}
