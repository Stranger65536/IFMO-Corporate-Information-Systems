package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.RoleRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.emc.internal.reserv.dto.FaultCode.ROLE_DOES_NOT_EXIST;
import static com.emc.internal.reserv.util.EndpointUtil.getInvalidRoleMessage;
import static com.emc.internal.reserv.util.EndpointUtil.raiseServiceFaultException;

/**
 * @author trofiv
 * @date 03.04.2017
 */
@Log4j2
public enum Roles {
    USER("user", 0),
    MODERATOR("moderator", 1),
    ADMIN("admin", 2);

    private static final Map<String, Role> INDEX = new HashMap<>(ReservationTypes.values().length);
    private final String name;
    @Getter
    private final int priority;
    @Getter
    private Role role;

    Roles(final String name, final int priority) {
        this.name = name;
        this.priority = priority;
    }

    public static Role getByName(final String name) {
        return Optional.ofNullable(INDEX.get(name)).orElseThrow(() ->
                raiseServiceFaultException(ROLE_DOES_NOT_EXIST,
                        getInvalidRoleMessage(name)));
    }

    @Component
    @SuppressWarnings("PublicInnerClass")
    public static class RoleRepositoryInjector {
        private final RoleRepository roleRepository;

        @Autowired
        public RoleRepositoryInjector(final RoleRepository roleRepository) {
            this.roleRepository = roleRepository;
        }

        @PostConstruct
        public void postConstruct() {
            for (Roles role : EnumSet.allOf(Roles.class)) {
                final Optional<Role> optionalRow = roleRepository.findOneByName(role.name);
                role.role = optionalRow.orElseThrow(() -> new ObjectNotFoundException(role.name, Role.class.getSimpleName()));
                INDEX.put(role.role.getName(), role.role);
            }
        }
    }
}
