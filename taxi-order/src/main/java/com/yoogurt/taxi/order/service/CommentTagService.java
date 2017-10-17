package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.CommentTag;

import java.util.List;

public interface CommentTagService {

    List<CommentTag> getTags();

    CommentTag addCommentTag(CommentTag tag);

    CommentTag updateCommentTag(CommentTag tag);

    int deleteCommentTag(List<Long> tagIds);
}
