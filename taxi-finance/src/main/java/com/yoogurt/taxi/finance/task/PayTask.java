package com.yoogurt.taxi.finance.task;

import com.yoogurt.taxi.finance.form.PayForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayTask implements Serializable {

	private TaskInfo task;

	private PayForm payParams;

}
