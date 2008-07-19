/**
 * $Id$
 * $URL$
 * SessionEntityProvider.java - entity-broker - Jul 15, 2008 4:03:52 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.providers;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.CRUDable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Inputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Outputable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.RequestAware;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;
import org.sakaiproject.entitybroker.entityprovider.extension.RequestGetter;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * Entity provider for Sakai Sessions
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class SessionEntityProvider implements EntityProvider, CRUDable, Inputable, Outputable, RequestAware, Describeable, AutoRegisterEntityProvider {

   public static String AUTH_USERNAME = "_username";
   public static String AUTH_PASSWORD = "_password";

   public DeveloperHelperService developerHelperService;
   public void setDeveloperHelperService(DeveloperHelperService developerHelperService) {
      this.developerHelperService = developerHelperService;
   }

   public SessionManager sessionManager;
   public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   public UserDirectoryService userDirectoryService;
   public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
      this.userDirectoryService = userDirectoryService;
   }

   public static String PREFIX = "session";
   public String getEntityPrefix() {
      return PREFIX;
   }

   public String createEntity(EntityReference ref, Object entity) {
      EntitySession es = (EntitySession) entity;
      String newSessionId = null;
      Session currentSession = null;
      if (developerHelperService.isUserAdmin(developerHelperService.getCurrentUserReference())) {
         // create the session with the given settings
         if (es.getUserId() == null || es.getUserId().equals("")) {
            throw new IllegalArgumentException("UserId must be set when creating a session");
         }
         User u = null;
         try {
            u = userDirectoryService.getUser(es.getUserId());
         } catch (UserNotDefinedException e) {
            throw new IllegalArgumentException("Invalid userId provided in session object, could not find user with that id: " + es.getUserId());
         }
         currentSession = sessionManager.startSession(es.getUserId());
         currentSession.setUserEid(u.getEid());
      } else {
         // when creating a new session we need some data from the request
         HttpServletRequest req = requestGetter.getRequest();
         if (req == null) {
            throw new IllegalStateException("Only super admins can create sessions without using a REST request currently");
         } else {
            String username = req.getParameter(AUTH_USERNAME);
            String password = req.getParameter(AUTH_PASSWORD);
            if (username == null || username.equals("") 
                  || password == null || password.equals("")) {
               throw new IllegalArgumentException("A session entity cannot be created without providing the username and password, " 
                     + "the username must be provided as '_username' and the password as '_password' in the POST");
            }
            // now we auth
            User u = userDirectoryService.authenticate(username, password);
            if (u == null) {
               throw new SecurityException("The username or password provided were invalid, could not authenticate user ("+username+") to create a session");
            }
            // create session or update existing one
            currentSession = sessionManager.getCurrentSession();
            if (currentSession == null) {
               // start a session if none is around
               currentSession = sessionManager.startSession(u.getId());
            }
            currentSession.setUserId(u.getId());
            currentSession.setUserEid(u.getEid());
         }
      }

      // set fields from the passed in session object
      if (es.getMaxInactiveInterval() > 0) {
         currentSession.setMaxInactiveInterval(es.getMaxInactiveInterval());
      }

      newSessionId = currentSession.getId();
      return newSessionId;
   }

   public Object getSampleEntity() {
      return new EntitySession();
   }

   public void updateEntity(EntityReference ref, Object entity) {
      String sessionId = ref.getId();
      if (sessionId == null) {
         throw new IllegalArgumentException("Cannot update session, No sessionId in provided reference: " + ref);
      }
      Session s = sessionManager.getSession(sessionId);
      if (s == null) {
         throw new IllegalArgumentException("Cannot find session to update with id: " + sessionId);
      }
      checkSessionOwner(s);
      // this simply causes the session to remain active, other changes are not allowed
      s.setActive();
   }

   public Object getEntity(EntityReference ref) {
      if (ref.getId() == null) {
         return new EntitySession();
      }
      String sessionId = ref.getId();
      Session s = sessionManager.getSession(sessionId);
      if (s == null) {
         throw new IllegalArgumentException("Cannot find session with id: " + sessionId);
      }
      EntitySession es = new EntitySession(s);
      return es;
   }

   public void deleteEntity(EntityReference ref) {
      String sessionId = ref.getId();
      if (sessionId == null) {
         throw new IllegalArgumentException("Cannot update session, No sessionId in provided reference: " + ref);
      }
      Session s = sessionManager.getSession(sessionId);
      if (s == null) {
         throw new IllegalArgumentException("Cannot find session with id: " + sessionId);
      }
      checkSessionOwner(s);
      s.invalidate();
   }

   public String[] getHandledInputFormats() {
      return new String[] { Formats.HTML, Formats.XML, Formats.JSON };
   }

   public String[] getHandledOutputFormats() {
      return new String[] { Formats.XML, Formats.JSON };
   }


   private RequestGetter requestGetter;
   public void setRequestGetter(RequestGetter requestGetter) {
      this.requestGetter = requestGetter;
   }

   /**
    * Checks if the current user can modify the session
    * @param s the session
    */
   private void checkSessionOwner(Session s) {
      String currentUser = developerHelperService.getCurrentUserReference();
      String currentUserId = developerHelperService.getUserIdFromRef(currentUser);
      if (developerHelperService.isUserAdmin(currentUser)) {
         return;
      } else {
         String userId = s.getUserId();
         if (userId.equals(currentUserId)) {
            return;
         }
      }
      throw new SecurityException("Current user ("+currentUser+") cannot modify this session: " + s.getId() + ", they are not the owner or not an admin");
   }

}
