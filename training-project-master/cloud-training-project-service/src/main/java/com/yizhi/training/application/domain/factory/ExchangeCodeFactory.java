package com.yizhi.training.application.domain.factory;

import com.yizhi.training.application.domain.ExchangeCode;
import com.yizhi.training.application.enums.TrainingCommonEnums;

import java.util.Date;

public class ExchangeCodeFactory {

    public static ExchangeCode newInstance(Long tpId, Integer type, Long siteId) {
        ExchangeCode result = new ExchangeCode();
        result.setRefId(tpId);
        result.setRefType(type);
        result.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
        result.setSiteId(siteId);
        return result;
    }

    public static ExchangeCode newInstance(Long tpId, Integer type, Long siteId, Integer state) {
        ExchangeCode result = new ExchangeCode();
        result.setRefId(tpId);
        result.setRefType(type);
        result.setSiteId(siteId);
        result.setState(state);
        return result;
    }

    public static ExchangeCode newInstance(Long id, Long tpId, Integer type, String code, Long orgId, Long siteId,
        Long companyId, Long createId) {
        ExchangeCode result = new ExchangeCode();
        result.setId(id);
        result.setRefId(tpId);
        result.setRefType(type);
        result.setCode(code);
        result.setState(TrainingCommonEnums.UN_USED.getCode());
        result.setOrgId(orgId);
        result.setSiteId(siteId);
        result.setCompanyId(companyId);
        result.setCreateById(createId);
        result.setCreateTime(new Date());
        result.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
        return result;
    }
}
