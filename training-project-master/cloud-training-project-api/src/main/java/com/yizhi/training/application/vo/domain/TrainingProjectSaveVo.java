package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Api(tags = "TrainingProjectSaveVo",
    description = "培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表）")
public class TrainingProjectSaveVo extends Model<TrainingProjectSaveVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目分类id")
    private Long tpClassificationId;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "积分")
    private Integer point;

    @ApiModelProperty(value = "logo图片")
    private String logoImg;

    @ApiModelProperty(value = "学习项目开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "学习项目结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "项目状态（0：草稿、1：启用、2：停用）")
    private Integer status;

    @ApiModelProperty(value = "项目是否0：指定学员可见，1平台用户可见（创建人管理权限范围）")
    private Integer visibleRange;

    @ApiModelProperty(value = "是否删除（0：否，1：是）")
    private Integer deleted;

    @ApiModelProperty(value = "关键字，英文逗号分隔")
    private String keyWords;

    @ApiModelProperty(value = "项目介绍，富文本")
    private String description;

    @ApiModelProperty(value = "是否启用报名（0：否，1：是），默认否")
    private Integer enableEnroll;

    @ApiModelProperty(value = "是否启用签到（0：否，1：是），默认否")
    private Integer enableSign;

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

    private Date releaseTime;

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

    @ApiModelProperty(value = "是否开启定位（0：否，1：是），默认否")
    private Integer enablePosition;

    @ApiModelProperty(value = "开启付费的项目是否在项目列表中显示；默认true显示")
    private Integer enableQueue;

    /**
     * 不持久化
     */
    @ApiModelProperty(value = "培训计划集合")
    private List<TpPlanVo> plans;

    @ApiModelProperty(value = "是否启用在日历任务中显示")
    private Integer enableTask;

    @ApiModelProperty(value = "权重")
    private Integer weight;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
