package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表）
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TrainingProjectVo",
    description = "培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表）")
@TableName("training_project")
public class TrainingProject extends Model<TrainingProject> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目分类id")
    @TableField("tp_classification_id")
    private Long tpClassificationId;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "积分")
    private Integer point;

    @ApiModelProperty(value = "logo图片")
    @TableField("logo_img")
    private String logoImg;

    @ApiModelProperty(value = "学习项目开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "学习项目结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "项目状态（0：草稿、1：启用、2：停用）")
    private Integer status;

    @ApiModelProperty(value = "项目是否0：指定学员可见，1平台用户可见（创建人管理权限范围）")
    @TableField("visible_range")
    private Integer visibleRange;

    @ApiModelProperty(value = "是否删除（0：否，1：是）")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "关键字，英文逗号分隔")
    @TableField("key_words")
    private String keyWords;

    @ApiModelProperty(value = "项目介绍，富文本")
    private String description;

    @ApiModelProperty(value = "是否启用报名（0：否，1：是），默认否")
    @TableField("enable_enroll")
    private Integer enableEnroll;

    @ApiModelProperty(value = "是否启用签到（0：否，1：是），默认否")
    @TableField("enable_sign")
    private Integer enableSign;

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

    @TableField(value = "release_time")
    private Date releaseTime;

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

    @ApiModelProperty(value = "是否开启定位（0：否，1：是），默认否")
    @TableField("enable_position")
    private Integer enablePosition;

    /**
     * 不持久化
     */
    @ApiModelProperty(value = "培训计划集合")
    @TableField(exist = false)
    private List<TpPlan> plans;

    @ApiModelProperty(value = "是否启用在日历任务中显示")
    @TableField("enable_task")
    private Integer enableTask;

    @ApiModelProperty(value = "权重")
    private Integer weight;

    @ApiModelProperty(value = "开启付费的项目是否在项目列表中显示；默认1显示")
    @TableField("enable_queue")
    private Integer enableQueue;

    private Integer sort;

    private Integer publishTerminal;

    private Integer enableStatistics;

    private Integer enableHeadTeacher;

    private Integer enableMsgRemind;

    @TableField(exist = false)
    private Integer enablePay;

    @TableField(exist = false)
    private Integer payType;

    @ApiModelProperty(value = "是否显示项目介绍页（0：不显示 1：默认 显示）")
    @TableField("project_description_flag")
    private Integer projectDescriptionFlag;

    @ApiModelProperty(value = "项目AI助手地址")
    @TableField("tp_ai_url")
    private String tpAiUrl;

    @ApiModelProperty(value = "项目AI助手开关")
    @TableField("tp_ai_open")
    private Boolean tpAiOpen;

    @TableField(exist = false)
    private Integer joinNumber;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
