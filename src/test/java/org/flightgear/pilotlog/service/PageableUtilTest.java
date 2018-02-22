package org.flightgear.pilotlog.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
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
        Pageable original = new PageRequest(0, 10, new Sort("id"));
        // when the pageable is adjusted for stabilization using id
        Pageable pageable = pageableUtil.adjustPageable(original, "id");
        // then the pageable should be unchanged
        assertThat(original).isEqualTo(pageable);
    }

    @Test
    public void testPageableUtilNonStable() {
        // Given a pageable sorted unstable by name
        Pageable original = new PageRequest(0, 10, new Sort("name"));
        // when the pageable is adjusted for stabilization using id
        Pageable pageable = pageableUtil.adjustPageable(original, "id");
        // then the page number and page size should be unchanged
        assertThat(pageable.getPageNumber()).isEqualTo(original.getPageNumber());
        assertThat(pageable.getPageSize()).isEqualTo(original.getPageSize());
        // and the sort should be stabilized by adding sort by id to it
        assertThat(pageable.getSort()).isEqualTo(original.getSort().and(new Sort("id")));
    }

    @Test
    public void testPageableUtilIgnoreCase() {
        // Given a pageable sorted by name
        Pageable original = new PageRequest(0, 10, new Sort("name"));
        // when the pageable specifies case-insensitive sorting on name
        Pageable pageable = pageableUtil.adjustPageable(original, "id", "name");
        // then the page number and page size should be unchanged
        assertThat(pageable.getPageNumber()).isEqualTo(original.getPageNumber());
        assertThat(pageable.getPageSize()).isEqualTo(original.getPageSize());
        // and the sort order for name should ignore case
        assertThat(pageable.getSort().getOrderFor("name").isIgnoreCase()).isTrue();
    }

}
