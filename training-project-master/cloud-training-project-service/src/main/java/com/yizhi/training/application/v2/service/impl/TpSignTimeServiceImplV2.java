package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpSign;
import com.yizhi.training.application.domain.TpSignTime;
import com.yizhi.training.application.v2.mapper.TpSignTimeMapperV2;
import com.yizhi.training.application.v2.service.TpSignTimeService;
import com.yizhi.training.application.v2.vo.TpSignTimeVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TpSignTimeServiceImplV2 extends ServiceImpl<TpSignTimeMapperV2, TpSignTime> implements TpSignTimeService {

    @Autowired
    private IdGenerator idGenerator;

    public static String getStringRandom(int length) {
        String val = "";
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{5,}$";
        boolean matches = Pattern.matches(regex, val);
        if (!matches || val.contains("l") || val.contains("l") || val.contains("I") || val.contains(
            "O") || val.contains("0")) {
            val = getStringRandom(5);
        }
        return val;
    }

    @Override
    public List<TpSignTime> selectBySignId(Long trainingProjectId, Long signId) {
        QueryWrapper<TpSignTime> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("sign_id", signId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public void updateSignTime(Long companyId, Long siteId, Long trainingProjectId, TpSign sign,
        List<TpSignTimeVO> tpSignTimes) {

        List<TpSignTime> oldTpSignTimes = selectBySignId(trainingProjectId, sign.getId());
        Set<Long> removeIds = CollectionUtils.isEmpty(oldTpSignTimes) ? new HashSet<>()
            : oldTpSignTimes.stream().map(TpSignTime::getId).collect(Collectors.toSet());

        List<TpSignTime> updateSignTimes = new ArrayList<>();
        List<TpSignTime> insertSignTimes = new ArrayList<>();
        for (TpSignTimeVO vo : tpSignTimes) {
            if (vo.getId() != null && vo.getId() > 0) {
                removeIds.remove(vo.getId());
                TpSignTime updateEnt = new TpSignTime();
                BeanUtils.copyProperties(vo, updateEnt);
                updateSignTimes.add(updateEnt);
            } else {
                TpSignTime insertEnt = new TpSignTime();
                BeanUtils.copyProperties(vo, insertEnt);
                insertEnt.setId(idGenerator.generate());
                insertEnt.setCompanyId(companyId);
                insertEnt.setSiteId(siteId);
                insertEnt.setTrainingProjectId(trainingProjectId);
                insertEnt.setSignId(sign.getId());
                insertEnt.setDeleted(0);
                insertEnt.setEnablePosition(sign.getEnablePosition());
                if (Objects.equals(insertEnt.getEnablePosition(), 1)) {
                    insertEnt.setCode(getStringRandom(5));
                }

                insertSignTimes.add(insertEnt);
            }
        }
        if (CollectionUtils.isNotEmpty(updateSignTimes)) {
            updateBatchById(updateSignTimes);
        }
        if (CollectionUtils.isNotEmpty(insertSignTimes)) {
            saveBatch(insertSignTimes);
        }
        if (CollectionUtils.isNotEmpty(removeIds)) {
            removeBatchByIds(removeIds);
        }
    }
}
