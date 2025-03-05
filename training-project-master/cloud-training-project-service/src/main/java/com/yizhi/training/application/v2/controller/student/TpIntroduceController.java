package com.yizhi.training.application.v2.controller.student;

import com.yizhi.training.application.v2.service.biz.TpIntroduceBizServices;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import com.yizhi.training.application.v2.vo.TpIntroduceVO;
import com.yizhi.training.application.v2.vo.TpStatusDetailsVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "项目介绍页")
@RestController
@RequestMapping("/v2/student/introduce")
public class TpIntroduceController {

    @Autowired
    private TpIntroduceBizServices tpIntroduceBizServices;

    @GetMapping("/details")
    @ApiOperation("项目状态及详情")
    public BizResponse<TpStatusDetailsVO> getStatusDetails(@RequestParam @ApiParam("项目ID") Long tpId,
        @RequestParam @ApiParam("0:普通项目,1:项目PRO") Integer tpType) {
        TpStatusDetailsVO vo = tpIntroduceBizServices.getStatusDetails(tpId, tpType);
        return BizResponse.ok(vo);
    }

    @GetMapping("/tplist/pro")
    @ApiOperation("项目PRO中项目的列表")
    public BizResponse<List<OnlineTpVO>> getTpListByProId(@RequestParam Long tpProId) {
        List<OnlineTpVO> list = tpIntroduceBizServices.getTpListByProId(tpProId);
        return BizResponse.ok(list);
    }

    @GetMapping("/item/details")
    public BizResponse<List<TpIntroduceVO>> getIntroduceItemDetails(@RequestParam Long tpId) {
        List<TpIntroduceVO> items = tpIntroduceBizServices.getIntroduceItemDetails(tpId);
        return BizResponse.ok(items);
    }

}
