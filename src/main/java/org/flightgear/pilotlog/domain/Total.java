package org.flightgear.pilotlog.domain;

@SuppressWarnings("unchecked")
public class Total<T extends Number> {

    private final T total;
    private final T pageTotal;

    public Total(T pageTotal, T total) {
        this.pageTotal = pageTotal;
        this.total = total;
    }

    public T getPageTotal() {
        return pageTotal;
    }

    public T getOtherTotal() {
        if (total instanceof Float) {
            return (T)new Float(total.floatValue() - pageTotal.floatValue());
        }
        if (total instanceof Double) {
            return (T)new Double(total.doubleValue() - pageTotal.doubleValue());
        }
        if (total instanceof Integer) {
            return (T)new Integer(total.intValue() - pageTotal.intValue());
        }
        if (total instanceof Long) {
            return (T)new Long(total.longValue() - pageTotal.longValue());
        }
        throw new IllegalArgumentException("Unsupported numeric type");
    }

    public T getTotal() {
        return total;
    }

}
