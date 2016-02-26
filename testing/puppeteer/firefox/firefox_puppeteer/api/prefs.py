# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this file,
# You can obtain one at http://mozilla.org/MPL/2.0/.

from marionette_driver.errors import MarionetteException

from firefox_puppeteer.base import BaseLib


class Preferences(BaseLib):
    archive = {}

    def get_pref(self, pref_name, default_branch=False, interface=None):
        """Retrieves the value of a preference.

        To retrieve the value of a preference its name has to be specified. By
        default it will be read from the `user` branch, and returns the current
        value. If the default value is wanted instead, ensure to flag that by
        passing `True` via default_branch.

        It is also possible to retrieve the value of complex preferences, which
        do not represent an atomic type like `basestring`, `int`, or `boolean`.
        Specify the interface of the represented XPCOM object via `interface`.

        :param pref_name: The preference name
        :param default_branch: Optional, flag to use the default branch,
         default to `False`
        :param interface: Optional, interface of the complex preference,
         default to `None`. Possible values are: `nsILocalFile`,
         `nsISupportsString`, and `nsIPrefLocalizedString`

        :returns: The preference value.
        """
        assert pref_name is not None

        with self.using_context('content'):
            return self.execute_script("""
              Components.utils.import("resource://gre/modules/Preferences.jsm");
              return Preferences.get(arguments[0], arguments[1], arguments[2]);
            """, script_args=[pref_name, default_branch, interface])

    def reset_pref(self, pref_name):
        """Resets a user set preference.

        Every modification of a preference will turn its status into a user-set
        preference. To restore the default value of the preference, call this
        function once. Further calls have no effect as long as the value hasn't
        changed again.

        In case the preference is not present on the default branch, it will be
        completely removed.

        :param pref_name: The preference to reset

        :returns: `True` if a user preference has been removed
        """
        assert pref_name is not None

        with self.marionette.using_context('chrome'):
            return self.marionette.execute_script("""
              Components.utils.import("resource://gre/modules/Services.jsm");
              let prefBranch = Services.prefs;

              let pref_name = arguments[0];

              if (prefBranch.prefHasUserValue(pref_name)) {
                prefBranch.clearUserPref(pref_name);
                return true;
              }
              else {
                return false;
              }
            """, script_args=[pref_name])

    def restore_all_prefs(self):
        """Restores all previously modified preferences to their former values.

        Please see :func:`~Preferences.restore_pref` for details.
        """
        while len(self.archive):
            self.restore_pref(self.archive.keys()[0])

    def restore_pref(self, pref_name):
        """Restores a previously set preference to its former value.

        The first time a preference gets modified a backup of its value is
        made. By calling this method, exactly this value will be restored,
        whether how often the preference has been modified again afterward.

        If the preference did not exist before and has been newly created, it
        will be reset to its original value. Please see
        :func:`~Preferences.reset_pref` for details.

        :param pref_name: The preference to restore
        """
        assert pref_name is not None

        try:
            # in case it is a newly set preference, reset it. Otherwise restore
            # its original value.
            if self.archive[pref_name] is None:
                self.reset_pref(pref_name)
            else:
                self.set_pref(pref_name, self.archive[pref_name])

            del self.archive[pref_name]
        except KeyError:
            raise MarionetteException('Nothing to restore for preference "%s"',
                                      pref_name)

    def set_pref(self, pref_name, value):
        """Sets a preference to a specified value.

        To set the value of a preference its name has to be specified.

        The first time a new value for a preference is set, its value will be
        automatically archived. It allows to restore the original value by
        calling :func:`~Preferences.restore_pref`.

        :param pref_name: The preference to set
        :param value: The value to set the preference to
        """
        assert pref_name is not None
        assert value is not None

        with self.using_context('content'):
            # Backup original value only once
            if pref_name not in self.archive:
                self.archive[pref_name] = self.get_pref(pref_name)

            retval = self.marionette.execute_script("""
              Components.utils.import("resource://gre/modules/Preferences.jsm");
              Preferences.set(arguments[0], arguments[1]);
            """, script_args=[pref_name, value])

        assert retval
