package com.coremedia.tomcat;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * Factory which returns a global ORB.
 */
public class SharedORBFactory implements ObjectFactory {

  @Override
  public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws NamingException {   //NOSONAR
      return SharedORBSingleton.getOrb();
  }

}

