package com.yizhi.training.application.v2.vo.study;

import com.yizhi.training.application.v2.vo.TpIntroduceLecturerVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TpStudyIntroduceVO {

    @ApiModelProperty("简介富文本")
    private String content;

    private List<TpIntroduceLecturerVO> lecturers;

    @ApiModelProperty("展示班主任统计按钮")
    private Boolean showTotal = false;

    @ApiModelProperty("展示学员个人统计按钮")
    private Boolean showPersonTotal = false;

}
