package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.AccessRecord;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface AccessRecordMapper extends Mapper<AccessRecord> {
}