/*
 * Copyright 2005-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.wms.putaway.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationType;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.transport.Barcode;
import org.openwms.common.transport.TransportUnit;
import org.openwms.common.transport.TransportUnitService;
import org.openwms.common.transport.TypePlacingRule;
import org.openwms.wms.putaway.PutawayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A PutawayServiceImpl.
 *
 * @author Heiko Scherrer
 */
@TxService
class PutawayServiceImpl implements PutawayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutawayServiceImpl.class);
    private final StockLocationRepository stockLocationRepository;
    private final TransportUnitService transportUnitService;

    PutawayServiceImpl(StockLocationRepository stockLocationRepository, TransportUnitService transportUnitService) {
        this.stockLocationRepository = stockLocationRepository;
        this.transportUnitService = transportUnitService;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public List<Location> findAvailableStockLocations(List<String> stockLocationGroupNames, Barcode barcode,
            LocationGroupState groupStateIn, LocationGroupState groupStateOut, int count) {

        LOGGER.debug("Searching [{}] Locations in LocationGroups [{}] in groupStateIn [{}] and groupStateOut [{}]", count,
                stockLocationGroupNames, groupStateIn, groupStateOut);
        TransportUnit transportUnit = transportUnitService.findByBarcode(barcode);
        List<LocationType> locationTypes = transportUnit.getTransportUnitType()
                .getTypePlacingRules()
                .stream()
                .map(TypePlacingRule::getAllowedLocationType)
                .collect(Collectors.toList());

        List<Location> locations = stockLocationRepository.findBy(
                count > 0 ? PageRequest.of(0, count) : Pageable.unpaged(),
                stockLocationGroupNames,
                groupStateIn,
                groupStateOut,
                locationTypes);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found [{}] locations in [{}] for location types {}.{}", locations.size(), stockLocationGroupNames,
                    locationTypes.toString(), locations.isEmpty() ? "" : format(" First one is [%s]", locations.get(0)));
        }
        return locations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public int availableLocationsIn(List<String> locationGroupNames) {
        return stockLocationRepository.findAvailableLocationsFor(locationGroupNames);
    }
}