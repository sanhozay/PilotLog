package org.flightgear.pilotlog.domain;

/**
 * Class to represent a paged total.
 *
 * @param <T> the numeric type of the total
 * @author Richard Senior
 */
@SuppressWarnings("unchecked")
public class Total<T extends Number> {

    private final T total;
    private final T pageTotal;

    /**
     * Construct a total.
     *
     * @param pageTotal the total for the current page
     * @param total the grand total
     */
    public Total(T pageTotal, T total) {
        this.pageTotal = pageTotal;
        this.total = total;
    }

    /**
     * Gets the total for this page.
     *
     * @return the page total
     */
    public T getPageTotal() {
        return pageTotal;
    }

    /**
     * Gets the combined total for the other pages.
     *
     * @return the other total
     */
    public T getOtherTotal() {
        if (total instanceof Float) {
            return (T)Float.valueOf(total.floatValue() - pageTotal.floatValue());
        }
        if (total instanceof Double) {
            return (T)Double.valueOf(total.doubleValue() - pageTotal.doubleValue());
        }
        if (total instanceof Integer) {
            return (T)Integer.valueOf(total.intValue() - pageTotal.intValue());
        }
        if (total instanceof Long) {
            return (T)Long.valueOf(total.longValue() - pageTotal.longValue());
        }
        throw new IllegalArgumentException("Unsupported numeric type");
    }

    /**
     * Gets the overall total.
     *
     * @return the total
     */
    public T getTotal() {
        return total;
    }

}
