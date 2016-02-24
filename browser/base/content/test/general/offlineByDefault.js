var offlineByDefault = {
  defaultValue: false,
  prefBranch: SpecialPowers.Services.prefs,
  set: function(allow) {
    try {
      this.defaultValue = this.prefBranch.getBoolPref("offline-apps.allow_by_default");
    } catch (e) {
      this.defaultValue = false
    }
    this.prefBranch.setBoolPref("offline-apps.allow_by_default", allow);
  },
  reset: function() {
    this.prefBranch.setBoolPref("offline-apps.allow_by_default", this.defaultValue);
  }
}

offlineByDefault.set(false);
