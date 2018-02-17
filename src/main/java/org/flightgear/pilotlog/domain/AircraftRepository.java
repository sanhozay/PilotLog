package org.flightgear.pilotlog.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of aircraft summaries.
 *
 * @author Richard Senior
 */
public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {}
