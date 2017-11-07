package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.finance.form.RefundForm;
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

	private RefundForm refundParams;

}
