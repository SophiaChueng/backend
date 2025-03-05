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
@TableName("tp_study_directory")
public class TpStudyDirectory extends Model<TpStudyDirectory> {

    private Long id;

    private Long companyId;

    private Long siteId;

    private Long trainingProjectId;

    /**
     * 学习页目录项类型（0：学习单元，1：简介，2：资料，3：评论，4：考试与作业，5：公告，6：讨论，7：富文本）
     */
    private Integer itemType;

    /**
     * 目录项名称
     */
    private String itemName;

    /**
     * 目录项排序
     */
    private Integer sort;

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
