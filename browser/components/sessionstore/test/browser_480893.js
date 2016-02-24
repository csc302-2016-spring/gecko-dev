/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

function test() {
  /** Test for Bug 480893 **/

  waitForExplicitFinish();

  // Test that starting a new session loads a blank page if Firefox is
  // configured to display a blank page at startup (browser.startup.page = 0)
  Services.prefs.setIntPref("browser.startup.page", 0);
  let tab = gBrowser.addTab("about:sessionrestore");
  gBrowser.selectedTab = tab;
  let browser = tab.linkedBrowser;
  promiseBrowserLoaded(browser).then(() => {
    let doc = browser.contentDocument;

    // click on the "Start New Session" button after about:sessionrestore is loaded
    doc.getElementById("errorCancel").click();
    promiseBrowserLoaded(browser).then(() => {
      let doc = browser.contentDocument;

      is(doc.URL, "about:blank", "loaded page is about:blank");

      // Test that starting a new session loads the homepage (set to http://mochi.test:8888)
      // if Firefox is configured to display a homepage at startup (browser.startup.page = 1)
      let homepage = "http://mochi.test:8888/";
      Services.prefs.setCharPref("browser.startup.homepage", homepage);
      Services.prefs.setIntPref("browser.startup.page", 1);
      gBrowser.loadURI("about:sessionrestore");
      promiseBrowserLoaded(browser).then(() => {
        let doc = browser.contentDocument;

        // click on the "Start New Session" button after about:sessionrestore is loaded
        doc.getElementById("errorCancel").click();
        promiseBrowserLoaded(browser).then(() => {
          let doc = browser.contentDocument;

          is(doc.URL, homepage, "loaded page is the homepage");

          // close tab, restore default values and finish the test
          gBrowser.removeTab(tab);
          Services.prefs.clearUserPref("browser.startup.page");
          Services.prefs.clearUserPref("browser.startup.homepage");
          finish();
        });
      });
    });
  });
}
