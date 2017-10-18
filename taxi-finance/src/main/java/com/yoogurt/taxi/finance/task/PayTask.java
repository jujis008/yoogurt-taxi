package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.dal.doc.finance.PayParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayTask {

	private TaskInfo task;

	private PayParams params;

}
