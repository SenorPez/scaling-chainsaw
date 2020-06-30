# Loot

Loot is a set of tools originally designed to manage inventory for roleplaying games. Loot currently consists of three components:
* **API**
* **Bot**
* **Frontend**

## Current Release

**Release 4** is the most current release of Loot and consists of the following components. See below for detailed release notes for each component.

* **API 2.1.0**
* **Bot 2.0.0**
* **Frontend 1.0.0**

## API
The current version of **API** is **2.1.0**.

The API provides a HAL-compliant HATEOAS REST application for serving data.

The reference implementation is at https://www.loot.senorpez.com/. Complete documentation of acceptable headers, HTTP methods, and endpoints is located at http://\<server\>/docs/reference.html.

### Changelog

* **2.1.0**: Removed empty inventory resource from `Characters` resources.
* **2.0.1**: Adds CORS support to endpoints.
* **2.0.1**: Corrects links in resources to correctly use `https` protocol.
* **2.0.0**: `POST` and `PUT` endpoints now require an `Authentication` header. See the documentation for details on `Authentication` header format.
* **2.0.0**: `Players` and `Player` resources renamed to `Characters` and `Character`. [[#3]](https://github.com/SenorPez/scaling-chainsaw/issues/3)
* **2.0.0**: Added additional reference links; should be completly navigable now. [[#9]](https://github.com/SenorPez/scaling-chainsaw/issues/9)
* **2.0.0**: "Loottable" in CURIEs and project structure renamed to "Loot".
* **1.1.0**: Added support for item charges. [[#17]](https://github.com/SenorPez/scaling-chainsaw/issues/17)
* **1.0.0**: First release.

## Bot

The current version of **Bot** is **2.0.0**.

Bot provides a Node.js Discord Bot for manipulation of inventory items.

### Changelog

* **2.0.0**: Rework of Bot. Commands now prefixed with `$` and arguments can be an ID or a text search. Run `$help` to list available commands.
* **1.2.0**: Updated existing commands to support authorization for `POST` and `PUT` endpoints.
* **1.1.0**: Added command for updating item charges. [[#17]](https://github.com/SenorPez/scaling-chainsaw/issues/17)
* **1.0.0**: First release.

## Frontend

The current version of **Frontend** is **1.0.0**.

Frontend provides an HTML interface for viewing characters and loot.

### Changelog

* **1.0.0**: First release.
