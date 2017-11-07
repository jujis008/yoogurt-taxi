package com.yoogurt.taxi.dal.doc.finance;

import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.bo.TaskInfo;
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
public class EventTask implements Serializable {

	@Id
	private String taskId;

	private TaskInfo task;

	private Event<? extends Notify> event;

	private Payment payment;


	public EventTask(String taskId) {
		this.taskId = taskId;
	}
}
