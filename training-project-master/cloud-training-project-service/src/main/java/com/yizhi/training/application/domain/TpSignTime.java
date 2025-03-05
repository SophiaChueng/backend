package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tr_sign_time")
public class TpSignTime extends Model<TpSignTime> {

    private Long id;

    private Long companyId;

    private Long siteId;

    private Long trainingProjectId;

    private Long signId;

    private Date startTime;

    private Date endTime;

    @TableLogic
    private Integer deleted;

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

    private Integer enablePosition;

    private String address;
    @TableField("`range`")
    private String range;

    private Integer point;

    private String code;

    private Double longitude;

    private Double latitude;

    @Override
    public Serializable pkVal() {
        return id;
    }
}
