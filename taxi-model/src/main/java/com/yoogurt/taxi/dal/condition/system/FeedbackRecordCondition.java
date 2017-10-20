package com.yoogurt.taxi.dal.condition.system;

import com.yoogurt.taxi.common.condition.PageableCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRecordCondition extends PageableCondition{
    private String username;
    private Long feedbackType;
}
