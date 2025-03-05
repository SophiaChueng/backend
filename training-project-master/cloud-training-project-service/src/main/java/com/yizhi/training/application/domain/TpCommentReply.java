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
 * 培训项目 - 评论回复
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpCommentReplyVo", description = "培训项目 - 评论回复")
@TableName("tp_comment_reply")
public class TpCommentReply extends Model<TpCommentReply> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目 - 评论id")
    @TableField("tp_comment_id")
    private Long tpCommentId;

    @TableField("training_project_id")
    private Long trainingProjectId;

    @SensitiveWords
    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "删除状态：0未删除，1已删除")
    @TableField("audit_status")
    private String auditStatus;

    @ApiModelProperty(value = "删除人id")
    @TableField("auditor_id")
    private Long auditorId;

    @ApiModelProperty(value = "删除时间")
    @TableField("auditor_time")
    private Date auditorTime;

    @ApiModelProperty(value = "创建者id")
    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @ApiModelProperty(value = "创建者账号名称")
    @TableField(value = "create_by_name", fill = FieldFill.INSERT)
    private String createByName;

    @ApiModelProperty(value = "创建者真实名称")
    @TableField(value = "create_by_full_name")
    private String createByFullName;

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

    @ApiModelProperty(value = "回复用户父id")
    @TableField("parent_account_id")
    private Long parentAccountId;

    @ApiModelProperty("回复用户父姓名")
    @TableField("parent_account_fullName")
    private String parentAccountFullName;

    @ApiModelProperty(value = "回复用户父账号名称")
    @TableField("parent_account_name")
    private String parentAccountName;

    @ApiModelProperty("评论回复父Id(若无默认为null)")
    @TableField("reply_parent_id")
    private Long replyParentId;

    @ApiModelProperty("上架状态：0上架（默认）1下架")
    @TableField("state")
    private Integer state;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
