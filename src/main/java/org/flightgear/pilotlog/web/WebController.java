/*
 * PilotLog
 *
 * Copyright (c) 2017 Richard Senior
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

package org.flightgear.pilotlog.web;

import java.util.LinkedList;
import java.util.List;

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.service.FlightService;
import org.flightgear.pilotlog.web.helpers.FlightRecordTotals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Web controller for PilotLog home page.
 *
 * @author Richard Senior
 */
@Controller
@SessionAttributes({"example"})
public class WebController {

    @Autowired(required = true)
    FlightService service;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // Convert empty parameters to null to work with by example queries
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute("example")
    public Flight getExample() {
        return new Flight();
    }

    @GetMapping("/")
    public String flightRecord(Model model,
        @ModelAttribute("example") Flight example,
        @PageableDefault(size = 10, sort = "startTime", direction = Direction.DESC) Pageable pageable) {

        final Page<Flight> flights = service.findFlightsByExample(example, pageable);
        model.addAttribute("flights", flights);
        model.addAttribute("pages", pager(flights));

        final int pageTotal = flights.getContent()
            .parallelStream()
            .filter(flight -> flight.getStatus().equals(FlightStatus.COMPLETE))
            .mapToInt(flight -> flight.getDuration())
            .sum();
        final int grandTotal = service.findFlightTimeTotal();
        model.addAttribute("total", new FlightRecordTotals(pageTotal, grandTotal));

        return "flightrecord";
    }

    private List<Integer> pager(Page<?> page) {
        final List<Integer> pager = new LinkedList<>();
        for (int p = 0; p < page.getTotalPages(); ++p) {
            pager.add(p + 1);
        }
        return pager;
    }

}
