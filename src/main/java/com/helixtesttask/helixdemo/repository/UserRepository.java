package com.helixtesttask.helixdemo.repository;

import com.helixtesttask.helixdemo.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDao, Long> {
}
