package com.mitrais.rms.dao.impl;

import com.mitrais.rms.dao.DataSourceFactory;
import com.mitrais.rms.dao.UserDao;
import com.mitrais.rms.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class UserDaoImpl implements UserDao
{
    @Override
    public Optional<User> find(Long id)
    {
        try (Connection connection = DataSourceFactory.getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE id=?");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                User user = new User(rs.getLong("id"), rs.getString("user_name"), rs.getString("password"));
                return Optional.of(user);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll()
    {
        List<User> result = new ArrayList<>();
        try (Connection connection = DataSourceFactory.getConnection())
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user");
            while (rs.next())
            {
                User user = new User(rs.getLong("id"), rs.getString("user_name"), rs.getString("password"));
                result.add(user);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean save(User user)
    {
        try (Connection connection = DataSourceFactory.getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO user VALUES (NULL, ?, ?)");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            int i = stmt.executeUpdate();
            if(i == 1) {
                return true;
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(User user)
    {
        try (Connection connection = DataSourceFactory.getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("UPDATE user SET user_name=?, password=? WHERE id=?");
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());
            int i = stmt.executeUpdate();
            if(i == 1) {
                return true;
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(User user)
    {
        try (Connection connection = DataSourceFactory.getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM user WHERE id=?");
            stmt.setLong(1, user.getId());
            int i = stmt.executeUpdate();
            if(i == 1) {
                return true;
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<User> findByUserName(String userName)
    {
        try (Connection connection = DataSourceFactory.getConnection())
        {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE user_name=?");
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                User user = new User(rs.getLong("id"), rs.getString("user_name"), rs.getString("password"));
                return Optional.of(user);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    private static class SingletonHelper
    {
        private static final UserDaoImpl INSTANCE = new UserDaoImpl();
    }

    public static UserDao getInstance()
    {
        return SingletonHelper.INSTANCE;
    }
}
