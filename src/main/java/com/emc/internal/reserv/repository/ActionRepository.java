package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.Action;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
public interface ActionRepository extends CrudRepository<Action, Long> {
}
