package com.emc.internal.reserv.repository;

import com.emc.internal.reserv.entity.LoginAttempt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author trofiv
 * @date 05.03.2017
 */
@Repository
public interface LoginAttemptRepository extends CrudRepository<LoginAttempt, Long> {
}
