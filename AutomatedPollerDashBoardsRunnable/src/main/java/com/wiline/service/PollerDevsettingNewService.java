package com.wiline.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiline.dao.PollerDevsettingNewDao;
import com.wiline.dao.PollerLinksNewDao;

@Service
@Transactional
public class PollerDevsettingNewService {

    @Autowired
    private PollerDevsettingNewDao pollerDevsettingNewDao;

    @Autowired
    private PollerLinksNewDao pollerLinksNewDao;

    public void updatePollerDevsettingNew(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {
	HashMap<Long, Integer> pollerLink = pollerLinksNewDao.getRowIdToRfidMap();
	HashSet<Long> insertSet = new HashSet<>(pollerLink.keySet());

	HashMap<Long, Long> pollerDevsetting = pollerDevsettingNewDao.getDevsettingPollerIdPointRowIdMap();
	HashSet<Long> deleteSet = new HashSet<>(pollerDevsetting.keySet());

	HashSet<Long> updateSet = new HashSet<>(insertSet);

	updateSet.retainAll(deleteSet);
	insertSet.removeAll(updateSet);
	deleteSet.removeAll(updateSet);

	if (!insertSet.isEmpty()) {
	    System.out.println(" insert devsetting... ");
	    pollerDevsettingNewDao.insertPollerDevsettingInfo(pollerLinkMap);
	    System.out.println(" insert devsetting complete ");
	}

	if (!updateSet.isEmpty()) {
	    System.out.println(" update devsetting... ");
	    pollerDevsettingNewDao.updatePollerDevsettingInfo(pollerLinkMap);
	    System.out.println(" insert devsetting complete ");
	}

	if (!deleteSet.isEmpty()) {
	    System.out.println(" delete devsetting... ");
	    pollerDevsettingNewDao.deletePollerDevsettingInfo(deleteSet);
	    System.out.println(" insert devsetting complete ");
	}

    }

}
