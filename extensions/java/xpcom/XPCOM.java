/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Java XPCOM Bindings.
 *
 * The Initial Developer of the Original Code is
 * IBM Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * IBM Corporation. All Rights Reserved.
 *
 * Contributor(s):
 *   Javier Pedemonte (jhpedemonte@gmail.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.mozilla.xpcom;

import java.lang.reflect.*;
import java.io.*;


/**
 *  Provides access to methods for initializing XPCOM, as well as helper methods
 *  for working with XPCOM classes.
 */
public final class XPCOM {

  /**
   * Initializes XPCOM. You must call this method before proceeding
   * to use XPCOM.
   *
   * @param aMozBinDirectory The directory containing the component
   *                         registry and runtime libraries;
   *                         or use <code>null</code> to use the working
   *                         directory.
   *
   * @param aAppFileLocProvider The object to be used by Gecko that specifies
   *                         to Gecko where to find profiles, the component
   *                         registry preferences and so on; or use
   *                         <code>null</code> for the default behaviour.
   *
   * @return the service manager
   *
   * @exception XPCOMException <ul>
   *      <li> NS_ERROR_NOT_INITIALIZED - if static globals were not initialied,
   *            which can happen if XPCOM is reloaded, but did not completly
   *            shutdown. </li>
   *      <li> Other error codes indicate a failure during initialisation. </li>
   * </ul>
   */
  public static native
  nsIServiceManager initXPCOM(File aMozBinDirectory, AppFileLocProvider aAppFileLocProvider);

  /**
   * Shutdown XPCOM. You must call this method after you are finished
   * using xpcom.
   *
   * @param aServMgr    The service manager which was returned by initXPCOM.
   *                    This will release servMgr.
   *
   * @exception XPCOMException  if a failure occurred during termination
   */
  public static native
  void shutdownXPCOM(nsIServiceManager aServMgr);

  /**
   * Public Method to access to the service manager.
   *
   * @return the service manager
   *
   * @exception XPCOMException
   */
  public static native
  nsIServiceManager getServiceManager();

  /**
   * Public Method to access to the component manager.
   *
   * @return the component manager
   *
   * @exception XPCOMException
   */
  public static native
  nsIComponentManager getComponentManager();

  /**
   * Public Method to access to the component registration manager.
   * 
   * @return the component registration manager
   *
   * @exception XPCOMException
   */
  public static native
  nsIComponentRegistrar getComponentRegistrar();

  /**
   * Public Method to create an instance of a nsILocalFile.
   *
   * @param aPath         A string which specifies a full file path to a 
   *                      location.  Relative paths will be treated as an
   *                      error (NS_ERROR_FILE_UNRECOGNIZED_PATH).
   * @param aFollowLinks  This attribute will determine if the nsLocalFile will
   *                      auto resolve symbolic links.  By default, this value
   *                      will be false on all non unix systems.  On unix, this
   *                      attribute is effectively a noop.
   *
   * @return an instance of an nsILocalFile that points to given path
   *
   * @exception XPCOMException <ul>
   *      <li> NS_ERROR_FILE_UNRECOGNIZED_PATH - raised for unrecognized paths
   *           or relative paths (must supply full file path) </li>
   * </ul>
   */
  public static native
  nsILocalFile newLocalFile(String aPath, boolean aFollowLinks);


  /**
   * If you create a class that implements nsISupports, you will need to provide
   * an implementation of the <code>queryInterface</code> method.  This helper
   * function provides a simple implementation.  Therefore, if your class does
   * not need to do anything special with <code>queryInterface</code>, your
   * implementation would look like:
   * <pre>
   *      public nsISupports queryInterface(String aIID) {
   *        return XPCOM.queryInterface(this, aIID);
   *      }
   * </pre>
   *
   * @param aObject object to query
   * @param aIID    requested interface IID
   *
   * @return        <code>aObject</code> if the given object supports that
   *                interface;
   *                <code>null</code> otherwise.
   */
  public static nsISupports queryInterface(nsISupports aObject, String aIID)
  {
    Class[] interfaces = aObject.getClass().getInterfaces();
    for (int i = 0; i < interfaces.length; i++ ) {
      if (aIID.equals(XPCOM.getInterfaceIID(interfaces[i])))
        return aObject;
    }
    return null;
  }

  /**
   * Gets the interface IID for a particular Java interface.  This is similar
   * to NS_GET_IID in the C++ Mozilla files.
   *
   * @param aInterface  interface which has defined an IID
   *
   * @return            IID for given interface
   */
  public static String getInterfaceIID(Class aInterface)
  {
    // Get short class name (i.e. "bar", not "org.blah.foo.bar")
    StringBuffer iidName = new StringBuffer();
    String fullClassName = aInterface.getName();
    int index = fullClassName.lastIndexOf(".");
    String className = index > 0 ? fullClassName.substring(index + 1) :
                                    fullClassName;

    // Create iid field name
    if (className.startsWith("ns")) {
      iidName.append("NS_");
      iidName.append(className.substring(2).toUpperCase());
    } else {
      iidName.append(className.toUpperCase());
    }
    iidName.append("_IID");

    String iid;
    try {
      Field iidField = aInterface.getDeclaredField(iidName.toString());
      iid = (String) iidField.get(null);
    } catch (NoSuchFieldException e) {
      // Class may implement non-Mozilla interfaces, which would not have an
      // IID method.  In that case, just return an empty string.
      iid = "";
    } catch (IllegalAccessException e) {
      // XXX Should be using a logging service, such as java.util.logging
      System.err.println("ERROR: Could not get field " + iidName.toString());
      iid = "";
    }

    return iid;
  }

}

