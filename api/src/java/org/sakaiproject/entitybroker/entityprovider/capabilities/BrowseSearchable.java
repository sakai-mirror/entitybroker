/**
 * $Id$
 * $URL$
 * BrowseSearchable.java - entity-broker - Aug 3, 2008 9:24:50 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.entityprovider.capabilities;

import java.util.List;
import java.util.Map;

import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.extension.EntityData;
import org.sakaiproject.entitybroker.entityprovider.search.Search;


/**
 * This indicates that this entity will participate in browse functionality for entities,
 * For example, it will provide lists of entities which are visible to users in locations
 * which can be looked through and selected<br/>
 * This provides fine grained control over which entities will appear in a browse list,
 * normally {@link CollectionResolvable} should show all entities, however, for the browse list
 * we will explicitly filter by users and/or locations and may not show all entities,
 * entities which do not implement this or {@link Browseable} will not appear in lists of entities which are being browsed<br/>
 * This is one of the capability extensions for the {@link EntityProvider} interface<br/>
 * This is the configuration interface for {@link Browseable} (the convention interface)
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface BrowseSearchable extends EntityProvider {

   /**
    * Returns the list of entities which are being browsed based on the given parameters
    * 
    * @param search a search object which can define the order to return entities,
    * search filters, and total number of entities returned, may be empty but will not be null,
    * implementors are encouraged to support ordering and limiting of the number of returned results at least
    * @param userReference (optional) the unique entity reference for a user which is browsing the results, 
    * this may be null to indicate that only items which are visible to all users should be shown
    * @param associatedReference (optional) 
    *           a globally unique reference to an entity, this is the entity that the 
    *           returned browseable data must be associated with (e.g. limited by reference to a location or associated entity), 
    *           this may be null to indicate there is no association limit
    * @param params (optional) incoming set of parameters which may be used to send data specific to this request, may be null
    * @return a list of search result objects which contain the reference, URL, display title and optionally other entity data
    * @throws SecurityException if the data cannot be accessed by the current user or is not publicly accessible
    * @throws IllegalArgumentException if the reference is invalid or the search is invalid
    * @throws IllegalStateException if any other error occurs
    */
   public List<EntityData> browseEntities(Search search, String userReference, String associatedReference, Map<String, Object> params);

}
