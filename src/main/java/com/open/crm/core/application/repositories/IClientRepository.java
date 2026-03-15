package com.open.crm.core.application.repositories;

import org.springframework.stereotype.Repository;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.client.Client;

@Repository
public interface IClientRepository extends IRepository<Client> {

}
