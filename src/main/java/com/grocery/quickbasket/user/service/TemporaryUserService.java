package com.grocery.quickbasket.user.service;

import com.grocery.quickbasket.user.entity.TemporaryUser;

public interface TemporaryUserService {
    void saveTemporaryUser(TemporaryUser temporaryUser);
    TemporaryUser getTemporaryUser(String verificationToken);
    void deleteTemporaryUser(String verificationToken);
}
