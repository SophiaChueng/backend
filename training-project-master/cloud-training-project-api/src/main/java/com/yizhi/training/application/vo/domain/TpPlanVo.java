package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.yizhi.training.application.vo.manage.TpPlanConditionVo;
import com.yizhi.training.application.vo.manage.TpRemindVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 培训项目 - 学习计划
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpPlanVo", description = "培训项目 - 学习计划")
public class TpPlanVo extends Model<TpPlanVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "学习计划名称")
    private String name;

    @ApiModelProperty(value = "学习计划开始时间")
    private Date startTime;

    @ApiModelProperty(value = "学习计划结束时间")
    private Date endTime;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @ApiModelProperty(value = "创建者id")
    private Long createById;

    @ApiModelProperty(value = "创建者名称")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新者id")
    private Long updateById;

    @ApiModelProperty(value = "修改者名称")
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    private Long orgId;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    @ApiModelProperty(value = "是否开启邮件提醒（0：否，1：是），默认否。")
    private Integer enableRemindMail;

    @ApiModelProperty(value = "是否开启app提醒（0：否，1：是），默认否。")
    private Integer enableRemindApp;

    @ApiModelProperty(value = "邮件模板")
    private Long mailRemindTemplateId;

    @ApiModelProperty(value = "app消息推送模板")
    private Long appRemindTemplateId;

    @ApiModelProperty(value = "提醒内容")
    private String remindContent;

    @ApiModelProperty(value = "前置计划，查看用，不持久化")
    private List<TpPlanConditionPreVo> conditionPres;

    @ApiModelProperty(value = "完成条件，查看用，不持久化")
    private List<TpPlanConditionPostVo> conditionPosts;

    @ApiModelProperty(value = "提醒时间，查看用，不持久化(已废弃)")
    private List<TpPlanRemindVo> reminds;

    @ApiModelProperty(value = "条件，查看用，不持久化")
    private TpPlanConditionVo condition;

    /**
     * 不持久化
     */
    @ApiModelProperty(value = "活动集合")
    private List<TpPlanActivityVo> activities;

    /**
     * 报错信息
     */
    private String subMsg;

    /**
     * 不持久化
     */
    private Integer finishedActivityNums = 0;

    @ApiModelProperty(value = "提醒时间，查看用，不持久化(可用)")
    private TpRemindVo remindVo;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
