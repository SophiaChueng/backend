package com.yizhi.training.application.v2.service.biz;

import com.yizhi.forum.application.constans.PostRelationTypeEnum;
import com.yizhi.forum.application.feign.PostsClient;
import com.yizhi.forum.application.feign.PostsRelationClient;
import com.yizhi.forum.application.vo.forum.AddPostRelationRequestVO;
import com.yizhi.forum.application.vo.forum.PostsManageVo;
import com.yizhi.training.application.v2.vo.request.AddTpPostsRequestVO;
import com.yizhi.training.application.v2.vo.request.DeleteTpPostsRequestVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TpForumBizService {

    @Autowired
    private PostsClient postsClient;

    @Autowired
    private PostsRelationClient postsRelationClient;

    /**
     * 查询项目绑定的帖子列表
     *
     * @param tpId
     * @return
     */
    public List<PostsManageVo> getTpPosts(Long tpId) {
        return postsClient.getRelationPosts(PostRelationTypeEnum.TRAINING.getCode(), tpId);
    }

    /**
     * 删除项目和帖子的关联关系
     *
     * @param requestVO
     * @return
     */
    public Boolean deleteTpPosts(DeleteTpPostsRequestVO requestVO) {
        if (CollectionUtils.isEmpty(requestVO.getPostsIds())) {
            return false;
        }
        return postsRelationClient.deleteRelation(requestVO.getTrainingProjectId(), requestVO.getPostsIds());
    }

    /**
     * 增加项目和帖子的关联关系
     *
     * @param requestVO
     * @return
     */
    public Boolean addTpPosts(AddTpPostsRequestVO requestVO) {
        if (CollectionUtils.isEmpty(requestVO.getPostsIds())) {
            return false;
        }
        AddPostRelationRequestVO request = new AddPostRelationRequestVO();
        request.setRelationId(requestVO.getTrainingProjectId());
        request.setTaskName(requestVO.getName());
        request.setTaskLogo(requestVO.getLogoImg());
        request.setType(PostRelationTypeEnum.TRAINING.getCode());
        request.setPostsIds(requestVO.getPostsIds());
        return postsRelationClient.addPostsRelation(request);
    }

    public Boolean updateSort(Long trainingProjectId, Long movePostId, Long prePostId) {
        return postsRelationClient.updatePostsSort(trainingProjectId, PostRelationTypeEnum.TRAINING.getCode(),
            movePostId, prePostId);
    }
}
