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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"javadoc", "unchecked"})
public class PageableUtilTest {

    private PageableUtil pageableUtil;

    @Before
    public void setUp() {
        pageableUtil = new PageableUtil();
    }

    @Test
    public void testPageableUtilStable() {
        // Given a pageable sorted stable by id
        Pageable original = PageRequest.of(0, 10, Sort.by("id"));
        // when the pageable is adjusted for stabilization using id
        Pageable pageable = pageableUtil.adjustPageable(original, "id");
        // then the pageable should be unchanged
        assertThat(original).isEqualTo(pageable);
    }

    @Test
    public void testPageableUtilNonStable() {
        // Given a pageable sorted unstable by name
        Pageable original = PageRequest.of(0, 10, Sort.by("name"));
        // when the pageable is adjusted for stabilization using id
        Pageable pageable = pageableUtil.adjustPageable(original, "id");
        // then the page number and page size should be unchanged
        assertThat(pageable.getPageNumber()).isEqualTo(original.getPageNumber());
        assertThat(pageable.getPageSize()).isEqualTo(original.getPageSize());
        // and the sort should be stabilized by adding sort by id to it
        assertThat(pageable.getSort()).isEqualTo(original.getSort().and(Sort.by("id")));
    }

    @Test
    public void testPageableUtilIgnoreCase() {
        // Given a pageable sorted by name
        Pageable original = PageRequest.of(0, 10, Sort.by("name"));
        // when the pageable specifies case-insensitive sorting on name
        Pageable pageable = pageableUtil.adjustPageable(original, "id", "name");
        // then the page number and page size should be unchanged
        assertThat(pageable.getPageNumber()).isEqualTo(original.getPageNumber());
        assertThat(pageable.getPageSize()).isEqualTo(original.getPageSize());
        // and the sort order for name should ignore case
        Sort.Order order = pageable.getSort().getOrderFor("name");
        assertThat(order != null && order.isIgnoreCase()).isTrue();
    }

}
