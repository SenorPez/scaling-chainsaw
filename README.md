# Loot

Loot is a set of tools originally designed to manage inventory for roleplaying games. Loot currently consists of two components: A REST API and a Discord Bot.

## Current Release

**Release 2** is the most current release of Loot and consists of the following components. See below for detailed release notes for each component.

* **API 1.1.0**
* **Bot 1.1.0**

## API
The current version of **API** is **1.1.0**.

The API provides a HAL-compliant HATEOAS REST application for serving data.

The reference implementation is located at http://www.senorpez.com:9090/. Complete documentation of acceptable headers, HTTP methods, and endpoints is located at http://<server>/docs/reference.html.

### Changelog

* **1.1.0**: Added support for item charges. [[#17]](https://github.com/SenorPez/scaling-chainsaw/issues/17)
* **1.0.0**: First release.

## Bot

The current version of **Bot** is **1.1.0**.

Bot provides a Node.js Discord Bot for manipulation of inventory items.

### Changelog

* **1.1.0**: Added command for updating item charges. [[#17]](https://github.com/SenorPez/scaling-chainsaw/issues/17)
* **1.0.0**: First release.
