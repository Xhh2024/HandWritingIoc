package xhh.club.service.impl;

import xhh.club.annotation.XhhBean;
import xhh.club.annotation.XhhDI;
import xhh.club.dao.UserDao;
import xhh.club.service.UserService;

@XhhBean
public class UserServiceImpl implements UserService {

    @XhhDI
    private UserDao userDao;

    @Override
    public void run() {
        System.out.println("UserService run.......");
        userDao.run();
    }
}
