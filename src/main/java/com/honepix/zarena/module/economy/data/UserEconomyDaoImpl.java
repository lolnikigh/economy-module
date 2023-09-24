package com.honepix.zarena.module.economy.data;

import com.honepix.userapi.data.User;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserEconomyDaoImpl extends BaseDaoImpl<UserEconomy, Integer> implements UserEconomyDao {

    public UserEconomyDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, UserEconomy.class);
        TableUtils.createTableIfNotExists(connectionSource, UserEconomy.class);
        setObjectCache(ReferenceObjectCache.makeSoftCache());
    }

    @Override
    public Optional<UserEconomy> findById(int id) {
        UserEconomy userEconomy;
        try {
            userEconomy = super.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(userEconomy);
    }

    @Override
    public Optional<UserEconomy> findByUser(User user) {
        List<UserEconomy> userEconomy;
        try {
            userEconomy = super.queryBuilder().where()
                    .eq("user_id", user.getId()).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (userEconomy.isEmpty()) return Optional.empty();
        return Optional.ofNullable(userEconomy.get(0));
    }

    @Override
    public UserEconomy save(UserEconomy userEconomy) {
        try {
            int id = super.extractId(userEconomy);
            boolean exists = super.idExists(id);
            if (exists) super.update(userEconomy);
            else userEconomy = super.createIfNotExists(userEconomy);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEconomy;
    }

}
