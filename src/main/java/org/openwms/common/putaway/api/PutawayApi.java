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
package org.openwms.common.putaway.api;

import org.openwms.common.location.api.LocationVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A PutawayApi.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifier = "putawayApi", decode404 = true)
public interface PutawayApi {

    /**
     * Count and return the number of free stock locations in the LocationGroups identified by the given {@code locationGroupNames}.
     * @param locationGroupNames A comma separated list of LocationGroup names
     * @return The sum of all available Locations in all LocationGroups
     */
    @GetMapping(value = PutawayConstants.API_LOCATION_GROUPS, params = {"locationGroupNames"})
    int countAvailableLocationsIn(
            @RequestParam("locationGroupNames") String locationGroupNames
    );

    /**
     * Find the next stock location for infeed in the {@code LocationGroup} identified by the {@code locationGroupName}.
     *
     * @param locationGroupName Name of the LocationGroup to search a Location for
     * @param transportUnitBK The unique (physical) identifier of the TransportUnit to search a Location for
     * @return Next free Location for infeed
     * @throws org.ameba.exception.NotFoundException May throw in case no Location available
     */
    @GetMapping(value = PutawayConstants.API_LOCATION_GROUPS, params = {"locationGroupName", "transportUnitBK"})
    LocationVO findInAisle(
            @RequestParam("locationGroupName") String locationGroupName,
            @RequestParam("transportUnitBK") String transportUnitBK
    );
}
