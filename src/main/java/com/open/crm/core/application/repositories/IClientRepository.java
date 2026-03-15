package com.open.crm.core.application.repositories;

import org.springframework.stereotype.Repository;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.client.Client;
import java.util.List;

@Repository
public interface IClientRepository extends IRepository<Client> {
    List<Client> findByEmailAndIsDeleted(String email, boolean isDeleted);

    List<Client> findByPhoneNumberAndIsDeleted(String phoneNumber, boolean isDeleted);
}
