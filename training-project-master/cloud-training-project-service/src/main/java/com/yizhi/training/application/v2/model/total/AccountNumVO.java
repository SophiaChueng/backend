package com.yizhi.training.application.v2.model.total;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AccountNumVO {

    @ApiModelProperty("班级总人数")
    private Integer totalAccountCount = 0;

    @ApiModelProperty("已完成人数")
    private Integer finishedAccountCount = 0;

    @ApiModelProperty("学习中人数")
    private Integer learningAccountCount = 0;

    @ApiModelProperty("未学习人数（总人数-已完成-学习中人数）")
    private Integer unStartAccountCount = 0;

    public void setUnStartAccountCount(Integer unStartAccountCount) {
        Integer unStart = this.totalAccountCount - finishedAccountCount - learningAccountCount;
        if (unStart < 0) {
            this.unStartAccountCount = 0;
        } else {
            this.unStartAccountCount = unStart;
        }
    }
}
