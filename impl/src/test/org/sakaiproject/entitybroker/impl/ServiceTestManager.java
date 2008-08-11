/**
 * $Id$
 * $URL$
 * TestManager.java - entity-broker - Jul 23, 2008 6:27:29 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.impl;

import org.sakaiproject.entitybroker.dao.EntityBrokerDao;
import org.sakaiproject.entitybroker.impl.entityprovider.EntityPropertiesService;
import org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl;
import org.sakaiproject.entitybroker.impl.entityprovider.extension.RequestGetterImpl;
import org.sakaiproject.entitybroker.impl.entityprovider.extension.RequestStorageImpl;
import org.sakaiproject.entitybroker.impl.mocks.FakeServerConfigurationService;
import org.sakaiproject.entitybroker.mocks.EntityViewAccessProviderManagerMock;
import org.sakaiproject.entitybroker.mocks.HttpServletAccessProviderManagerMock;
import org.sakaiproject.entitybroker.mocks.data.TestData;


/**
 * This creates all the needed services (as if it were the component manager),
 * this will let us create the objects we need without too much confusion and ensure
 * we are using the same ones
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ServiceTestManager {

   public FakeServerConfigurationService serverConfigurationService;
   public RequestStorageImpl requestStorage;
   public RequestGetterImpl requestGetter;
   public EntityPropertiesService entityPropertiesService;
   public EntityActionsManager entityActionsManager;
   public EntityProviderManagerImpl entityProviderManager;
   public EntityBrokerManager entityBrokerManager;
   public EntityDescriptionManager entityDescriptionManager;
   public EntityEncodingManager entityEncodingManager;
   public EntityRedirectsManager entityRedirectsManager;
   public EntityHandlerImpl entityRequestHandler;
   public HttpServletAccessProviderManagerMock httpServletAccessProviderManager;
   public EntityViewAccessProviderManagerMock entityViewAccessProviderManager;
   public EntityMetaPropertiesService entityMetaPropertiesService;
   public EntityTaggingService entityTaggingService;

   public ServiceTestManager(TestData td) {
      this(td, null);
   }

   public ServiceTestManager(TestData td, EntityBrokerDao dao) {
      // initialize all the parts
      requestGetter = new RequestGetterImpl();
      entityPropertiesService = new EntityPropertiesService();
      entityActionsManager = new EntityActionsManager();
      entityRedirectsManager = new EntityRedirectsManager();
      serverConfigurationService = new FakeServerConfigurationService();
      httpServletAccessProviderManager = new HttpServletAccessProviderManagerMock();
      entityViewAccessProviderManager = new EntityViewAccessProviderManagerMock();

      requestStorage = new RequestStorageImpl();
      requestStorage.setRequestGetter(requestGetter);

      entityProviderManager = new EntityProviderManagerImpl();
      entityProviderManager.setRequestGetter( requestGetter );
      entityProviderManager.setRequestStorage( requestStorage );
      entityProviderManager.setEntityProperties( entityPropertiesService );
      entityProviderManager.setEntityActionsManager( entityActionsManager );
      entityProviderManager.setEntityRedirectsManager(entityRedirectsManager);

      entityProviderManager.init();

      entityProviderManager.registerEntityProvider(td.entityProvider1);
      entityProviderManager.registerEntityProvider(td.entityProvider1T);
      entityProviderManager.registerEntityProvider(td.entityProvider2);
      entityProviderManager.registerEntityProvider(td.entityProvider3);
      entityProviderManager.registerEntityProvider(td.entityProvider4);
      entityProviderManager.registerEntityProvider(td.entityProvider5);
      entityProviderManager.registerEntityProvider(td.entityProvider6);
      entityProviderManager.registerEntityProvider(td.entityProvider7);
      entityProviderManager.registerEntityProvider(td.entityProvider8);
      entityProviderManager.registerEntityProvider(td.entityProviderA);
      entityProviderManager.registerEntityProvider(td.entityProviderA1);
      entityProviderManager.registerEntityProvider(td.entityProviderA2);
      entityProviderManager.registerEntityProvider(td.entityProviderA3);
      entityProviderManager.registerEntityProvider(td.entityProviderU1);
      entityProviderManager.registerEntityProvider(td.entityProviderU2);
      entityProviderManager.registerEntityProvider(td.entityProviderU3);
      entityProviderManager.registerEntityProvider(td.entityProviderTag);
      entityProviderManager.registerEntityProvider(td.entityProviderB1);
      entityProviderManager.registerEntityProvider(td.entityProviderB2);
      // add new providers here

      entityBrokerManager = new EntityBrokerManager();
      entityBrokerManager.setEntityProviderManager( entityProviderManager );
      entityBrokerManager.setServerConfigurationService( serverConfigurationService );

      entityDescriptionManager = new EntityDescriptionManager();
      entityDescriptionManager.setEntityProviderManager( entityProviderManager );
      entityDescriptionManager.setEntityBrokerManager( entityBrokerManager );
      entityDescriptionManager.setEntityProperties( entityPropertiesService );
      entityDescriptionManager.setEntityActionsManager( entityActionsManager );
      entityDescriptionManager.setEntityRedirectsManager(entityRedirectsManager);

      entityEncodingManager = new EntityEncodingManager();
      entityEncodingManager.setEntityProviderManager( entityProviderManager );
      entityEncodingManager.setEntityBrokerManager( entityBrokerManager );

      entityRequestHandler = new EntityHandlerImpl();
      entityRequestHandler.setAccessProviderManager( httpServletAccessProviderManager );
      entityRequestHandler.setEntityBrokerManager( entityBrokerManager );
      entityRequestHandler.setEntityDescriptionManager( entityDescriptionManager );
      entityRequestHandler.setEntityEncodingManager( entityEncodingManager );
      entityRequestHandler.setEntityProviderManager( entityProviderManager );
      entityRequestHandler.setEntityViewAccessProviderManager( entityViewAccessProviderManager );
      entityRequestHandler.setRequestGetter( requestGetter );
      entityRequestHandler.setRequestStorage( requestStorage );
      entityRequestHandler.setEntityActionsManager(entityActionsManager);
      entityRequestHandler.setEntityRedirectsManager(entityRedirectsManager);

      entityMetaPropertiesService = new EntityMetaPropertiesService();
      entityMetaPropertiesService.setDao(dao);
      entityMetaPropertiesService.setEntityBrokerManager(entityBrokerManager);
      entityMetaPropertiesService.setEntityProviderManager(entityProviderManager);

      entityTaggingService = new EntityTaggingService();
      entityTaggingService.setDao(dao);
      entityTaggingService.setEntityBrokerManager(entityBrokerManager);
      entityTaggingService.setEntityProviderManager(entityProviderManager);
   }

}
