package com.yizhi.training.application.v2.vo;

import com.yizhi.util.application.sensitive.annotation.SensitiveWords;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TpCommentReplyVO implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目 - 评论id")
    private Long tpCommentId;

    private Long trainingProjectId;

    @SensitiveWords
    @ApiModelProperty(value = "回复内容")
    private String content;

    @ApiModelProperty(value = "创建者id")
    private Long createById;

    @ApiModelProperty(value = "创建者账号名称")
    private String createByName;

    @ApiModelProperty(value = "创建者真实名称")
    private String createByFullName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

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
}
