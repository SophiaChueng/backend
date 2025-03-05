package com.yizhi.training.application.util;

import com.alibaba.fastjson.JSON;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.training.application.constant.RedisKeyConstant;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.v2.model.VisibleRangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 之前的Redis管理一团糟，用一个工具类统一管理
 */
@Slf4j
@Component
public class CacheUtil {

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取可见范围缓存
     *
     * @param tpId
     * @return
     */
    public List<TpAuthorizationRange> getAuthorizationList(Long tpId) {
        Object obj = redisCache.hget(RedisKeyConstant.VISIBLE_RANGE_KEY, String.valueOf(tpId));
        if (obj == null) {
            return null;
        }
        String s = (String)obj;
        return JSON.parseArray(s, TpAuthorizationRange.class);
    }

    /**
     * 添加可见范围到缓存
     *
     * @param list
     * @param tpId
     */
    public void addAuthorizationList(List<TpAuthorizationRange> list, Long tpId) {
        redisCache.hset(RedisKeyConstant.VISIBLE_RANGE_KEY, String.valueOf(tpId), JSON.toJSONString(list), 600);
    }

    /**
     * 删除缓存中的可见范围
     *
     * @param tpId
     */
    public void delAuthorizationList(Long tpId) {
        String[] keys = new String[] {String.valueOf(tpId)};
        redisCache.hdel(RedisKeyConstant.VISIBLE_RANGE_KEY, keys);
    }

    /**
     * 添加项目可见范围用户id缓存
     *
     * @param accountIds 用户id
     * @param tpId       项目id
     */
    public void addAuthorizationAccountIdList(Set<Long> accountIds, Long tpId, Integer type) {
        VisibleRangeModel build = VisibleRangeModel.builder().accountSet(accountIds).type(type).build();
        redisCache.set(RedisKeyConstant.VISIBLE_RANGE_ACCOUNT_IDS_KEY + tpId, JSON.toJSONString(build), 600);
    }

    /**
     * 删除项目可见范围用户id缓存 用户id
     *
     * @param tpId 项目id
     */
    public void delAuthorizationAccountIdList(Long tpId) {
        redisCache.delete(RedisKeyConstant.VISIBLE_RANGE_ACCOUNT_IDS_KEY + tpId);
    }

    public VisibleRangeModel getAuthorizationAccountIdList(Long tpId) {
        Object obj = redisCache.get(RedisKeyConstant.VISIBLE_RANGE_ACCOUNT_IDS_KEY + tpId);
        if (obj == null) {
            return null;
        }
        String s = (String)obj;
        return JSON.parseObject(s, VisibleRangeModel.class);
    }

}
