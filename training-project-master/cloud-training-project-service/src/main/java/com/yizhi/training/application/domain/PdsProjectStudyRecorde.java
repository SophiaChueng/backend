package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * pds 自定义项目 学习记录
 * </p>
 *
 * @author fulan123
 * @since 2022-05-18
 */
@Data
@ApiModel(value = "PdsProjectStudyRecorde", description = "pds 自定义项目 学习记录")
@TableName("pds_project_study_recorde")
public class PdsProjectStudyRecorde {

    private static final long serialVersionUID = 1L;

    // 联合主键： 1. 将@TableId都改为@TableFiled，并将主键字段加上@MppMultiId注解。 2. 最后，记得项目的运行类需要加上@EnableMPP，开启MPP
    // 参考：https://blog.csdn.net/qq_37922457/article/details/123417942  https://blog.csdn
    // .net/thethefighter/article/details/124083484
    @ApiModelProperty(value = "用户id")
    //@MppMultiId
    //@TableField("uid")
    private Long uid;

    @ApiModelProperty(value = "项目id")
    //@MppMultiId
    //@TableField("pid")
    private Long pid;

    @ApiModelProperty(value = "存储点的内容")
    @TableField("point_context")
    private String pointContext;

    @ApiModelProperty(value = "用户姓名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "学习时长")
    private Float period;

    @ApiModelProperty(value = "头像")
    @TableField("head_portrait")
    private String headPortrait;

    @ApiModelProperty(value = "数据创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "数据更新时间")
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

}
