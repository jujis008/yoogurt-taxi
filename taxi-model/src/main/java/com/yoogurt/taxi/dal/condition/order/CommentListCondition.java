package com.yoogurt.taxi.dal.condition.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentListCondition {

    private String orderId;

    private String fromUserId;

    private String toUserId;
}
