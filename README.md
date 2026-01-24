# External Inspections

![Build](https://github.com/j-plugins/external-inspections-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/29890-external-inspections.svg)](https://plugins.jetbrains.com/plugin/29890-external-inspections)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/29890-external-inspections.svg)](https://plugins.jetbrains.com/plugin/29890-external-inspections)

<!-- Plugin description -->

[GitHub](https://github.com/j-plugins/external-inspections-plugin) | [Telegram](https://t.me/jb_plugins) | [Donation](https://github.com/xepozz/xepozz?tab=readme-ov-file#become-a-sponsor)

## External Inspections

Let external tools inspect your code and provide feedback.

### Features

- XML Support: Define diagnostics using standard XML format.
- JSON Support: Use JSON for a more compact representation of diagnostics.
- Custom File Filters: Configure which files the plugin should track using wildcard patterns (e.g., `*.inspections.xml`, `custom-report.json`).

### General Usage:

- Create an XML or JSON file
- Add tags or properties describing what you want to highlight
- Ensure the file name matches available file patterns in the settings

### TODO:

- Introduce XSD and JSON Schema
- Add inspections to the files

Check out the [playground](https://github.com/j-plugins/external-inspections-plugin/tree/main/playground) for more details.

## Donation

Open-source tools can greatly improve workflows, helping developers and businesses save time and increase revenue.
Many successful projects have been built on these tools, benefiting a wide community.
However, maintaining and enhancing these resources requires continuous effort and investment.

Support from the community helps keep these projects alive and ensures they remain useful for everyone.
Donations play a key role in sustaining and improving these open-source initiatives.

Chose the best option for you to say thank you:

[<img height="28" src="https://github.githubassets.com/assets/patreon-96b15b9db4b9.svg"> Patreon](https://patreon.com/xepozz)
|
[<img height="28" src="https://github.githubassets.com/assets/buy_me_a_coffee-63ed78263f6e.svg"> Buy me a coffee](https://buymeacoffee.com/xepozz)
|
[<img height="28" src="https://boosty.to/favicon.ico"> Boosty](https://boosty.to/xepozz)

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "external-inspections-plugin"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29890-external-inspections) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/29890-external-inspections/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/j-plugins/external-inspections-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
