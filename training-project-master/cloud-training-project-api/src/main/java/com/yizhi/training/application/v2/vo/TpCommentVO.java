package com.yizhi.training.application.v2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TpCommentVO implements Serializable {

    @ApiModelProperty(value = "回复列表")
    List<TpCommentReplyVO> tpCommentReplies;

    private Long id;

    @ApiModelProperty(value = "创建者id")
    private Long createById;

    @ApiModelProperty(value = "创建者账号名称")
    private String createByName;

    @ApiModelProperty(value = "创建者真实名称")
    private String createByFullName;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "评论对象")
    private String replyName;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "点赞数")
    private Integer thumbsUpCount;

    @ApiModelProperty(value = "是否已经点赞")
    private Boolean hasThumbsUp;

    @ApiModelProperty(value = "回复数")
    private Integer replyCount;

    @ApiModelProperty("状态(0：上架|1：下架  默认为0)")
    private Integer state;

}
