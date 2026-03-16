package com.open.crm.admin.application.interfaces;

import com.open.crm.admin.entities.user.User;

public interface ISecurityGateway {
  void refreshAccessUser(User user);
}
