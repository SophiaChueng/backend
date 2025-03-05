package com.yizhi.training.application.controller;

import com.alibaba.fastjson.JSON;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.TaskContext;
import com.yizhi.core.application.file.task.AbstractDefaultTask;
import com.yizhi.point.application.feign.PointUserFeignClients;
import com.yizhi.system.application.system.remote.OrganizationClient;
import com.yizhi.system.application.system.remote.ReportClient;
import com.yizhi.system.application.vo.OrgVO;
import com.yizhi.system.application.vo.ReportAccountRespVO;
import com.yizhi.training.application.domain.StatisticsTrainingProject;
import com.yizhi.training.application.domain.StatisticsTrainingProjectLearn;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.StatisticsTrainingProjectMapper;
import com.yizhi.training.application.service.IStatisticsTrainingProjectLearnService;
import com.yizhi.training.application.service.IStatisticsTrainingProjectService;
import com.yizhi.util.application.domain.Response;
import com.yizhi.util.application.page.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class StatisticsTrainingProjectRun extends AbstractDefaultTask<String, Map<String, Object>> {

    //字段分割符
    private final static String FIELD_STERMINATED = "'\t'";

    //行分割符
    private final static String LINE_STERMINATED = "'\r\n'";

    private final static String FENGEFU = "\t";  //tab键

    private final static String HUANHANGFU = "\r\n";  //换行

    private final static String COLUMNS =
        "(id,training_project_id,training_project_name,training_project_create_time,training_project_start_time," +
            "training_project_end_time,training_project_state,training_project_org_id,training_project_org_name," +
            "training_project_site_id,training_project_company_id," + "account_id,work_num,name,fullname,org_id," +
            "org_no,org_name,org_parent_names,join_state,account_state,account_org_id,account_site_id," +
            "account_company_id,record_create_time)";

    private final static String EXECUTE_SQL =
        "load data local infile ''  into table statistics_training_project fields terminated by " + FIELD_STERMINATED + "enclosed by '' escaped by 'N'" + " lines terminated by " + LINE_STERMINATED + " " + COLUMNS;

    /**
     * 缓存学员学习信息
     */
    private final Map<String, Map<Long, StatisticsTrainingProject>> accountMap =
        new HashMap<String, Map<Long, StatisticsTrainingProject>>();

    /**
     * 缓存部门信息
     */
    private final Map<String, List<OrgVO>> orgMap = new HashMap<String, List<OrgVO>>();

    // 分页获取学员学习信息
    int pageNo = 1;

    int pageSize = 30;

    @Autowired
    IdGenerator idGenerator;

    private Logger logger = LoggerFactory.getLogger(StatisticsTrainingProjectRun.class);

    //复用对象
    private StringBuilder strBuilder = new StringBuilder();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private IStatisticsTrainingProjectService statisticsTrainingProjectService;

    @Autowired
    private IStatisticsTrainingProjectLearnService statisticsTrainingProjectLearnService;

    @Autowired
    private ReportClient reportClient;

    @Autowired
    private StatisticsTrainingProjectMapper statisticsTrainingProjectMapper;

    @Autowired
    private PointUserFeignClients pointUserFeignClients;

    @Autowired
    private OrganizationClient organizationClient;

    @Autowired
    private DataSource dataSource;

    public Response<String> insertBatchStatisticsTrainingProject(List<StatisticsTrainingProject> insertList)
        throws SQLException {

        Connection conn = null;
        InputStream in = null;
        PreparedStatement preStatement = null;
        com.mysql.cj.jdbc.ClientPreparedStatement mysqlPreStatement = null;
        try {
            // sql编译
            conn = DataSourceUtils.getConnection(dataSource);
            preStatement = conn.prepareStatement(EXECUTE_SQL);
            mysqlPreStatement = preStatement.unwrap(com.mysql.cj.jdbc.ClientPreparedStatement.class);
            in = switchStream(insertList);
            mysqlPreStatement.setLocalInfileInputStream(in);
            // 执行
            preStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            //大对象手动显示的设置为空保证GC时被释放
            if (in != null) {
                in = null;
            }

            //关闭数据库连接
            if (mysqlPreStatement != null) {
                mysqlPreStatement.close();
            }
            if (preStatement != null) {
                preStatement.close();
            }
            if (conn != null) {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        }

        return Response.ok();
    }

    public InputStream switchStream(List<StatisticsTrainingProject> insertList) {

        Long id = null;
        for (StatisticsTrainingProject st : insertList) {
            id = idGenerator.generate();
            strBuilder.append(id); // 主键
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectId());// 培训项目ID
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectName());// 培训项目名字
            strBuilder.append(FENGEFU);
            if (st.getRecordCreateTime() != null) {
                strBuilder.append(simpleDateFormat.format(st.getTrainingProjectCreateTime()));// 培训项目的创建时间
            }
            strBuilder.append(FENGEFU);
            if (st.getTrainingProjectStartTime() != null) {
                strBuilder.append(simpleDateFormat.format(st.getTrainingProjectStartTime())); //培训项目的开始时间
            }
            strBuilder.append(FENGEFU);
            if (st.getTrainingProjectEndTime() != null) {
                strBuilder.append(simpleDateFormat.format(st.getTrainingProjectEndTime()));//培训项目的结束时间
            }
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectState());//培训项目的状态
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectOrgId());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectOrgName());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectSiteId());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getTrainingProjectCompanyId());
            strBuilder.append(FENGEFU);

            //个人信息
            strBuilder.append(st.getAccountId());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getWorkNum());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getName());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getFullname());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getOrgId());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getOrgNo());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getOrgName());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getOrgParentNames());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getJoinState());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getAccountState());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getAccountOrgId());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getAccountSiteId());
            strBuilder.append(FENGEFU);
            strBuilder.append(st.getAccountCompanyId());

            strBuilder.append(HUANHANGFU);

        }
        byte[] bytes = strBuilder.toString().getBytes();
        InputStream in = new ByteArrayInputStream(bytes);

        //大对象手动显示的设置为空保证GC时被释放
        bytes = null;

        //清空之前的内容
        strBuilder.delete(0, strBuilder.length());

        return in;
    }

    /**
     * @param listReportAccountRespVO 分页查询出来的人
     * @param findAccountId           null
     * @param resultccountMap         resultMap
     * @param rangeKey
     * @return
     */
    private StatisticsTrainingProject initStatisticsTrainingProjects(List<ReportAccountRespVO> listReportAccountRespVO,
        Long findAccountId, Map<Long, StatisticsTrainingProject> resultccountMap, String rangeKey) {

        //查找指定的人
        StatisticsTrainingProject learnStu = null;

        // 初始化学员学习信息
        for (ReportAccountRespVO item : listReportAccountRespVO) {
            Long accountId = item.getUserId();
            StatisticsTrainingProject stu = null;
            if (accountMap.get(rangeKey) != null) {
                stu = accountMap.get(rangeKey).get(accountId);
            } else {
                accountMap.put(rangeKey, resultccountMap);
            }

            if (stu == null) {
                stu = new StatisticsTrainingProject();
                stu.setId(null);
                stu.setAccountId(accountId);
                stu.setAccountState(item.getStatus());
                stu.setWorkNum(item.getWorkNum());
                stu.setName(item.getUserName());
                stu.setFullname(item.getUserFullName());
                stu.setAccountOrgId(item.getOrgId());
                stu.setAccountSiteId(null);
                stu.setAccountCompanyId(null);
                stu.setOrgNo(item.getOrgCode());
                stu.setOrgName(item.getOrgName());
                String orgParentNames = null;
                if (item.getParentOrgNames() != null) {
                    for (String orgName : item.getParentOrgNames()) {
                        if (orgParentNames == null) {
                            orgParentNames = orgName;
                        } else {
                            orgParentNames += "/" + orgName;
                        }
                    }
                    stu.setOrgParentNames(orgParentNames);
                }
                // 设置实际参加状态，用于实际人数的统计

                resultccountMap.put(accountId, stu);
            }
            if (findAccountId != null && findAccountId.compareTo(accountId) == 0) {
                learnStu = stu;
            }
        }
        return learnStu;
    }

    private PageInfo<ReportAccountRespVO> getPage() {
        PageInfo<ReportAccountRespVO> page = new PageInfo<ReportAccountRespVO>();
        List<ReportAccountRespVO> list = new ArrayList<ReportAccountRespVO>();
        ReportAccountRespVO vo = new ReportAccountRespVO();
        vo.setUserId(1314L);
        vo.setUserName("1314UserName");
        vo.setOrgId(1314L);
        vo.setOrgName("1314LOrgName");
        vo.setWorkNum("1314workNum");

        list.add(vo);

        ReportAccountRespVO vo1 = new ReportAccountRespVO();
        vo1.setUserId(0L);
        vo1.setUserName("0UserName");
        vo1.setOrgId(0L);
        vo1.setOrgName("0LOrgName");
        vo1.setWorkNum("0workNum");
        list.add(vo1);

        page.setRecords(list);
        page.setPageTotal(2);

        return page;
    }

    /**
     * 创建和获取学员学习对象
     *
     * @param siteId                  站点ID，为空不缓存学员信息
     * @param listReportAccountRespVO 分页查询出的学员
     * @param resultAccountIds        保存当前查询的学员IDs，每次会清空集合中的记录
     * @param resultccountMap         保存当前查询的学员信息，每次会清空集合中的记录
     * @return 当前查询的学员IDs
     */

    private List<OrgVO> getOrg() {
        List<OrgVO> list = new ArrayList<OrgVO>();
        OrgVO ov = new OrgVO();
        ov.setId(1L);
        ov.setName("name");

        return null;
    }

    @SuppressWarnings("static-access")
    private Date addDay(Date date, int num) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, num);
        date = calendar.getTime();
        return date;
    }

    /**
     * @param siteId              站点ID
     * @param orgIdsAndAccountIds 指定人
     * @param pageNo
     * @param pageSize
     * @param findAccountId       null
     * @param resultccountMap     map中的值
     * @param rangeKey
     * @param taskId              执行本本天任务的id编号  一天的不变
     * @param taskCon             执行本个培训项目的信息  没个项目都不一样
     * @return
     */
    private StatisticsTrainingProject getReportAccountRespVO(Long siteId, List<Long> orgIdsAndAccountIds,
        Integer pageNo, Integer pageSize, Long findAccountId, Map<Long, StatisticsTrainingProject> resultccountMap,
        String rangeKey, Long taskId, String taskCon) {/*

		//查找指定的人
		StatisticsTrainingProjectVo learnStu = null;

		// 获取用户信息
		ReportRangeAccountReqVO rrarv = new ReportRangeAccountReqVO();
		rrarv.setSiteId(siteId);
		rrarv.setPageNo(pageNo);
		rrarv.setOrgIdsAndAccountIds(orgIdsAndAccountIds);
		rrarv.setPageSize(pageSize);
		PageInfo<ReportAccountRespVO> page = null;
		try{
			page = reportClient.getRangeAccounts(rrarv);
			if(page == null || page.getRecords() == null){
				return null;
			}

			learnStu = initStatisticsTrainingProjects(page.getRecords(), null, resultccountMap, rangeKey);

			//分页查询查找所有人
			int pageTotal = page.getPageTotal();
			//int pageCount = (int) Math.ceil(Double.valueOf(page.getPageRecords()) / pageSize);
			for (int i = 2; i <= pageTotal; i++) {
				rrarv.setSiteId(siteId);
				rrarv.setPageNo(i);
				rrarv.setOrgIdsAndAccountIds(orgIdsAndAccountIds);
				rrarv.setPageSize(pageSize);
				page = reportClient.getRangeAccounts(rrarv);
				if(page != null && page.getRecords().size() > 0){
					if(learnStu == null){
						learnStu = initStatisticsTrainingProjects(page.getRecords(), findAccountId, resultccountMap,
						rangeKey);
					} else {
						initStatisticsTrainingProjects(page.getRecords(), null, resultccountMap, rangeKey);
					}

				}
			}
		} catch(Exception e){
			e.printStackTrace();
			if(siteId == null){
				taskDetail(taskId, "获取" + taskCon + "的指定范围Ids:(" + orgIdsAndAccountIds + ") system服务不可用,:" + e
				.getMessage());
			} else {
				taskDetail(taskId, "获取" + taskCon + "的全平台:(" + siteId + ") system服务不可用,:" + e.getMessage());
			}

			return null;
		}

		return learnStu;
	*/
        return null;
    }

    /**
     * 插入空的学习记录
     *
     * @param resultccountMap
     * @param statCourse
     * @param insertList
     * @param course
     * @param courseDuration
     */
    private void insertEmptyTrainingProjectLearn(Map<Long, StatisticsTrainingProject> resultccountMap,
        TrainingProject trainingProject, List<StatisticsTrainingProject> insertList, Long taskId) {
        if (insertList != null) {
            insertList.clear();
        }

        //获取企业的所有部门信息
        Long orgId = trainingProject.getOrgId(); // 部门ID

        String orgNo = "";
        String orgName = "";
        List<OrgVO> orgs = orgMap.get(orgId.toString());
        if (orgs == null) {
            try {
                List<Long> ids = new ArrayList<Long>();
                ids.add(orgId);
                orgs = organizationClient.listByOrgIds(ids);
            } catch (Exception e) {
                e.printStackTrace();
                taskDetail(taskId, "获取" + orgId + "的部门信息错误system服务不可用:" + e.getMessage());
            }
            orgMap.put(orgId.toString(), orgs);
        }
        if (orgs != null) {
            for (OrgVO item : orgs) {
                if (orgId.compareTo(item.getId()) == 0) {
                    orgNo = item.getCode();
                    orgName = item.getName();
                    break;
                }
            }
        }

        //通过培训项目的ID从人和培训项目对应关系表获取该培训项目已经存在的学员账号
        Map<Long, Long> stuMap =
            statisticsTrainingProjectMapper.selectAccountLearnByTrainingProjectId(trainingProject.getId());

        //获得所有的个人信息
        Iterator<StatisticsTrainingProject> statTrainingProjects = resultccountMap.values().iterator();
        StatisticsTrainingProject statTrainingProject;
        while (statTrainingProjects.hasNext()) {
            //更新培训项目信息
            statTrainingProject = statTrainingProjects.next();
            statTrainingProject.setTrainingProjectId(trainingProject.getId());
            statTrainingProject.setTrainingProjectName(trainingProject.getName());
            statTrainingProject.setTrainingProjectCreateTime(trainingProject.getCreateTime());
            statTrainingProject.setTrainingProjectStartTime(trainingProject.getStartTime());
            statTrainingProject.setTrainingProjectEndTime(trainingProject.getEndTime());
            statTrainingProject.setTrainingProjectState(trainingProject.getStatus());
            statTrainingProject.setTrainingProjectOrgId(trainingProject.getOrgId());
            statTrainingProject.setTrainingProjectSiteId(trainingProject.getSiteId());
            statTrainingProject.setTrainingProjectCompanyId(trainingProject.getCompanyId());

            if (stuMap == null) {
                insertList.add(statTrainingProject);
            } else {
                //检查 如果培训项目和人没有对应关系，则插入
                if (stuMap.get(statTrainingProject.getAccountId()) == null) {
                    insertList.add(statTrainingProject);
                }
            }
        }
        //批量插入
        if (!CollectionUtils.isEmpty(insertList)) {
            try {
                //statisticsTrainingProjectService.saveBatch(insertList);
                insertBatchStatisticsTrainingProject(insertList);
            } catch (Exception e) {
                logger.info("***insert data*********" + JSON.toJSONString(insertList));
                e.printStackTrace();
            }
        }
    }

    /**
     * map定义的键 startDate 为空系统自动计算（从对中间表或主表中获取），不为空就用实际传参 endDate 为空系统自动计算（当前日期-1），不为空就用实际传参
     */
    @Override
    protected String execute(Map<String, Object> map) {

        Date startRunDate = new Date();

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

        Long taskId = idGenerator.generate();
        TaskContext taskContext =
            new TaskContext(taskId, formatDate.format(startRunDate) + " 培训项目报表分析", 999L, startRunDate);
        working(taskContext);

        try {
            String startDate = null;
            String endDate = null;
            Date dCurDate = null;

            if (map != null) {
                startDate = (String)map.get("startDate");
                endDate = (String)map.get("endDate");
            }

            //判断是否首次初始化，不是的话删除之前的数据
            boolean isRunDel = true;

            if (startDate == null || "".equals(startDate)) {
                //从中间表取最大日期，如有有记录，那就是最大日期+1天
                Date middleMaxTime = statisticsTrainingProjectMapper.selectMaxDate();
                if (middleMaxTime != null) {
                    //记录的最大一天加一天
                    dCurDate = addDay(middleMaxTime, 1);
                    startDate = formatDate.format(dCurDate);
                    isRunDel = true;
                } else {
                    isRunDel = false;
                }
            }

            if (endDate == null || "".equals(endDate)) {                        //endDate是当前日期的昨天，只统计到前一天
                dCurDate = addDay(new Date(), -1);
                endDate = formatDate.format(dCurDate);
            }

            Long day = 0L;
            Date beginDate = null;
            Date overDate = formatDate.parse(endDate);
            //不是首次初始化数据，删除 数据
            if (isRunDel) {
                try {
                    beginDate = formatDate.parse(startDate);
                    day = (overDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
                } catch (ParseException e1) {
                    return null;
                }

                //先删除这天的记录再添加，防止一天添加多次记录
                for (int d = 0; d <= day; d++) {
                    String currentDate = startDate;
                    if (d > 0) {
                        dCurDate = addDay(beginDate, d);
                        currentDate = formatDate.format(dCurDate);
                    }
                    statisticsTrainingProjectMapper.deleteRecordeByDate(currentDate);
                }
            }

            // 获得所有的培训项目(条件：未上架/已上架，没有被删除的课程)
            List<TrainingProject> getAllTrainingProject = statisticsTrainingProjectMapper.getAllTrainingProject();
            //指定范围可见的课程人员的IdList
            List<Long> listAccountIdRange = null;

            logger.info("***** 培训项目统计开始 *****");

            List<StatisticsTrainingProjectLearn> accountLearns = null;
            List<StatisticsTrainingProject> insertList = new ArrayList<StatisticsTrainingProject>();

            String rangeKey = "";  //判断是否是不符合常理的数据
            boolean isOk = true;
            String curTrainingProjectName = "";
            Long curTrainingProjectId = 0L;
            try {
                // 按课程遍历学员学习记录
                for (TrainingProject trainingProject : getAllTrainingProject) {

                    Long trainingProjectId = trainingProject.getId();     //培训项目ID
                    Long siteId = trainingProject.getSiteId();            //培训项目的站点ID
                    curTrainingProjectName = trainingProject.getName();   //培训项目的名字
                    curTrainingProjectId = trainingProjectId;             //培训项目的ID

                    Integer scope = trainingProject.getVisibleRange(); //0：指定学员可见，1平台用户可见
                    if (scope == 0) {   //如果是指定范围  siteId=null   listAccountIdRange有值
                        siteId = null;
                        //查出来这个培训项目的指定人
                        listAccountIdRange =
                            statisticsTrainingProjectMapper.getRangeByTrainingProjectId(trainingProjectId);
                        if (null == listAccountIdRange) {
                            taskDetail(taskId,
                                trainingProject.getName() + "(" + trainingProject.getId() + ")" + " " +
                                    "范围可见表中数据为空，请确认是否为该培训项目设置的范围,无法统计数据");
                            rangeKey = "";
                        } else {
                            if (trainingProject.getSiteId() != null) {
                                rangeKey = trainingProject.getSiteId().toString() + listAccountIdRange.toString();
                            }
                        }
                    }

                    if (scope == 1) {   // 指定范围为全平台可见的
                        listAccountIdRange = null;
                        if (siteId != null) {
                            rangeKey = siteId.toString();
                        } else {
                            rangeKey = "";
                            taskDetail(taskId,
                                trainingProject.getName() + "(" + trainingProject.getId() + ")" + " 全平台可见为空:(" + siteId + ") ，全平台的站点有问题，请确认数据,无法统计数据");
                        }
                    }
                    if ("".equals(rangeKey)) {
                        continue;
                    }

                    // 从缓存中获取用户，没有找到再请求system查询
                    // rangeKey ：站点id+指定人   站点id
                    Map<Long, StatisticsTrainingProject> resultccountMap = accountMap.get(rangeKey);
                    if (resultccountMap == null) {
                        resultccountMap = new HashMap<Long, StatisticsTrainingProject>();
                        getReportAccountRespVO(trainingProject.getSiteId(), listAccountIdRange, pageNo, pageSize, null,
                            resultccountMap, rangeKey, taskId,
                            trainingProject.getName() + "(" + trainingProject.getId() + ")");
                        if (resultccountMap.size() == 0) {
                            if (scope == 2) {

                                taskDetail(taskId,
                                    trainingProject.getName() + "(" + trainingProject.getId() + ")" + " 没有查找到该站点(" + trainingProject.getId() + ")下" + "指定范围IDs:(" + listAccountIdRange.toString() + ") 的学员信息,无法统计数据");
                            } else {
                                taskDetail(taskId,
                                    trainingProject.getName() + "(" + trainingProject.getId() + ")" + " 没有查找到全平台可见:(" + siteId + ") 学员,无法统计数据");
                            }
                            continue;
                        }
                    }
                    if (resultccountMap != null) {
                        insertEmptyTrainingProjectLearn(resultccountMap, trainingProject, insertList, taskId);
                    }

                    // 循环统计每天的学习记录
                    if (!isRunDel) {
                        //首次初始化数据时，开始时间就是课程创建时间
                        beginDate = trainingProject.getStartTime();
                    }
                    day = (overDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);

                    //循环统计每天的学习记录
                    for (int d = 0; d <= day; d++) {
                        String currentDate = null;
                        dCurDate = addDay(beginDate, d);
                        currentDate = formatDate.format(dCurDate);

                        //批量添加当前日期学习记录
                        accountLearns =
                            statisticsTrainingProjectService.insertAccountLearn(trainingProjectId, currentDate);
                        //没有学习记录，跳转
                        if (accountLearns == null || accountLearns.size() == 0) {
                            continue;
                        }

                        /**
                         * 培训项目没有积分这个东西，因此这段代码没用
                         */
	/*				// 遍历有学习的学员信息统计他们的学习情况，并更新统计表中存在的记录：更新条件ID
					for (StatisticsTrainingProjectLearnVo statCourse : accountLearns) {
						Long accountId = statCourse.getAccountId();

						// 学习这门课的积分
						try {
							statCourse.setLearnPoint(pointUserFeignClients.acquirePoint(accountId, trainingProjectId,
							currentDate));
						} catch (Exception e) {
							taskDetail(taskId,
									trainingProject.getName() + "(" + trainingProject.getId() + ")" + " 获取积分服务不可用: " +
									 e.getMessage());
						}
						// 更新已学习的学员信息
						statisticsTrainingProjectLearnService.updateById(statCourse);
					}*/
                    }
                    taskDetail(taskId,
                        "成功统计培训项目 " + trainingProject.getName() + "(" + trainingProject.getId() + ")");
                }
            } catch (Exception e) {
                e.printStackTrace();
                isOk = false;
                taskDetail(taskId,
                    "统计培训项目" + curTrainingProjectName + "(" + curTrainingProjectId + ")异常:" + e.getMessage());
            }

            try {
                //添加课程，先删除后添加信息
                statisticsTrainingProjectMapper.deleteStatisticsTrainingProjectToGroupFind();
                statisticsTrainingProjectMapper.insertStatisticsTrainingProjectToGroupFind();

                //添加学员，先删除后添加信息
                statisticsTrainingProjectMapper.deleteStatisticsTrainingProjectToAccountGroupFind();
                statisticsTrainingProjectMapper.insertStatisticsTrainingProjectToAccountGroupFind();

                //添加部门学习数据，先删除后添加信息
                //statisticsCourseMapper.deleteStatisticsCourseToOrgMetadata();
                //statisticsCourseMapper.insertStatisticsCourseToOrgMetadata();

                //更新部门编码
				/*List<Map<String, Long>> emptyOrg = statisticsCourseMapper.selectEmptyOrgCode();
				List<OrgVO> orgVos = null;
				for(Map<String, Long> mapItem : emptyOrg){
					Long companyId = mapItem.get("companyId");
					orgVos = organizationClient.fuzzySearchOrgByName("", companyId);
					if(orgVos != null){
						for(OrgVO item : orgVos){
							statisticsCourseMapper.updateEmptyOrgCode(item.getCode(), companyId, item.getId());
						}
					}
				}*/
            } catch (Exception e) {
                logger.info("***** 添加数据到按部门统计表错误 *****");
                taskDetail(taskId, "添加数据到中间表异常:" + e.getMessage());
                isOk = false;
            }

            accountMap.clear();

            Date endRunDate = new Date();

            String con = "培训项目报表分析完成耗时 :" + (endRunDate.getTime() - startRunDate.getTime()) / 1000 + "秒";
            taskDetail(taskId, con);
            if (isOk) {
                success(taskContext, con);
            } else {
                fail(taskContext, con);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String con = "培训项目报表分析异常结束：" + e.getMessage();
            logger.info("***** " + con);
            taskDetail(taskId, con);
            fail(taskContext, con);
        }

        return "123";
    }

}
