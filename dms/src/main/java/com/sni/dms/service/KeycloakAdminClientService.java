package com.sni.dms.service;

import com.sni.dms.configuration.KeycloakProvider;
import com.sni.dms.entities.UserEntity;
import com.sni.dms.requests.CreateUserRequest;
import com.sni.dms.services.UserService;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;


@Service
public class KeycloakAdminClientService {
    @Value("${keycloak.realm}")
    public String realm;

    private final KeycloakProvider kcProvider;


    public KeycloakAdminClientService(KeycloakProvider keycloakProvider,UserService userService) {
        this.kcProvider = keycloakProvider;
    }

    public Response createKeycloakUser(CreateUserRequest user) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstname());
       kcUser.setLastName(user.getLastname());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);



        Response response = usersResource.create(kcUser);

        if (response.getStatus() == 201) {
            //If you want to save the user to your other database, do it here, for example:
//            User localUser = new User();
//            localUser.setFirstName(kcUser.getFirstName());
//            localUser.setLastName(kcUser.getLastName());
//            localUser.setEmail(user.getEmail());
//            localUser.setCreatedDate(Timestamp.from(Instant.now()));
//            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
//            usersResource.get(userId).sendVerifyEmail();
//            userRepository.save(localUser);
        }

        return response;

    }
    public boolean updateKeyCloakUser(UserEntity user){
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credential =createPasswordCredentials(user.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
//        kcUser.setFirstName(user.getFirstname());
//        kcUser.setLastName(user.getLastname());
//        kcUser.setEmail(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credential));
        if(UserService.getKeyCloakUser(user.getUsername())!=null) {
            usersResource.get(UserService.getKeyCloakUser(user.getUsername()).getId()).update(kcUser);

            UserRepresentation userRepresentation = UserService.getKeyCloakUser(user.getUsername());
            UsersResource users = UserService.getUsersFromKeyCloak();
            UserResource userResource = users.get(userRepresentation.getId());

            List<RoleRepresentation> oldRoles = userResource.roles().realmLevel().listAll();

            userResource.roles().realmLevel().remove(oldRoles);


            userResource.roles().realmLevel().add(UserService.getRealmRole(user.getRole()));
            return true;
        }
        else{
            return false;
        }
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;

    }


}