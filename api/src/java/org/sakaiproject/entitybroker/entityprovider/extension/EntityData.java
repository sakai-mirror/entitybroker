/**
 * $Id$
 * $URL$
 * EntityData.java - data-broker - Aug 3, 2008 6:03:53 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakaiproject.entitybroker.entityprovider.extension;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Resolvable;


/**
 * This is an object to hold data from a search which would normally return data references,
 * This is basically a POJO which allows us to return a few results instead of only the reference,
 * it helps us get the data back more efficiently and makes it easier on developers who
 * need to search for entities
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class EntityData {

    /**
     * (OPTIONAL - may be null)
     * This is the data object itself (if there is one),
     * this is included at the discretion of the data provider author,
     * if this is null then the data data is not available or would be prohibitively large (i.e. typically left out for efficiency)
     */
    private Object data;
    public void setData(Object entity) {
        this.data = entity;
//      if (data != null) {
//      this.entity = new WeakReference<Object>(data);
//      } else {
//      this.entity = null;
//      }
    }
    /**
     * (OPTIONAL - may be null)
     * This is the data object itself (if there is one),
     * this is included at the discretion of the data provider author,
     * if this is null then the data data is not available or would be prohibitively large (i.e. typically left out for efficiency)
     */
    public Object getData() {
        return this.data;
//      if (this.entity == null) {
//      return null;
//      } else {
//      return this.entity.get();
//      }
    }

    private String entityId = null;
    public String getEntityId() {
        return entityId;
    }

    /**
     * The data reference -  a globally unique reference to an data, 
     * consists of the data prefix and optional segments (normally the id at least),
     * this should be set by the constructor only
     */
    private String entityReference;
    /**
     * The data reference -  a globally unique reference to an data, 
     * consists of the data prefix and optional segments (normally the id at least)
     */
    public String getEntityReference() {
        return entityReference;
    }

    /**
     * The data reference object which makes it easy to get to the prefix or id of this data,
     * this should be set by the constructor only
     */
    private transient EntityReference entityRef;
    /**
     * The data reference object which makes it easy to get to the prefix or id of this data is needed
     */
    public EntityReference getEntityRef() {
        return entityRef;
    }

    /**
     * A string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     */
    private String entityDisplayTitle;
    /**
     * A string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     */
    public void setDisplayTitle(String displayTitle) {
        this.entityDisplayTitle = displayTitle;
    }
    /**
     * A string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     */
    public String getDisplayTitle() {
        if (this.entityDisplayTitle == null) {
            return this.entityRef.getPrefix() + " : " + entityReference;
        }
        return entityDisplayTitle;
    }
    /**
     * @return true if the display title is actually set, false if it is null and will return an autogenerated value
     */
    public boolean isDisplayTitleSet() {
        return entityDisplayTitle != null;
    }

    /**
     * (OPTIONAL - may be null)
     * The entityURL to the data represented by this reference,
     * should be an absolute entityURL (server name optional),
     * if this is null then the entityURL is formed from the reference
     */
    private String entityURL;
    /**
     * WARNING: for internal use only
     * @param url the url to access this data
     */
    public void setEntityURL(String url) {
        entityURL = url;
    }
    /**
     * The entityURL to the data represented by this reference,
     * should be an absolute entityURL (server name optional)
     */
    public String getEntityURL() {
        return entityURL;
    }

    /**
     * (OPTIONAL - may be null)
     * A set of properties to return along with the data information,
     * this may be presented and used for filtering,
     * this will be null or empty if it is not used
     */
    private Map<String, Object> entityProperties;
    /**
     * (OPTIONAL - may be null)
     * A set of properties to return along with the data information,
     * this may be presented and used for filtering,
     * should be null or empty if not used
     * @param entityProperties a map of property name => value
     */
    public void setEntityProperties(Map<String, Object> entityProperties) {
        this.entityProperties = entityProperties;
    }
    /**
     * A set of properties to return along with the data information,
     * this may be presented and used for filtering,
     * this will be empty if it is not used
     */
    public Map<String, Object> getEntityProperties() {
        if (entityProperties == null) {
            entityProperties = new HashMap<String, Object>(0);
        }
        return entityProperties;
    }

    /**
     * used to ensure that we do not accidently attempt to populate this twice
     */
    private transient boolean populated = false;
    /**
     * FOR INTERNAL USE ONLY - do not use
     */
    public void setPopulated(boolean populated) {
        this.populated = populated;
    }
    /**
     * @return true if this object was populated, false otherwise
     */
    public boolean isPopulated() {
        return populated;
    }

    /**
     * Minimal constructor - used for most basic cases
     * Use the setters to add in properties or the data if desired
     * 
     * @param reference a globally unique reference to an data, 
     * consists of the data prefix and id (e.g. /prefix/id)
     * @param entityDisplayTitle a string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     */
    public EntityData(String reference, String displayTitle) {
        this(reference, displayTitle, null, null);
    }

    /**
     * Basic constructor
     * Use this to construct a search result using the typical minimal amount of information,
     * Use the setters to add in properties or the data if desired
     * 
     * @param reference a globally unique reference to an data, 
     * consists of the data prefix and id (e.g. /prefix/id)
     * @param entityDisplayTitle a string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     * @param data an data object, see {@link Resolvable}
     */
    public EntityData(String reference, String displayTitle, Object entity) {
        this(reference, displayTitle, entity, null);
    }

    /**
     * Full constructor
     * Use this if you want to return the data itself along with the key meta data and properties
     * 
     * @param reference a globally unique reference to an data, 
     * consists of the data prefix and id (e.g. /prefix/id)
     * @param entityDisplayTitle a string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     * @param data an data object, see {@link Resolvable}
     * @param entityProperties a set of properties to return along with the data information,
     * this may be presented and used for filtering,
     */
    public EntityData(String reference, String displayTitle, Object entity, Map<String, Object> entityProperties) {
        this.entityRef = new EntityReference(reference);
        this.entityReference = this.entityRef.getReference();
        this.entityId = this.entityRef.getId();
        this.entityDisplayTitle = displayTitle;
        this.entityURL = EntityView.DIRECT_PREFIX + this.entityReference;
        setData(entity);
        setEntityProperties(entityProperties);
    }


    /**
     * Minimal constructor - used for most basic cases
     * Use the setters to add in properties or the data if desired
     * 
     * @param ref an object which represents a globally unique reference to an data, 
     * consists of the data prefix and id
     * @param entityDisplayTitle a string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     */
    public EntityData(EntityReference ref, String displayTitle) {
        this(ref, displayTitle, null, null);
    }

    /**
     * Basic constructor
     * Use this to construct a search result using the typical minimal amount of information,
     * Use the setters to add in properties or the data if desired
     * 
     * @param ref an object which represents a globally unique reference to an data, 
     * consists of the data prefix and id
     * @param entityDisplayTitle a string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     * @param data an data object, see {@link Resolvable}
     */
    public EntityData(EntityReference ref, String displayTitle, Object entity) {
        this(ref, displayTitle, entity, null);
    }

    /**
     * Full constructor
     * Use this if you want to return the data itself along with the key meta data and properties
     * 
     * @param ref an object which represents a globally unique reference to an data, 
     * consists of the data prefix and id
     * @param entityDisplayTitle a string which is suitable for display and provides a short summary of the data,
     * typically 100 chars or less, this may the name or title of the data represented by an data
     * @param data an data object, see {@link Resolvable}
     * @param entityProperties a set of properties to return along with the data information,
     * this may be presented and used for filtering,
     */
    public EntityData(EntityReference ref, String displayTitle,
            Object entity, Map<String, Object> entityProperties) {
        if (ref == null || ref.isEmpty()) {
            throw new IllegalArgumentException("reference object cannot be null and must have values set");
        }
        this.entityRef = ref;
        this.entityReference = this.entityRef.getReference();
        this.entityId = this.entityRef.getId();
        this.entityDisplayTitle = displayTitle;
        this.entityURL = EntityView.DIRECT_PREFIX + this.entityReference;
        this.entityDisplayTitle = displayTitle;
        setData(entity);
        setEntityProperties(entityProperties);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof EntityData))
            return false;
        else {
            EntityData castObj = (EntityData) obj;
            if (null == this.entityReference || null == castObj.entityReference)
                return false;
            else
                return (this.entityReference.equals(castObj.entityReference));
        }
    }

    @Override
    public int hashCode() {
        String hashStr = this.getClass().getName() + ":" + this.entityReference.hashCode();
        return hashStr.hashCode();
    }

    @Override
    public String toString() {
        return "ED: ref="+entityReference+":display="+entityDisplayTitle+":url="+entityURL+":props("+getEntityProperties().size()+"):data="+data;
    }

    public static class ReferenceComparator implements Comparator<EntityData> {
        public int compare(EntityData o1, EntityData o2) {
            return o1.entityReference.compareTo(o2.entityReference);
        }
    }

}
