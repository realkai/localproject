package com.wiline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestPollerMibService {
    
    @Autowired
    private TestPollerMibsDao pollerMibsDao;
    
    public void comeon() {
	pollerMibsDao.show();
    }
}
