package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.yizhi.util.application.sensitive.annotation.SensitiveWords;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 培训项目 - 评论
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpCommentVo", description = "培训项目 - 评论")
@TableName("tp_comment")
public class TpComment extends Model<TpComment> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @SensitiveWords
    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "审核状态：0通过，1下架（默认通过）")
    @TableField("audit_status")
    private String auditStatus;

    @ApiModelProperty(value = "下架人id")
    @TableField("auditor_id")
    private Long auditorId;

    @ApiModelProperty(value = "下架意见")
    @TableField("audit_content")
    private String auditContent;

    @ApiModelProperty(value = "下架时间")
    @TableField("auditor_time")
    private Date auditorTime;

    @ApiModelProperty(value = "创建者id")
    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @ApiModelProperty(value = "创建者名称")
    @TableField(value = "create_by_name", fill = FieldFill.INSERT)
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "站点id")
    @TableField("site_id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    @TableField("org_id")
    private Long orgId;

    @ApiModelProperty(value = "企业id")
    @TableField("company_id")
    private Long companyId;

    @ApiModelProperty(value = "用户头像")
    @TableField("user_avatar")
    private String userAvatar;

    @ApiModelProperty("状态(0：上架|1：下架  默认为0)")
    @TableField("state")
    private Integer state;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
