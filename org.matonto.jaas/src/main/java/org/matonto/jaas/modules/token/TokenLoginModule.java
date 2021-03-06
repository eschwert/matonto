package org.matonto.jaas.modules.token;

/*-
 * #%L
 * org.matonto.jaas
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 iNovex Information Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.apache.karaf.jaas.boot.principal.GroupPrincipal;
import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.apache.karaf.jaas.boot.principal.UserPrincipal;
import org.apache.karaf.jaas.modules.AbstractKarafLoginModule;
import org.apache.karaf.jaas.modules.properties.PropertiesBackingEngine;
import org.apache.log4j.Logger;
import org.matonto.jaas.config.LoginModuleConfig;
import org.matonto.jaas.utils.TokenUtils;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;


public class TokenLoginModule extends AbstractKarafLoginModule {

    private static final Logger LOG = Logger.getLogger(TokenLoginModule.class.getName());

    private String usersFileString;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        super.initialize(subject, callbackHandler, options);
        usersFileString = options.get(LoginModuleConfig.USERS_FILE) + "";
        LOG.debug("Initialized TokenLoginModule usersFileString=" + usersFileString);
    }

    @Override
    public boolean login() throws LoginException {
        LOG.debug("Verifying token...");

        File usersFile = new File(usersFileString);
        Properties users = new Properties();
        try {
            users.load(new FileInputStream(usersFile));
        } catch (IOException ioe) {
            String msg = "Unable to load user properties file " + usersFile;
            LOG.debug(msg);
            throw new LoginException(msg);
        }

        Callback[] callbacks = new Callback[1];
        callbacks[0] = new TokenCallback();

        try {
            callbackHandler.handle(callbacks);
        } catch (IOException ioe) {
            LOG.debug(ioe.getMessage());
            throw new LoginException(ioe.getMessage());
        } catch (UnsupportedCallbackException uce) {
            String msg = uce.getMessage() + " not available to obtain information from user";
            LOG.debug(msg);
            throw new LoginException(msg);
        }

        String tokenString = ((TokenCallback) callbacks[0]).getTokenString();
        if (tokenString == null) {
            String msg = "Unable to retrieve token string";
            LOG.debug(msg);
            throw new FailedLoginException(msg);
        }

        Optional<SignedJWT> tokenOptional;
        try {
            tokenOptional = TokenUtils.verifyToken(tokenString);
        } catch (ParseException e) {
            String msg = "Problem parsing JWT";
            LOG.debug(msg);
            throw new FailedLoginException(msg);
        } catch (JOSEException e) {
            String msg = "Problem verifying JWT";
            LOG.debug(msg);
            throw new FailedLoginException(msg);
        }

        if (!tokenOptional.isPresent()) {
            if (!this.detailedLoginExcepion) {
                String msg = "Login failed";
                LOG.debug(msg);
                throw new FailedLoginException(msg);
            } else {
                String msg = "Token not verified";
                LOG.debug(msg);
                throw new FailedLoginException(msg);
            }
        }

        LOG.debug("Token found and verified.");
        SignedJWT token = tokenOptional.get();

        String user;
        try {
            user = token.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            String msg = "Problem parsing JWT";
            LOG.debug(msg);
            throw new FailedLoginException(msg);
        }

        // user infos container read from the users properties file
        String userInfos = users.getProperty(user);
        if (userInfos == null) {
            if (!this.detailedLoginExcepion) {
                throw new FailedLoginException("Login failed");
            } else {
                throw new FailedLoginException("User " + user + " does not exist");
            }
        }

        String[] infos = userInfos.split(",");

        principals = new HashSet<>();
        principals.add(new UserPrincipal(user));
        for (int i = 1; i < infos.length; i++) {
            if (infos[i].trim().startsWith(PropertiesBackingEngine.GROUP_PREFIX)) {
                // it's a group reference
                principals.add(new GroupPrincipal(infos[i].trim().substring(PropertiesBackingEngine.GROUP_PREFIX.length())));
                String groupInfo = users.getProperty(infos[i].trim());
                if (groupInfo != null) {
                    String[] roles = groupInfo.split(",");
                    for (int j = 1; j < roles.length; j++) {
                        principals.add(new RolePrincipal(roles[j].trim()));
                    }
                }
            } else {
                // it's an user reference
                principals.add(new RolePrincipal(infos[i].trim()));
            }
        }

        users.clear();

        LOG.debug("Successfully logged in " + user);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        clear();
        LOG.debug("Abort");
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        LOG.debug("Logout");
        return true;
    }
}
