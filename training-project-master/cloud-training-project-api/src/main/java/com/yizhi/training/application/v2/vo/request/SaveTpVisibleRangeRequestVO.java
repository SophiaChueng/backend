package com.yizhi.training.application.v2.vo.request;

import com.yizhi.training.application.v2.vo.TpVisibleRangeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "保存项目可见范围入参")
public class SaveTpVisibleRangeRequestVO implements Serializable {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("项目可见范围列表")
    private List<TpVisibleRangeVO> visibleRanges;
}
