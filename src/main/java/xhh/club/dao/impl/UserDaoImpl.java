package xhh.club.dao.impl;

import xhh.club.annotation.XhhBean;
import xhh.club.dao.UserDao;

@XhhBean
public class UserDaoImpl implements UserDao {
    @Override
    public void run() {
        System.out.println("UserDao run.......");
    }
}
