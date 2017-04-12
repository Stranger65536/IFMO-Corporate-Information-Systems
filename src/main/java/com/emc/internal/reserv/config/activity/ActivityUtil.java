package com.emc.internal.reserv.config.activity;

import com.emc.internal.reserv.entity.Role;
import com.emc.internal.reserv.entity.User;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;

/**
 * @author trofiv
 * @date 11.04.2017
 */
public class ActivityUtil {
    public static final String DEFAULT_GROUP_TYPE = "security-type";

    public static Group roleToActivitiGroup(final Role role) {
        final Group activitiGroup = new GroupEntity();
        activitiGroup.setId(Integer.toString(role.getId()));
        activitiGroup.setName(role.getName());
        activitiGroup.setType(DEFAULT_GROUP_TYPE);
        return activitiGroup;
    }

    public static org.activiti.engine.identity.User userToActivitiUser(final User user) {
        final UserEntity activitiUser = new UserEntity();
        activitiUser.setId(Integer.toString(user.getId()));
        activitiUser.setPassword(user.getPassword());
        activitiUser.setFirstName(user.getFirstName());
        activitiUser.setLastName(user.getLastName());
        activitiUser.setEmail(user.getEmail());
        return activitiUser;
    }

    public static IdentityInfoEntity userToActivitiIdentityInfo(final User user) {
        final IdentityInfoEntity activitiIdentity = new IdentityInfoEntity();
        activitiIdentity.setId(Integer.toString(user.getId()));
        activitiIdentity.setPassword(user.getPassword());
        return activitiIdentity;
    }
}
