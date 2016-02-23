A3 GFB 
Ammar ElAmir
g4elamir
Bug 1064821 - Throw Component.exceptions instead of strings in contentprefs
https://bugzilla.mozilla.org/show_bug.cgi?id=1064821

This bug is a fix of the source code in toolkit//contentprefs.
https://dxr.mozilla.org/mozilla-central/search?q=throw+path%3Atoolkit%2Fcomponents%2Fcontentprefs&case=true
The case is that exceptions are thrown as strings (since JS has no restriction on throws) however they should be appropriate exception objects.

I will read the source file to find these throw cases and replace them with excpetion objects accordingly (as described in the first comment on the bug page).

This can be tested by running code that will reach an exception case or by visually reviewing the exception cases in the code.

--

Instances Found: 
https://dxr.mozilla.org/mozilla-central/search?q=%22throw+%22+path%3Atoolkit%2Fcomponents%2Fcontentprefs&redirect=false&case=true

File | Count | Line(s)
--- | --- | ---
toolkit/components/contentprefs/ContentPrefService2.jsm | 1 | 847
toolkit/components/contentprefs/nsContentPrefService.js | 1 | 72
toolkit/components/contentprefs/tests/unit/head_contentPrefs.js | 2 | 54, 71
toolkit/components/contentprefs/tests/unit/test_contentPrefs.js | 3 | 242, 268, 341

Once I found the lines with errors from the link above, updateing them was easy.Given the specification in the first comment by David Rajchenbach-Teller on the bug webpage, I simply replaced the lines accordingly.
So far this bug proved easier than expected. Final submission pending. 
