package kz.kegoc.bln.gateway.oic;

import java.time.LocalDateTime;
import java.util.List;

public interface OicImpGateway {
    OicImpGateway config(OicConfig config);
    OicImpGateway points(List<Long> points);
    List<TelemetryRaw> request(LocalDateTime requestedTime) throws Exception;
}