/**
 * $Id$
 * $URL$
 * EntityBrokerDao.java - entity-broker - May 3, 2008 4:46:19 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Centre for Applied Research in Educational Technologies, University of Cambridge
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.dao;

import java.util.List;

import org.sakaiproject.genericdao.api.GeneralGenericDao;
import org.sakaiproject.genericdao.api.search.Search;

/**
 * Interface for internal proxy only
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public interface EntityBrokerDao extends GeneralGenericDao {

   /**
    * Get a list of unique entity references for a set of search params, all lists must be the same
    * size
    * 
    * @param properties
    *           the persistent object properties
    * @param values
    *           the values to match against the properties
    * @param comparisons
    *           the type of comparisons to make between property and value
    * @param relations
    *           the relation to the previous search param (must be "and" or "or") - note that the
    *           first relation is basically thrown away
    * @return a list of unique {@link String}s which represent entity references
    */
   public List<String> getEntityRefsForSearch(List<String> properties, List<String> values,
         List<Integer> comparisons, List<String> relations);

   /**
    * Remove properties from an entity without wasting time doing a lookup first
    * 
    * @param entityReference
    *           unique reference to an entity
    * @param name
    *           the name of the property to remove, leaving this null will remove all properties
    * @return the number of properties removed
    */
   public int deleteProperties(String entityReference, String name);

   /**
    * Get all entity refs which match the given search and either match all tags or any tag
    * 
    * @param search should include restrictions on tags and optionally prefixes,
    * may optionally include order by tag, prefix, or ref
    * @param matchAll if true then all tags must match, if false then any tag can match
    * @return the list of refs
    */
   public List<String> getEntityRefsForTags(Search search, boolean matchAll);

   /**
    * Remove all given tags from an entity reference
    * @param entityReference
    * @param tags
    * @return the number of tags removed
    */
   public int deleteTags(String entityReference, String[] tags);

}
