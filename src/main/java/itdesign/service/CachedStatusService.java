package itdesign.service;

import itdesign.entity.Status;

public interface CachedStatusService {
    Status getStatus(String statusCode, String lang);
}
