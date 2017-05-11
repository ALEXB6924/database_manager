package com.nokia.dao;

import com.nokia.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@SuppressWarnings("unchecked")
@Repository
public interface RoleDao extends CrudRepository<Role, Long> {
    Role findById(Long id);

    @Override
    Role save(Role role);
}