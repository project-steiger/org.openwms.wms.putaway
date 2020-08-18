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
package org.openwms.common.putaway;

import org.openwms.common.location.Location;
import org.openwms.common.location.api.LocationGroupState;
import org.openwms.common.transport.barcode.Barcode;

import java.util.List;

/**
 * A PutawayService.
 *
 * @author Heiko Scherrer
 */
public interface PutawayService {

    /**
     * Find and return all {@code Location}s that belong to the {@code LocationGroup}s
     * identified by the given {@code stockLocationGroupNames} and that match the applied
     * filter criteria.
     *
     * @param stockLocationGroupNames The names of the LocationGroups to search Locations
     * for
     * @param barcode The Barcode of the TransportUnit to search a Location for
     * @param groupStateIn If {@literal null} this criterion is not applied, otherwise
     * only Locations are considered that match the demanded groupStateIn
     * @param groupStateOut If {@literal null} this criterion is not applied, otherwise
     * only Locations are considered that match the demanded groupStateOut
     * @param count A number of Locations to return. Useful to limit the result set
     * @return All Locations, never {@literal null}
     */
    List<Location> findAvailableStockLocations(List<String> stockLocationGroupNames, Barcode barcode, LocationGroupState groupStateIn, LocationGroupState groupStateOut, int count);

    int availableLocationsIn(List<String> locationGroupNames);
}
