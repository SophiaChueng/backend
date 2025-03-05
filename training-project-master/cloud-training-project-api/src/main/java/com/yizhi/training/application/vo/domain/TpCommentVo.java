package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
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
public class TpCommentVo extends Model<TpCommentVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "审核状态：0通过，1下架（默认通过）")
    private String auditStatus;

    @ApiModelProperty(value = "下架人id")
    private Long auditorId;

    @ApiModelProperty(value = "下架意见")
    private String auditContent;

    @ApiModelProperty(value = "下架时间")
    private Date auditorTime;

    @ApiModelProperty(value = "创建者id")
    private Long createById;

    @ApiModelProperty(value = "创建者名称")
    private String createByName;

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

    @ApiModelProperty("状态(0：上架|1：下架  默认为0)")
    private Integer state;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
