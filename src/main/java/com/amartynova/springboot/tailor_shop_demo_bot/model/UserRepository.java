package com.amartynova.springboot.tailor_shop_demo_bot.model;

import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long> {
}
