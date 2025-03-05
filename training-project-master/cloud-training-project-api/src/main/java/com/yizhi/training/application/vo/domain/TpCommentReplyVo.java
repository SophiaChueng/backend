package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class TpCommentReplyVo extends Model<TpCommentReplyVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目 - 评论id")
    private Long tpCommentId;

    @TableField("training_project_id")
    private Long trainingProjectId;

    @SensitiveWords
    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "删除状态：0未删除，1已删除")
    private String auditStatus;

    @ApiModelProperty(value = "删除人id")
    private Long auditorId;

    @ApiModelProperty(value = "删除时间")
    private Date auditorTime;

    @ApiModelProperty(value = "创建者id")
    private Long createById;

    @ApiModelProperty(value = "创建者账号名称")
    private String createByName;

    @ApiModelProperty(value = "创建者真实名称")
    private String createByFullName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    private Long orgId;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "回复用户父id")
    private Long parentAccountId;

    @ApiModelProperty("回复用户父姓名")
    private String parentAccountFullName;

    @ApiModelProperty(value = "回复用户父账号名称")
    private String parentAccountName;

    @ApiModelProperty("评论回复父Id(若无默认为null)")
    private Long replyParentId;

    @ApiModelProperty("上架状态：0上架（默认）1下架")
    private Integer state;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
