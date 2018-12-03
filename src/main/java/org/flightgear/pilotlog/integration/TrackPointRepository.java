package org.flightgear.pilotlog.integration;

import org.flightgear.pilotlog.domain.TrackPoint;
import org.flightgear.pilotlog.dto.TrackPointDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackPointRepository extends JpaRepository<TrackPoint, Long> {

    List<TrackPointDTO> findByFlightIdOrderByOdometer(int id);

}
