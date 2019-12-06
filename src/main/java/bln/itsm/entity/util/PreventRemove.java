package bln.itsm.entity.util;

import javax.persistence.PreRemove;

public class PreventRemove {
    @PreRemove
    void onPreRemove(Object o) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
}
