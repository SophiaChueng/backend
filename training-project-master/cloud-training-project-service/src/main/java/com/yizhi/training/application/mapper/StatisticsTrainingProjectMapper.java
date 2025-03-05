package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.StatisticsTrainingProject;
import com.yizhi.training.application.domain.StatisticsTrainingProjectLearn;
import com.yizhi.training.application.domain.TrainingProject;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
public interface StatisticsTrainingProjectMapper extends BaseMapper<StatisticsTrainingProject> {

    Date selectMaxDate();

    Date selectRecordMinTime();

    List<TrainingProject> getAllTrainingProject();

    Integer deleteRecordeByDate(@Param("currentDate") String currentDate);

    List<Long> getRangeByTrainingProjectId(@Param("trainingProjectId") Long trainingProjectId);

    void deleteStatisticsTrainingProjectToGroupFind();

    void insertStatisticsTrainingProjectToGroupFind();

    void deleteStatisticsTrainingProjectToAccountGroupFind();

    void insertStatisticsTrainingProjectToAccountGroupFind();

    /**
     * 批量插入学习记录
     *
     * @param courseId 课程ID
     * @param curDate  学习日期
     * @return
     */
    int insertAccountLearn(@Param("trainingProjectId") Long trainingProjectId, @Param("curDate") String curDate);

    /**
     * 获取学习记录
     *
     * @param courseId 课程ID
     * @param curDate  学习日期
     * @return 返回学习的学生ID集合
     */
    List<StatisticsTrainingProjectLearn> selectAccountLearn(@Param("trainingProjectId") Long trainingProjectId,
        @Param("curDate") String curDate);

    @MapKey("accountId")
    Map<Long, Long> selectAccountLearnByTrainingProjectId(@Param("trainingProjectId") Long trainingProjectId);

}
