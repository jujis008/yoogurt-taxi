package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.CommonResource;

import java.util.List;

public interface CommonResourceService {

	List<CommonResource> getResources(String linkId, String tableName);

	int addResources(List<CommonResource> resources);

	int removeResources(String linkId, String tableName);

	List<CommonResource> assembleResources(String linkId, String tableName, String... pictures);
}
