OpenWMS.org WMS: Simple Putaway
=====================

Responsibilities of the Putaway service are as follows:
  
  - The Putaway strategy is called to find an available Location for a TransportUnit in a given list of warehouse aisles
  - The strategy counts the number of free Locations in a warehouse aisle

# Resources

[![Build status](https://travis-ci.com/openwms/org.openwms.wms.putaway.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.wms.putaway)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.wms.putaway&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.wms.putaway)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Build & Run

```
$ mvn package
```

The Putaway strategy is a so called attached service that must be attached to the OpenWMS.org COMMON Service in order to run. So grab the
JAR file of the COMMON Service and attach this service while starting the COMMON Service:

```
$ java -cp openwms-common-service-exec.jar -Dloader.main=org.openwms.common.CommonStarter -Dloader.path=target/openwms-putaway-service.jar org.springframework.boot.loader.PropertiesLauncher
```

After the service has started up make a HTTP call to

```
http://localhost:8120/v1/location-groups?locationGroupName=FGSTOCK&transportUnitBK=00000000000000004711
```

to find a free stock Location.

# Current state of development

Under development

# Models

## Anti Corruption Layer (ACL)

Entities of the ACL do not belong and are not owned by the service itself. They can be
seen as narrowed representations of entities in other domains and store only attributes
and relationships that are required for the context of inventory management service.

A **Location** in the context of inventory management is identified by a 5-tupel of area,
aisle, x, y, z. Where area defines the logical area of the Location, like "Pallet" or 
"Hanging Goods", aisle the particular stock aisle (0001, 0002, etc.) and the coordinate in 
the aisle as x-y-z tupel.

A **TransportUnit** represents the physical container that is used to move items between
locations in the warehouse. In the context of inventory management the TransportUnit tracks
at least the information of its actual location.

## Domain Model

Basically this service deals with Products and LoadUnits. A **Product** is the representation
of a real world product, or an article and has at least a [SKU](https://en.wikipedia.org/wiki/Stock_keeping_unit) (identifier) and a
descriptive text. Products may not be placed everywhere in a stock but only in certain
areas. Toxic liquids for example might only be stored down at the bottom of the stock aisle
but never in the upper rows. This mapping is defined with **StockZones** where a Product may
tied to.

A **PackagingUnit** contains an amount of a particular Product item. For example a box of
100 screws or a tot of 1 fl oz liquid cleaner is a PackagingUnit of screws or liquid
cleaner.

A **LoadUnit** is used to divide a **TransportUnit** into physical areas. Each of these areas can
be assigned to a particular Product only and may refer to a number of PackagingUnits that
are placed in the area. LoadUnits are used in the picking process to tell the operator
where to take PackagingUnits or an amount of Product items from.
