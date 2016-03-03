## CSC302 A3 (GFB Patch) - g2zach

For the GFB, I selected bug [1251600](https://bugzilla.mozilla.org/show_bug.cgi?id=1251600): "test_page_info_window.py doesn't run test for opening/closing the window via menu"

### Diagnosis

Marionette is a testing framework that allows someone to programatically control the Firefox browser - whether that is to open a tab, press a button, or close the application entirely.

The bug I selected had to deal with some Marionette-related tests silently skipping during comparisons with system values.

In the framework for Marionette, there is a helper method that returns what platform Firefox is running on. It returns the platform name - like 'Darwin' or 'Windows NT' - as an all-lowercase string.

The relevant docstring:

```Python
    @property
    def platform(self):
        """Returns the lowercased platform name.

        :returns: Platform name
        """
```

However, in some tests there was a comparison being made between the platform string returned by the framework and a capitalized version: `self.platform == 'Darwin'`. Everytime, when evaluated, is equivalent to `'darwin' == 'Darwin'`. Clearly, this condition will never evaluate to `True` and the `True` branch is never executed.

There are platform dependent tests so if we had a platform value of `darwin` we would expect that the `darwin` related tests would execute. However, due to the comparisons, they will silently skip when the platform is Darwin.

The second component was a depreciated method was being used in one place to get the platform name. It was requested that this be updated to use the `self.platform` attribute of the framework that was detailed above.

### Proposed solution

The solution requires fixing the comparison, as well as updating one of the methods to get the platform name from the framework. The method to attribute change is straight forward and is basically in the interests of code quality and removing depreciated methods.

However, when thinking about resolving the comparison, two are two options to fix this and have the `True` branch execute when it should. 

The first option is to change the helper attribute to return a captalized platform name - like `Darwin`. However, this can be dangerous - you would have to change the Docstring of the attribute and implicitly any place in the codebase that depended on an all-lowercase platform name now will suffer from the same silent skip.

Instead, I would recommend a second option: change the string we are comparing `self.platform` with to match the all-lowercase format. `Darwin` and `Windows_NT` now will become `darwin` and `windows_nt`, respectively.

### Testing

To test this, I targeted the test file I was editing by using the command `./mach marionette-test testing/firefox-ui/tests/firefox_ui_tests/puppeteer/test_page_info_window.py`. This way I did not need to wait to test code I was not editing and could get quick feedback that my commit would be syntactically correct as well as satisfy the tests.

Before pushing my patch to the mozilla review board, I ran the full suite of marionette tests using `./mach marionette-test`.

### Screenshots

![Patch diff](http://i.imgur.com/pIfXB56.png?1 "Patch diff on bugzilla")
