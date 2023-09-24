package com.honepix.zarena.module.economy.data;

import com.honepix.userapi.data.User;
import com.j256.ormlite.dao.Dao;

import java.util.Optional;

public interface UserEconomyDao extends Dao<UserEconomy, Integer> {

    Optional<UserEconomy> findById(int id);

    Optional<UserEconomy> findByUser(User user);

    UserEconomy save(UserEconomy userEconomy);


}
