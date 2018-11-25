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

package org.flightgear.pilotlog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PageableUtil {

    private static final Logger log = LoggerFactory.getLogger(PageableUtil.class);

    /**
     * Adapts a pageable to ensure stable sorting and allows properties to
     * sort in a case-insensitive order.
     * <p>
     * There are a couple of things going on in this method to manipulate the
     * pageable passed in from the web tier:
     * <p>
     * 1. If the sort property is one that contains duplicate values, the sort
     * can become unstable. Adding a sort by primary key (id) prevents this.
     * <p>
     * 2. If the sort property has values with mixed case, but the web tier should
     * present them in case-insensitive order, the sort order is replaced with
     * a version that ignores case.
     *
     * @param pageable the original pageable
     * @param idProperty the unique id property for the entity
     * @param caseInsensitiveProperties the variadic list of case insensitive properties
     * @return a stable pageable with case insensitive properties as required
     */
    public Pageable adjustPageable(
            Pageable pageable,
            String idProperty,
            String... caseInsensitiveProperties
    ) {
        boolean stable = false;
        List<Sort.Order> orders = new ArrayList<>();
        log.debug("Sort order:");
        Set<String> needsIgnoreCase = new HashSet<>(Arrays.asList(caseInsensitiveProperties));
        for (Sort.Order order : pageable.getSort()) {
            if (needsIgnoreCase.contains(order.getProperty())) {
                orders.add(order.ignoreCase());
                log.debug(" - {} {} (ignoring case)", order.getProperty(), order.getDirection());
            } else {
                orders.add(order);
                log.debug(" - {} {}", order.getProperty(), order.getDirection());
            }
            stable = stable || order.getProperty().equals(idProperty);
        }
        if (!stable) {
            orders.add(Sort.Order.by(idProperty));
            log.debug(" - {}", idProperty);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
        return pageable;
    }

}
