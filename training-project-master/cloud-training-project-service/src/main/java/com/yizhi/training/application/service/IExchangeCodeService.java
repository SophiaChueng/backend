package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.core.application.exception.CustomException;
import com.yizhi.training.application.domain.ExchangeCode;
import com.yizhi.training.application.enums.TrainingCommonEnums;
import com.yizhi.training.application.qo.ExchangeCodeQO;
import com.yizhi.training.application.vo.ExchangeCodeExportVO;
import com.yizhi.training.application.vo.ExchangeCodeVO;
import com.yizhi.training.application.vo.api.ExchangeTrainingProjectVO;
import com.yizhi.util.application.page.PageInfo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 兑换码 服务类
 * </p>
 *
 * @author xiaoyu
 * @since 2021-05-14
 */
public interface IExchangeCodeService extends IService<ExchangeCode> {

    @Override
    default ExchangeCode getOne(Wrapper<ExchangeCode> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 已生成的激活码数量
     *
     * @param tpId
     * @return
     */
    public Integer codeCount(Long tpId, Integer type);

    /**
     * 生成指定数量激活码
     *
     * @param num
     * @return
     */
    public void createCode(Long tpId, Integer type, Integer num);

    public PageInfo<ExchangeCodeVO> listPage(ExchangeCodeQO qo);

    public List<ExchangeCodeExportVO> listExport(ExchangeCodeQO qo, Long siteId);

    public String batchDelete(List<Long> ids);

    CustomException codeExchangeTraining(String exchangeCode, Long tpId) throws BizException;

    CustomException<String> codeExchangeCourse(String exchangeCode, Long courseId) throws BizException;

    CustomException<PageInfo<ExchangeTrainingProjectVO>> getExchangeTrainingList(Integer pageNo, Integer pageSize,
        Integer type);

    Boolean userRelationExchange(Long relationId, Long accountId);

    /**
     * 获取学员兑换记录
     *
     * @param type
     * @return
     * @see TrainingCommonEnums
     */
    List<Long> getEnrollRelationIdsByAccountId(Integer type);

    CustomException<ExchangeTrainingProjectVO> getRefInfoByExchangeCode(String exchangeCode);

}
