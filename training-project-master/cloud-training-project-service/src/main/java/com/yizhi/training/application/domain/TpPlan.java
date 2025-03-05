package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tp_plan")
public class TpPlan extends Model<TpPlan> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "学习计划名称")
    private String name;

    @ApiModelProperty(value = "学习计划开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "学习计划结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建者id")
    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @ApiModelProperty(value = "创建者名称")
    @TableField(value = "create_by_name", fill = FieldFill.INSERT)
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新者id")
    @TableField(value = "update_by_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateById;

    @ApiModelProperty(value = "修改者名称")
    @TableField(value = "update_by_name", fill = FieldFill.INSERT_UPDATE)
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "站点id")
    @TableField("site_id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    @TableField("org_id")
    private Long orgId;

    @ApiModelProperty(value = "企业id")
    @TableField("company_id")
    private Long companyId;

    @ApiModelProperty(value = "是否开启邮件提醒（0：否，1：是），默认否。")
    @TableField("enable_remind_mail")
    private Integer enableRemindMail;

    @ApiModelProperty(value = "是否开启app提醒（0：否，1：是），默认否。")
    @TableField("enable_remind_app")
    private Integer enableRemindApp;

    @ApiModelProperty(value = "邮件模板")
    @TableField("mail_remind_template_id")
    private Long mailRemindTemplateId;

    @ApiModelProperty(value = "app消息推送模板")
    @TableField("app_remind_template_id")
    private Long appRemindTemplateId;

    @ApiModelProperty(value = "提醒内容")
    @TableField("remind_content")
    private String remindContent;

    private Integer enableStudyInSequence;

    private Integer enableContinueStudy;

    private Long directoryItemId;

    @ApiModelProperty(value = "前置计划，查看用，不持久化")
    @TableField(exist = false)
    private List<TpPlanConditionPre> conditionPres;

    @ApiModelProperty(value = "完成条件，查看用，不持久化")
    @TableField(exist = false)
    private List<TpPlanConditionPost> conditionPosts;

    @ApiModelProperty(value = "提醒时间，查看用，不持久化(已废弃)")
    @TableField(exist = false)
    private List<TpPlanRemind> reminds;

    @ApiModelProperty(value = "条件，查看用，不持久化")
    @TableField(exist = false)
    private TpPlanConditionVo condition;

    /**
     * 不持久化
     */
    @ApiModelProperty(value = "活动集合")
    @TableField(exist = false)
    private List<TpPlanActivity> activities;

    /**
     * 不持久化
     */
    @TableField(exist = false)
    private Integer finishedActivityNums = 0;

    @ApiModelProperty(value = "提醒时间，查看用，不持久化(可用)")
    @TableField(exist = false)
    private TpRemindVo remindVo;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
