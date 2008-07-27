/**
 * $Id$
 * $URL$
 * EntityActionsManagerTest.java - entity-broker - Jul 27, 2008 6:10:15 PM - azeckoski
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

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybroker.entityprovider.extension.CustomAction;
import org.sakaiproject.entitybroker.mocks.ActionsEntityProviderMock;
import org.sakaiproject.entitybroker.mocks.data.MyEntity;
import org.sakaiproject.entitybroker.mocks.data.TestData;

import junit.framework.TestCase;


/**
 * Test the code which handled entity actions
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class EntityActionsManagerTest extends TestCase {

   protected EntityActionsManager entityActionsManager;
   private TestData td;

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      // setup things
      td = new TestData();

      entityActionsManager = new TestManager(td).entityActionsManager;
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.EntityActionsManager#handleCustomActionRequest(org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable, org.sakaiproject.entitybroker.EntityView, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
    */
   public void testHandleCustomActionRequest() {
      //TODO fail("Not yet implemented");
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.EntityActionsManager#handleCustomActionExecution(org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable, org.sakaiproject.entitybroker.EntityReference, java.lang.String, java.util.Map, java.io.OutputStream)}.
    */
   public void testHandleCustomActionExecution() {
      // test the double/xxx/clear actions
      ActionsEntityProviderMock actionProvider = td.entityProviderA1;
      EntityReference ref = new EntityReference(TestData.PREFIXA1, TestData.IDSA1[0]);

      // double
      MyEntity me = (MyEntity) actionProvider.getEntity( new EntityReference(TestData.REFA1) );
      int num = me.getNumber();
      ActionReturn actionReturn = entityActionsManager.handleCustomActionExecution(actionProvider, ref, "double", null, null);
      assertNotNull(actionReturn);
      assertNotNull(actionReturn.entityData);
      MyEntity doubleMe = (MyEntity) actionReturn.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);
      assertEquals(me.getId(), doubleMe.getId());

      // xxx
      MyEntity me1 = (MyEntity) actionProvider.getEntity( new EntityReference(TestData.REFA1) );
      assertFalse("xxx".equals(me1.extra));
      assertFalse("xxx".equals(me1.getStuff()));
      actionReturn = entityActionsManager.handleCustomActionExecution(actionProvider, ref, "xxx", null, null);
      assertNull(actionReturn);
      MyEntity xxxMe = (MyEntity) actionProvider.getEntity( new EntityReference(TestData.REFA1) );
      assertEquals(me1.getId(), xxxMe.getId());
      assertTrue("xxx".equals(xxxMe.extra));
      assertTrue("xxx".equals(xxxMe.getStuff()));

      // clear
      assertEquals(2, actionProvider.myEntities.size());
      actionReturn = entityActionsManager.handleCustomActionExecution(actionProvider, new EntityReference(TestData.PREFIXA1, ""), "clear", null, null);
      assertEquals(0, actionProvider.myEntities.size());

      // check exception when try to execute invalid action
      try {
         entityActionsManager.handleCustomActionExecution(actionProvider, ref, "NOT_VALID_ACTION", null, null);
         fail("should have thrown exeception");
      } catch (UnsupportedOperationException e) {
         assertNotNull(e.getMessage());
      }
      
      try {
         entityActionsManager.handleCustomActionExecution(null, ref, "xxx", null, null);
         fail("should have thrown exeception");
      } catch (IllegalArgumentException e) {
         assertNotNull(e.getMessage());
      }

      try {
         entityActionsManager.handleCustomActionExecution(actionProvider, null, "xxx", null, null);
         fail("should have thrown exeception");
      } catch (IllegalArgumentException e) {
         assertNotNull(e.getMessage());
      }

      try {
         entityActionsManager.handleCustomActionExecution(actionProvider, ref, "", null, null);
         fail("should have thrown exeception");
      } catch (IllegalArgumentException e) {
         assertNotNull(e.getMessage());
      }
   }

   /**
    * Ensure that the mock for testing the custom actions works correctly
    */
   public void testActionsEntityProviderMock() {
      ActionsEntityProviderMock aep = td.entityProviderA1;

      // check double operation works
      MyEntity me = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      int num = me.getNumber();
      ActionReturn ar = (ActionReturn) aep.doubleAction(new EntityView(new EntityReference(TestData.REFA1), null, null));
      MyEntity doubleMe = (MyEntity) ar.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);
      assertEquals(me.getId(), doubleMe.getId());

      // make sure it works twice
      ar = (ActionReturn) aep.doubleAction(new EntityView(new EntityReference(TestData.REFA1), null, null));
      doubleMe = (MyEntity) ar.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);

      // test xxx operation
      MyEntity me1 = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      assertFalse("xxx".equals(me1.extra));
      assertFalse("xxx".equals(me1.getStuff()));
      aep.xxxAction( new EntityReference(TestData.REFA1) );
      MyEntity xxxMe = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      assertEquals(me1.getId(), xxxMe.getId());
      assertTrue("xxx".equals(xxxMe.extra));
      assertTrue("xxx".equals(xxxMe.getStuff()));

      // test clear
      assertEquals(2, aep.myEntities.size());
      aep.executeActions(new EntityView(new EntityReference(TestData.PREFIXA1, ""), EntityView.VIEW_NEW, null), "clear", null, null);
      assertEquals(0, aep.myEntities.size());      
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#setCustomActions(java.lang.String, java.util.Map)}.
    */
   public void testSetCustomActions() {
      Map<String, CustomAction> actions = new HashMap<String, CustomAction>();
      actions.put("test", new CustomAction("test", EntityView.VIEW_SHOW));
      entityActionsManager.setCustomActions(TestData.PREFIXA1, actions);
      assertNotNull(entityActionsManager.getCustomAction(TestData.PREFIXA1, "test"));

      // NOTE: can set custom actions for entities without the ability to process them
      entityActionsManager.setCustomActions(TestData.PREFIX2, actions);

      // test using reserved word fails
      actions.clear();
      actions.put("describe", new CustomAction("describe", EntityView.VIEW_SHOW));
      try {
         entityActionsManager.setCustomActions(TestData.PREFIXA1, actions);
         fail("should have thrown exeception");
      } catch (IllegalArgumentException e) {
         assertNotNull(e.getMessage());
      }
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#getCustomAction(java.lang.String, java.lang.String)}.
    */
   public void testGetCustomAction() {
      assertNotNull( entityActionsManager.getCustomAction(TestData.PREFIXA1, "xxx") );
      assertNotNull( entityActionsManager.getCustomAction(TestData.PREFIXA1, "double") );

      assertNull( entityActionsManager.getCustomAction(TestData.PREFIXA1, "apple") );
      assertNull( entityActionsManager.getCustomAction(TestData.PREFIX2, "action") );
      assertNull( entityActionsManager.getCustomAction(TestData.PREFIX5, "action") );
   }

   /**
    * Test method for {@link org.sakaiproject.entitybroker.impl.entityprovider.EntityProviderManagerImpl#removeCustomActions(java.lang.String)}.
    */
   public void testRemoveCustomActions() {
      assertNotNull( entityActionsManager.getCustomAction(TestData.PREFIXA1, "xxx") );
      entityActionsManager.removeCustomActions(TestData.PREFIXA1);
      assertNull( entityActionsManager.getCustomAction(TestData.PREFIXA1, "xxx") );      
   }

   public void testCustomActions() {
      ActionsEntityProviderMock aep = td.entityProviderA1;

      // check double operation works
      MyEntity me = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      int num = me.getNumber();
      ActionReturn ar = (ActionReturn) aep.doubleAction(new EntityView(new EntityReference(TestData.REFA1), null, null));
      MyEntity doubleMe = (MyEntity) ar.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);
      assertEquals(me.getId(), doubleMe.getId());

      // make sure it works twice
      ar = (ActionReturn) aep.doubleAction(new EntityView(new EntityReference(TestData.REFA1), null, null));
      doubleMe = (MyEntity) ar.entityData;
      assertEquals(doubleMe.getNumber(), num * 2);

      // test xxx operation
      MyEntity me1 = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      assertFalse("xxx".equals(me1.extra));
      assertFalse("xxx".equals(me1.getStuff()));
      aep.xxxAction( new EntityReference(TestData.REFA1) );
      MyEntity xxxMe = (MyEntity) aep.getEntity( new EntityReference(TestData.REFA1) );
      assertEquals(me1.getId(), xxxMe.getId());
      assertTrue("xxx".equals(xxxMe.extra));
      assertTrue("xxx".equals(xxxMe.getStuff()));

      // test clear
      assertEquals(2, aep.myEntities.size());
      aep.executeActions(new EntityView(new EntityReference(TestData.PREFIXA1, ""), EntityView.VIEW_NEW, null), "clear", null, null);
      assertEquals(0, aep.myEntities.size());      
   }

}
