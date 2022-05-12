package com.helixtesttask.helixdemo.batch;

import com.helixtesttask.helixdemo.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;


@Slf4j
public class UserItemProcessor implements ItemProcessor<UserDao, UserDao> {
    @Override
    public UserDao process(UserDao userDao) throws Exception {
        if (userDao != null) {
            val lastUpdatedTime = userDao.getLastUpdatedTime() == null ? LocalDateTime.now() : userDao.getLastUpdatedTime();
            userDao.setLastUpdatedTime(lastUpdatedTime);
            log.info("Batch user dao processing started for user {}, updated at: {}", userDao, userDao.getLastUpdatedTime());
        }
        return userDao;
    }
}
