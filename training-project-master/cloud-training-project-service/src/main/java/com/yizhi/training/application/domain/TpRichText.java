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
@TableName("tp_rich_text")
public class TpRichText extends Model<TpRichText> {

    private Long id;

    private Long companyId;

    private Long siteId;

    private Long trainingProjectId;

    private Integer directoryType;

    private Long directoryItemId;

    private String title;

    private String content;

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
    private Boolean deleted;

    @Override
    public Serializable pkVal() {
        return id;
    }
}
