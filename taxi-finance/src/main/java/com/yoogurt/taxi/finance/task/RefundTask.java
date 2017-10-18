package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.RefundParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefundTask {

	private TaskInfo task;

	private RefundParams params;

}
