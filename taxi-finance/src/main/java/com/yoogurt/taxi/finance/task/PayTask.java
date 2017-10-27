package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.finance.form.PayForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayTask implements Serializable {

	@Id
	private String taskId;

	private TaskInfo task;

	private PayForm payParams;

}
