package com.project.JWT;

import com.project.POJO.User;
import com.project.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CustomerUsersDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private User userDetail;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        userDetail = userDao.findByEmail(userName);
        if (!Objects.isNull(userDetail)) {
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        } else
            throw new UsernameNotFoundException("User not found");
    }

    public User getUserDetail() {
        return userDetail;
    }
}
