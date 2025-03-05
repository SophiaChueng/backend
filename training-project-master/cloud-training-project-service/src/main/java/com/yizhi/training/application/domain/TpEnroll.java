package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tr_enroll")
public class TpEnroll extends Model<TpEnroll> {

    private Long id;

    private Long trainingProjectId;

    private Date startTime;

    private Date endTime;

    private Integer personLimitNum;

    private Integer needAudit;

    private String notice;

    @TableField(fill = FieldFill.INSERT)
    private Long createById;

    @TableField(fill = FieldFill.INSERT)
    private String createByName;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateById;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateByName;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private Long companyId;

    private Long orgId;

    private Long siteId;

    private Integer enablePay;

    private Integer actualPrice;

    // 原价可以设置为null
    @TableField(insertStrategy = FieldStrategy.ALWAYS, updateStrategy = FieldStrategy.ALWAYS)
    private Integer originalPrice;

    @ApiModelProperty("付费方式 0：非付费，1：虚拟币，2：兑换码，3：虚拟币/兑换码 4.会员")
    private Integer payType;

    @Override
    public Serializable pkVal() {
        return id;
    }
}
