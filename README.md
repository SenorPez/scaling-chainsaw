# Loot

Loot is a set of tools originally designed to manage inventory for roleplaying games. Loot currently consists of two components: A REST API and a Discord Bot.

## Current Release

**Release 3** is the most current release of Loot and consists of the following components. See below for detailed release notes for each component.

* **API 2.0.0**
* **Bot 1.2.0**

## API
The current version of **API** is **2.0.0**.

The API provides a HAL-compliant HATEOAS REST application for serving data.

The reference implementation is at https://www.loot.senorpez.com/. Complete documentation of acceptable headers, HTTP methods, and endpoints is located at http://\<server\>/docs/reference.html.

### Changelog

* **2.0.0**: `POST` and `PUT` endpoints now require an `Authentication` header. See the documentation for details on `Authentication` header format.
* **2.0.0**: `Players` and `Characters` resources renamed to `Characters` and `Character`. [[#3]](https://github.com/SenorPez/scaling-chainsaw/issues/3)
* **2.0.0**: Added additional reference links; should be completly navigable now. [[#9]](https://github.com/SenorPez/scaling-chainsaw/issues/9)
* **2.0.0**: "Loottable" in CURIEs and project structure renamed to "Loot".
* **1.1.0**: Added support for item charges. [[#17]](https://github.com/SenorPez/scaling-chainsaw/issues/17)
* **1.0.0**: First release.

## Bot

The current version of **Bot** is **1.2.0**.

Bot provides a Node.js Discord Bot for manipulation of inventory items.

### Changelog

* **1.2.0**: Updated existing commands to support authorization for `POST` and `PUT` endpoints.
* **1.1.0**: Added command for updating item charges. [[#17]](https://github.com/SenorPez/scaling-chainsaw/issues/17)
* **1.0.0**: First release.
