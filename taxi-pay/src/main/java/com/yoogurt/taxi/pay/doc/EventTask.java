package com.yoogurt.taxi.pay.doc;

import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.bo.TaskInfo;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
