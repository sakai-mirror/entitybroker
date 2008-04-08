/**
 * $Id$
 * $URL$
 * RequestInterceptor.java - entity-broker - Apr 8, 2008 8:42:28 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Centre for Applied Research in Educational Technologies, University of Cambridge
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.entityprovider.capabilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.exception.EntityException;


/**
 * Allows actions to be taken before a direct request is handled or after it has been handled,
 * will only affect requests coming in via the direct servlet
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public interface RequestInterceptor extends EntityProvider {

   /**
    * Take actions before the request is handled,
    * this will be called just before each request is sent to the correct request handler,
    * this might be used to add information to the response before it goes on to be handled
    * or to take some action as a result of information in the request or reference,<br/>
    * if you want to interrupt the handling of this request (stop it) then throw an
    * {@link EntityException} and include the type of response you would like to return in the exception
    * (this can be a success or failure response status)
    * 
    * @param ref an entity reference
    * @param req the servlet request (available in case you need to get anything out of it)
    * @param res the servlet response, put the correct data response into the outputstream
    */
   public void before(HttpServletRequest req, HttpServletResponse res, EntityReference ref);

   /**
    * Take actions after the request is handled,
    * this will be called just before each response is sent back to the requester,
    * normally this would be used to add something to the response as it is getting ready to be
    * sent back to the requester
    * 
    * @param ref an entity reference
    * @param req the servlet request (available in case you need to get anything out of it)
    * @param res the servlet response, put the correct data response into the outputstream
    */
   public void after(HttpServletRequest req, HttpServletResponse res, EntityReference ref);

}
