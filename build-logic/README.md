# Build Logic 
The `build-logic` directory is made to simply the build files of Nabi in general. All modules (except the [dashboard](../dashboard))
apply some form of these scripts.

## Plugins
- [`live.shuuyu.plugins.nabi`](convention/src/main/kotlin/live/shuuyu/plugins/convention/NabiProjectModule.kt):
  Configures the Kotlin options

- [`live.shuuyu.plugins.i18n`](i18n/src/main/kotlin/live/shuuyu/plugins/i18n/I18nExtensionModule.kt):
  Configures the I18n module. The I18n module will collect all keys and convert them into properties and functions.