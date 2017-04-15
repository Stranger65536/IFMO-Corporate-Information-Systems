package com.emc.internal.reserv.entity;

import com.emc.internal.reserv.repository.RoleRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
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
@Slf4j
public enum Roles {
    USER("user"),
    MODERATOR("moderator"),
    ADMIN("admin");

    private final String name;
    @Getter
    private Role role;

    Roles(final String name) {
        this.name = name;
    }

    @Component
    @SuppressWarnings("PublicInnerClass")
    public static class RoleRepositoryInjector {
        private final RoleRepository roleRepository;
        private final IdentityService identityService;

        @Autowired
        public RoleRepositoryInjector(final RoleRepository roleRepository, final IdentityService identityService) {
            this.roleRepository = roleRepository;
            this.identityService = identityService;
        }

        @PostConstruct
        public void postConstruct() {
            for (Roles role : EnumSet.allOf(Roles.class)) {
                final Optional<Role> optionalRow = roleRepository.findOneByName(role.name);
                role.role = optionalRow.orElseThrow(() -> new ObjectNotFoundException(role.name, Role.class.getSimpleName()));
            }
        }
    }
}
