package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.ActionStatusRepository;
import lombok.Getter;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.Optional;

/**
 * @author trofiv
 * @date 03.04.2017
 */
public enum ActionStatuses {
    APPROVED("Approved"),
    CANCELED("Canceled"),
    WAITING_FOR_APPROVAL("Waiting for approval"),
    NEW_TIME_PROPOSED("New time proposed");

    private final String name;
    @Getter
    private ActionStatus actionStatus;

    ActionStatuses(final String name) {
        this.name = name;
    }

    @Component
    @SuppressWarnings("PublicInnerClass")
    public static class ActionStatusRepositoryInjector {
        private final ActionStatusRepository actionStatusRepository;

        @Autowired
        public ActionStatusRepositoryInjector(final ActionStatusRepository actionStatusRepository) {
            this.actionStatusRepository = actionStatusRepository;
        }

        @PostConstruct
        public void postConstruct() {
            for (ActionStatuses status : EnumSet.allOf(ActionStatuses.class)) {
                final Optional<ActionStatus> optionalRow = actionStatusRepository.findOneByName(status.name);
                status.actionStatus = optionalRow.orElseThrow(() -> new ObjectNotFoundException(status.name, ActionStatus.class.getSimpleName()));
            }
        }
    }
}
