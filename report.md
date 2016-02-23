Bug: https://bugzilla.mozilla.org/show_bug.cgi?id=1210420
- Note: Someone was assigned the bug a week after I chose it, did not have the time to find and solve a new bug so I decided to keep this one.

### Diagnosis
The reload button in the network (netmonitor ctrl+shift+q) tool under developer tools has a black background colour when it should be light gray like all the other developer toolbar buttons. Firefox (especially Nightly) uses a dark theme by default, and has an option for a light theme. Therefore to avoid buttons blending in with the black background, the buttons must be a shade of grey. 
- The issue for this bug being that the colour of the reload button blends in with the background of the devtools when using the dark theme, making it appear as if it isnt a button.
- The benefits of fixing the bug include uniformity in style across the toolbar and slightly better usability when using netmonitor.
- There arent major risks with this bug however developers who use the default dark theme will not notice its a button making it look unpleasant when inline with text, and could also cause confusion in terms of usability. The reason it isnt a major risk is because developers will largely still know how to use the network monitor tool, as they would just need to refresh their browser. A button to reload isnt totally necessary, however this bug could still have a low chance of affecting how the developer perceives the tool is to be used.


### Proposing the Solution
1. Find the related files that deal with netmonitor and its CSS
	- devtools/client/themes/netmonitor.css "CSS for netmonitor"
	- devtools/client/locales/en-US/netmonitor.dtd "text for netmonitor HTML"
	- devtools/client/netmonitor/netmonitor.xul "HTML of netmonitor section"
	- devtools/client/themes/toolbars.css "Overall CSS for the devtools toolbar"
2. Find the Reload button and its CSS
	- Reload button is given the id #requests-menu-reload-notice-button
	- grep'ed and found its CSS in netmonitor.css
3. Identify the background colour of the reload button
	- The CSS was missing a background-colour styling, so that explains why it was inheriting the overall browsers background.
4. Find the colour code (rgba) of the grey to be used for buttons
	- Realized I can't hardcode it because the grey shades are slightly different against light and dark backgrounds
	- Exploring toolbars.css resulted in finding the global variable --theme-toolbar-background, which is the darker grey used in the toolbar.
5. Change the colour of Reload to the right darker shade of grey.
	- add background-color styling with --theme-toolbar-background as the colour, to fix the bug.

### Testing Document
- Since this is a graphical/CSS related bug, I will present screenshots that show the issue and the final correct behaviour, which can be used to evaluate the success of the fix.

Issue - Reload button is black and blends in with the background on dark theme, but light theme is fine.
![Issue1](/A3-screenshots/Issue-Reload-Button.png)
![Issue2](/A3-screenshots/Issue-light-theme-example.png)

Correct behaviour - Reload button is the darker shade of grey like the rest of the buttons, thus no longer blends in with the background after my bug fix.

![Correct-fix](/A3-screenshots/Reload-Fix.png)
