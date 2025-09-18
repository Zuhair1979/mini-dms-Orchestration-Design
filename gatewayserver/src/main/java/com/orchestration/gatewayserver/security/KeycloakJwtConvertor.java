package com.orchestration.gatewayserver.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class KeycloakJwtConvertor {

public static JwtAuthenticationConverter customConvertor(){

    // to get default scope
    /*{
        "sub": "user123",
            "scope": "read write",
            "realm_access": { "roles": ["admin", "viewer"] }
            defaultscope SCOPE_READ, SCOPE_WRITE, IT IS DELEGATED BY JwtAuthenticationConverter TO GET THE SCOP
    }*/
    JwtGrantedAuthoritiesConverter defaultScope=new JwtGrantedAuthoritiesConverter();

    JwtAuthenticationConverter converter= new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(jwt ->{

        // create Collection of Grand authority
        // first add scop
        Collection<GrantedAuthority> authorityCollection=defaultScope.convert(jwt);

        // now get the roles from real.roles and convert it to role_Role and add it to authorityCollection

        Map<String,Object> realmAccess=(Map<String,Object>) jwt.getClaim("realm_access");

        if (realmAccess != null && realmAccess.containsKey("roles")){
            List<String> roles = (List<String>) realmAccess.get("roles");
            authorityCollection.addAll(
                    roles.stream()
                            .map(role -> "ROLE_" + role) // add ROLE_ prefix
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
            );
        }

        return authorityCollection;

            }

    );
return converter;


}

    }
