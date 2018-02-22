package org.flightgear.pilotlog.service;

import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.domain.AircraftRepository;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AircraftService {

    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final PageableUtil pageableUtil;

    @Autowired
    public AircraftService(
            AircraftRepository aircraftRepository,
            FlightRepository flightRepository,
            PageableUtil pageableUtil
    ) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.pageableUtil = pageableUtil;
    }

    @Transactional(readOnly = true)
    public List<Aircraft> findAllAircraft() {
        return aircraftRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Aircraft> findAllAircraft(Pageable pageable) {
        pageable = pageableUtil.adjustPageable(pageable, "model", "model");
        return aircraftRepository.findAll(pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateSummary(String model) {
        Aircraft summary = flightRepository.aircraftSummaryByModel(model);
        aircraftRepository.save(summary);
    }

}
