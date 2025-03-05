package com.yizhi.training.application.v2.controller.student;

import com.yizhi.training.application.v2.service.biz.TpHomeBizService;
import com.yizhi.training.application.v2.vo.HotTpVO;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.OnLineTpVO;
import com.yizhi.training.application.v2.vo.request.SearchTpAndProVO;
import com.yizhi.training.application.vo.StuMemberResourceTpVo;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "项目主页")
@RestController
@RequestMapping("/v2/student/home")
public class TpHomeController {

    @Autowired
    private TpHomeBizService tpHomeBIzService;

    @ApiOperation("火热报名列表")
    @GetMapping("/hot/tp")
    public BizResponse<List<HotTpVO>> hotEnrollTp(
        @ApiParam(allowableValues = "MOBILE, PC") @RequestParam(defaultValue = "MOBILE") String terminalType) {
        List<HotTpVO> vos = tpHomeBIzService.hotEnrollTp(terminalType);
        return BizResponse.ok(vos);
    }

    @PostMapping("/online/tp")
    @ApiOperation("在线项目")
    public BizResponse<PageDataVO<OnlineTpVO>> onLineTp(@RequestBody OnLineTpVO tpVO) {
        PageDataVO<OnlineTpVO> page = tpHomeBIzService.getOnLineTpPage(tpVO);
        return BizResponse.ok(page);
    }

    @PostMapping("/member/online/tp")
    @ApiOperation("在线项目")
    public List<StuMemberResourceTpVo> memberOnLineTp(@RequestBody OnLineTpVO tpVO) {
        return tpHomeBIzService.memberOnLineTpList(tpVO);
    }

    @PostMapping("/search/tp")
    @ApiOperation("搜索")
    public BizResponse<PageDataVO<OnlineTpVO>> searchTp(@RequestBody SearchTpAndProVO searchVo) {
        PageDataVO<OnlineTpVO> page = tpHomeBIzService.searchTp(searchVo);
        return BizResponse.ok(page);
    }

}
