/*
 * PilotLog
 *
 * Copyright © 2018 Richard Senior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flightgear.pilotlog.integration;

import org.flightgear.pilotlog.domain.TrackPoint;
import org.flightgear.pilotlog.dto.TrackPointDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for direct access to track points.
 *
 * @author Richard Senior
 */
public interface TrackPointRepository extends JpaRepository<TrackPoint, Long> {

    List<TrackPointDTO> findByFlightIdOrderByOdometer(int id);

    TrackPointDTO findFirstByFlightIdOrderByOdometerDesc(int id);

}
