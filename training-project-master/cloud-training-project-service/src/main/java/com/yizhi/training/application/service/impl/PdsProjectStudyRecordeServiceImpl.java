package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.PdsProjectStudyRecorde;
import com.yizhi.training.application.mapper.PdsProjectStudyRecordeMapper;
import com.yizhi.training.application.service.IPdsProjectStudyRecordeService;
import com.yizhi.training.application.vo.PdsProjectStudyRecordeVo;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * pds 自定义项目 学习记录 服务实现类
 * </p>
 *
 * @author fulan123
 * @since 2022-05-18
 */
@Service
public class PdsProjectStudyRecordeServiceImpl extends ServiceImpl<PdsProjectStudyRecordeMapper, PdsProjectStudyRecorde>
    implements IPdsProjectStudyRecordeService {

    /**
     * 分页查询 排行榜列表
     *
     * @param pageSize
     * @param pageNum
     * @param companyId
     * @param siteId
     * @param pid
     * @return
     */
    @Override
    public Page<PdsProjectStudyRecordeVo> studyPeriodRankingPage(Integer pageSize, Integer pageNum, Long companyId,
        Long siteId, Long pid, Long uid) {
        /*Page<PdsProjectStudyRecordeVo> pageData = new Page<>();
        List<PdsProjectStudyRecordeVo> data;
        // 设置分页
        pageData.setCurrent(pageNum);
        pageData.setSize(pageSize);
        PageHelper.startPage(pageNum, pageSize);
        data = this.baseMapper.studyPeriodRankingList(companyId, siteId, pid, uid);
        Pagination pagination = PageHelper.getPagination();
        PageHelper.remove(); // 移除，免得影响后面的执行
        pageData.setTotal(pagination.getTotal());
        pageData.setRecords(data);
        return pageData;*/

        // 原生sql查询：
        // 参考课程服务： com.yizhi.course.application.service.impl.CourseServiceImpl#findWechatCourseList
        Page<PdsProjectStudyRecordeVo> pageData = new Page<>(); // 默认创建的是第一1页，每页10条
        try (Connection conn = this.sqlSessionBatch().getConnection(); Statement pst = conn.createStatement();) {
            // 查询语句
            StringBuilder sql = new StringBuilder(
                "SELECT s.pid, s.uid, s.period, s.head_portrait, s.user_name, s.rank FROM ("); //-- p.company_id, p
            // .site_id,
            sql.append(" SELECT p.pid, p.uid, p.period, p.head_portrait, p.user_name, ").append(" CASE")
                .append(" WHEN @prevRank = p.period THEN @curRank")
                .append(" WHEN @prevRank := p.period THEN @curRank := @curRank + 1").append(" END AS rank")
                .append(" FROM pds_project_study_recorde p,").append(" (SELECT @curRank :=0, @prevRank := NULL) r")
                .append(" WHERE p.company_id = ").append(companyId).append(" AND p.site_id = ").append(siteId);
            if (pid != null) {
                sql.append(" AND p.pid = " + pid);
            }

            sql.append(" AND p.period > 0").append(" ORDER BY p.period DESC ) s");

            if (uid != null) {
                sql.append(" WHERE s.uid = " + uid);
            }

            if (pageSize != null && pageNum != null) {
                //pageData = new Page<>(pageNum<1?pageNum+1:pageNum, pageSize);
                pageData.setCurrent(pageNum < 1 ? pageNum + 1 : pageNum);
                pageData.setSize(pageSize);
                // 需要分页的业务。首先查询总数
                String countSql = "SELECT COUNT(1) countNum " + (sql.substring(sql.indexOf("FROM")));
                // 执行统计查询
                ResultSet rs = pst.executeQuery(countSql);
                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                pageData.setTotal(count);
                if (count > 0) {
                    sql.append(" LIMIT ").append((pageData.getCurrent() - 1) * pageSize).append(",").append(pageSize);
                    rs = pst.executeQuery(sql.toString());
                    List<PdsProjectStudyRecordeVo> recordeVos = new ArrayList<>();
                    while (rs.next()) {
                        PdsProjectStudyRecordeVo vo = new PdsProjectStudyRecordeVo();
                        vo.setUid(rs.getLong("uid"));
                        vo.setPid(rs.getLong("pid"));
                        vo.setPeriod(rs.getFloat("period"));
                        vo.setHeadPortrait(rs.getString("head_portrait"));
                        vo.setRank(rs.getInt("rank"));
                        vo.setUserName(rs.getString("user_name"));
                        recordeVos.add(vo);
                    }
                    pageData.setRecords(recordeVos);
                    rs.close();
                }
            } else {
                int index = 0;
                ResultSet rs = pst.executeQuery(sql.toString());
                List<PdsProjectStudyRecordeVo> recordeVos = new ArrayList<>();
                while (rs.next()) {
                    PdsProjectStudyRecordeVo vo = new PdsProjectStudyRecordeVo();
                    vo.setUid(rs.getLong(1));
                    vo.setPid(rs.getLong(2));
                    vo.setPeriod(rs.getFloat(3));
                    vo.setHeadPortrait(rs.getString(4));
                    vo.setRank(rs.getInt(6));
                    vo.setUserName(rs.getString(5));
                    recordeVos.add(vo);
                    index++;
                }
                pageData.setRecords(recordeVos);
                pageData.setTotal(index);
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pageData;
    }

    /**
     * 查询自己的课程学习名次和情况
     *
     * @param companyId
     * @param siteId
     * @param pid
     * @param uid
     * @return
     */
    @Override
    public List<PdsProjectStudyRecordeVo> studyPeriodRankingList(Long companyId, Long siteId, Long pid, Long uid) {
        return this.baseMapper.studyPeriodRankingList(companyId, siteId, pid, uid);
    }
}
