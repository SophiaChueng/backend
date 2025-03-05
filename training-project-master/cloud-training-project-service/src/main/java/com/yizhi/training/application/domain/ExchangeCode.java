package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 兑换码
 * </p>
 *
 * @author xiaoyu
 * @since 2021-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("exchange_code")
@ApiModel(value = "ExchangeCode对象", description = "兑换码")
public class ExchangeCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    @ApiModelProperty(value = "关联的项目/活动等等的ID")
    @TableField("ref_id")
    private Long refId;

    @ApiModelProperty(value = "关联的项目/活动等等的类型")
    @TableField("ref_type")
    private Integer refType;

    @ApiModelProperty(value = "兑换码")
    @TableField("code")
    private String code;

    @ApiModelProperty(value = "状态 0：未使用，1：已使用")
    @TableField("state")
    private Integer state;

    @ApiModelProperty(value = "兑换时间")
    @TableField("exchange_time")
    private Date exchangeTime;

    @ApiModelProperty(value = "兑换人ID")
    @TableField("account_id")
    private Long accountId;

    @TableField("org_id")
    private Long orgId;

    @TableField("site_id")
    private Long siteId;

    @TableField("company_id")
    private Long companyId;

    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @TableField(value = "create_by_name", fill = FieldFill.INSERT)
    private String createByName;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_by_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateById;

    @TableField(value = "update_by_name", fill = FieldFill.INSERT_UPDATE)
    private String updateByName;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
