/******************************************************************************
 * EntityBrokerManagerImpl.java - created by aaronz on 11 May 2007
 *****************************************************************************/

package org.sakaiproject.entitybroker.impl.entityprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ReferenceParseable;
import org.sakaiproject.entitybroker.impl.BlankReferenceParseable;
import org.sakaiproject.entitybroker.impl.util.ReflectUtil;

/**
 * Base implementation of the entity provider manager
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
public class EntityProviderManagerImpl implements EntityProviderManager {

   private static Log log = LogFactory.getLog(EntityProviderManagerImpl.class);

   // placeholder value indicating that this reference is parsed internally
   private static ReferenceParseable internalRP = new BlankReferenceParseable();

   // writeable maps
   private ConcurrentMap<String, EntityProvider> prefixWriteMap = new ConcurrentHashMap<String, EntityProvider>();
   private ConcurrentMap<String, ReferenceParseable> parseWriteMap = new ConcurrentHashMap<String, ReferenceParseable>();

   // readonly maps
   private Map<String, EntityProvider> prefixMap = new HashMap<String, EntityProvider>();
   private Map<String, ReferenceParseable> parseMap = new HashMap<String, ReferenceParseable>();

   public void init() {
      log.info("init");
   }

   private String getBiKey(String prefix, Class<? extends EntityProvider> clazz) {
      return prefix + "/" + clazz.getName();
   }

   private String getPrefix(String bikey) {
      int slashpos = bikey.indexOf('/');
      return bikey.substring(0, slashpos);
   }

   public EntityProvider getProviderByReference(String reference) {
      String prefix = EntityReference.getPrefix(reference);
      return getProviderByPrefix(prefix);
   }

   public EntityProvider getProviderByPrefix(String prefix) {
      EntityProvider provider = getProviderByPrefixAndCapability(prefix, CoreEntityProvider.class);
      if (provider == null) {
         provider = getProviderByPrefixAndCapability(prefix, EntityProvider.class);
      }
      return provider;
   }

   public EntityProvider getProviderByPrefixAndCapability(String prefix,
         Class<? extends EntityProvider> capability) {
      if (capability == null) {
         throw new NullPointerException("capability cannot be null");
      }
      if (ReferenceParseable.class.isAssignableFrom(capability)) {
         return parseMap.get(prefix);
      }
      String bikey = getBiKey(prefix, capability);
      return prefixMap.get(bikey);
   }

   public Set<String> getRegisteredPrefixes() {
      Set<String> togo = new HashSet<String>();
      for (String bikey : prefixMap.keySet()) {
         togo.add(getPrefix(bikey));
      }
      return togo;
   }

   /**
    * Get the capabilities implemented by this provider
    * 
    * @param provider
    * @return
    */
   @SuppressWarnings("unchecked")
   private static List<Class<? extends EntityProvider>> getCapabilities(EntityProvider provider) {
      List<Class<?>> superclasses = ReflectUtil.getSuperclasses(provider.getClass());
      Set<Class<? extends EntityProvider>> capabilities = new HashSet<Class<? extends EntityProvider>>();

      for (Class<?> superclazz : superclasses) {
         if (superclazz.isInterface() && EntityProvider.class.isAssignableFrom(superclazz)) {
            capabilities.add((Class<? extends EntityProvider>) superclazz);
         }
      }
      return new ArrayList<Class<? extends EntityProvider>>(capabilities);
   }

   public void registerEntityProvider(EntityProvider entityProvider) {
      String prefix = entityProvider.getEntityPrefix();
      List<Class<? extends EntityProvider>> superclasses = getCapabilities(entityProvider);
      for (Class<? extends EntityProvider> superclazz : superclasses) {
         registerPrefixCapability(prefix, superclazz, entityProvider);
      }
      if (!superclasses.contains(ReferenceParseable.class)) {
         registerPrefixCapability(prefix, ReferenceParseable.class, internalRP);
      }
   }

   public void unregisterEntityProvider(EntityProvider entityProvider) {
      final String prefix = entityProvider.getEntityPrefix();
      List<Class<? extends EntityProvider>> superclasses = getCapabilities(entityProvider);
      for (Class<? extends EntityProvider> superclazz : superclasses) {
         // ensure that the root EntityProvider is never absent from the map unless
         // there is a call to unregisterEntityProviderByPrefix
         if (superclazz == EntityProvider.class) {
            if (getProviderByPrefixAndCapability(prefix, EntityProvider.class) != null) {
               // entity provider inner class
               registerEntityProvider( new EntityProvider() {
                  public String getEntityPrefix() {
                     return prefix;
                  }
               });
            }
         } else {
            unregisterCapability(prefix, superclazz);
         }
      }
   }

   public void unregisterCapability(String prefix, Class<? extends EntityProvider> capability) {
      if (capability == EntityProvider.class) {
         throw new IllegalArgumentException(
               "Cannot separately unregister root EntityProvider capability - " 
               + "use unregisterEntityProviderByPrefix instead");
      }
      String bikey = getBiKey(prefix, capability);
      removePrefixKey(bikey);
   }

   public void unregisterEntityProviderByPrefix(String prefix) {
      if (prefix == null) {
         throw new NullPointerException("prefix cannot be null");
      }
      for (String bikey : prefixMap.keySet()) {
         String keypref = getPrefix(bikey);
         if (keypref.equals(prefix)) {
            removePrefixKey(bikey);
         }
      }
   }

   /**
    * Removes a key from the maps and updates the read maps if needed,
    * this is the only place where we remove from the maps
    * 
    * @param key a bikey
    * @return true if key was found and removed, false if key not found
    */
   private boolean removePrefixKey(String key) {
      if (prefixWriteMap.remove(key) != null) {
         prefixMap = new HashMap<String, EntityProvider>(prefixWriteMap);
         return true;
      }
      return false;
   }

   /**
    * Allows for easy registration of a prefix and capability,
    * this is the only place where we insert to the maps
    * 
    * @param prefix entity prefix
    * @param capability the capability class
    * @param provider the provider to register
    * @return true if the provider is newly registered, false if it was already registered
    */
   public boolean registerPrefixCapability(String prefix,
         Class<? extends EntityProvider> capability, 
         EntityProvider entityProvider) {
      if (ReferenceParseable.class.isAssignableFrom(capability)) {
         boolean result = parseWriteMap.put(prefix, (ReferenceParseable) entityProvider) == null;
         if (result) {
            parseMap = new HashMap<String, ReferenceParseable>(parseWriteMap);
         }
         return result;
      }
      else {
         String key = getBiKey(prefix, capability);
         boolean result = prefixWriteMap.put(key, entityProvider) == null;
         if (result) {
            prefixMap = new HashMap<String, EntityProvider>(prefixWriteMap);
         }
         return result;
      }
   }

}
