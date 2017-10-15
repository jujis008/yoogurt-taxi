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

    private Long orderId;

    private Long fromUserId;

    private Long toUserId;
}
