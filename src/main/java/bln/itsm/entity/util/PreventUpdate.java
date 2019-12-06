package bln.itsm.entity.util;

import javax.persistence.PreUpdate;

public class PreventUpdate {
    @PreUpdate
    void onPreUpdate(Object o) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
}
