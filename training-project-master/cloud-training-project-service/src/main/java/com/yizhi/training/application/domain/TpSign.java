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
@TableName("tr_sign")
public class TpSign extends Model<TpSign> {

    private Long id;

    private Long trainingProjectId;

    private String name;

    private Integer type;

    private Integer enableRetroactive;

    private Integer enableRemindMail;

    private Integer enableRemindNote;

    private String enableRemindApp;

    private Long mailRemindTemplateId;

    private Long appRemindTemplateId;

    private String remindContent;

    private Long companyId;

    private Long siteId;

    private Long orgId;

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

    @TableLogic
    private Integer state;

    private Integer remindTime;

    private Integer enablePosition;

    private String address;
    @TableField("`range`")
    private String range;

    @Override
    public Serializable pkVal() {
        return id;
    }
}
