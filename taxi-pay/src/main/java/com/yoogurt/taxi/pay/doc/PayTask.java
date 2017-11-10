package com.yoogurt.taxi.pay.doc;

import com.yoogurt.taxi.dal.bo.TaskInfo;
import com.yoogurt.taxi.pay.params.PayParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayTask implements Serializable {

    @Id
    private String taskId;

    private TaskInfo task;

    private PayParams payParams;

    public PayTask(String taskId) {
        this.taskId = taskId;
    }
}
