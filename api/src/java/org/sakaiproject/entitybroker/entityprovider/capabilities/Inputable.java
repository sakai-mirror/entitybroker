/**
 * $Id$
 * $URL$
 * Inputable.java - entity-broker - Apr 12, 2008 1:36:32 PM - azeckoski
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

import org.sakaiproject.entitybroker.access.EntityViewAccessProvider;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.extension.Formats;

/**
 * These entities can be entered as certain input formats which are handled automatically
 * and translated into entity objects<br/>
 * If you want to define the way the input is translated instead of using the internal methods 
 * then use {@link InputTranslatable}<br/>
 * <br/>
 * <b>NOTE:</b> By default all entity view requests go through to the available access providers: 
 * {@link EntityViewAccessProvider} or {@link HttpServletAccessProvider}
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public interface Inputable extends EntityProvider, Formats, Saveable {

   /**
    * Defines the input format types (extensions) handled by this provider<br/>
    * <b>NOTE:</b> In the case of an entity view the extension 
    * which goes on the end of an entity URL (after a ".") indicates the input type<br/>
    * <b>WARNING:</b> not including {@link #HTML} in the return will stop all redirects to the access providers
    * and therefore will cause HTML requests for entities to go nowhere
    * 
    * @return an array containing the extension formats (from {@link Formats}) handled,
    * use the constants (example: {@link #XML}) or feel free to make up your own if you like
    */
   public String[] getHandledInputFormats();

}
