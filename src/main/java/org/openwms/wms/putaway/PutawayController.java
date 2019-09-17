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
package org.openwms.wms.putaway;

import org.ameba.annotation.Measured;
import org.ameba.exception.NotFoundException;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.location.Location;
import org.openwms.common.location.LocationGroupService;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.location.api.LocationVO;
import org.openwms.common.transport.Barcode;
import org.openwms.core.http.AbstractWebController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.openwms.wms.putaway.api.PutawayConstants.API_LOCATION_GROUPS;

/**
 * A PutawayController.
 *
 * @author Heiko Scherrer
 */
@RestController
class PutawayController extends AbstractWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutawayController.class);
    private final LocationGroupService locationGroupService;
    private final PutawayService putawayService;
    private final BeanMapper mapper;

    PutawayController(LocationGroupService locationGroupService, PutawayService putawayService, BeanMapper mapper) {
        this.locationGroupService = locationGroupService;
        this.putawayService = putawayService;
        this.mapper = mapper;
    }

    @Measured
    @GetMapping(value = API_LOCATION_GROUPS, params = {"locationGroupNames"})
    int findAvailableLocationsOf(
            @RequestParam("locationGroupNames") String locationGroupNames
    ) {
        int count = putawayService.availableLocationsIn(Stream.of((locationGroupNames).split(",")).map(String::trim).collect(Collectors.toList()));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Found [{}] locations in LocationGroups [{}]", count, locationGroupNames);
        }
        return count;
    }

    @Measured
    @GetMapping(value = API_LOCATION_GROUPS + "/{locationGroupName}/location", params = {"barcode"})
    LocationVO findInAisle(
            @PathVariable("locationGroupName") String locationGroupName,
            @RequestParam("barcode") String barcode
    ) {
        List<Location> locations = putawayService.findAvailableStockLocations(
                Collections.singletonList(locationGroupName),
                Barcode.of(barcode),
                LocationGroupState.AVAILABLE,
                null,
                1
        );
        if (locations.isEmpty()) {
            throw new NotFoundException(format("No stock locations available for infeed in LocationGroup [%s]", locationGroupName));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Available location found in aisle [{}] for infeed", locations.get(0));
        }
        return mapper.map(locations.get(0), LocationVO.class);
    }
}
