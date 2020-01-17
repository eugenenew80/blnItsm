package bln.itsm.schedule;

import bln.itsm.client.query.QueryRequestDto;
import bln.itsm.client.query.QueryResponseDto;
import bln.itsm.client.RestClient;
import bln.itsm.client.rating.RatingRequestDto;
import bln.itsm.entity.Rating;
import bln.itsm.entity.SupportRequest;
import bln.itsm.entity.SupportRequestFile;
import bln.itsm.entity.enums.BatchStatusEnum;
import bln.itsm.repo.RatingRepo;
import bln.itsm.repo.SupportRequestFileRepo;
import bln.itsm.repo.SupportRequestRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final RestClient restClient;
    private final SupportRequestRepo supportRequestRepo;
    private final SupportRequestFileRepo supportRequestFileRepo;
    private final RatingRepo ratingRepo;

    @Scheduled(cron = "*/15 * * * * *")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void startImport() {
        List<SupportRequest> list = supportRequestRepo.findByStatus(BatchStatusEnum.W);
        if (list.size() > 0) {
            logger.info("Sending requests, count of records: " + list.size());
            for (SupportRequest req : list)
                sendRequestQuery(req);
            updateStatuses();
        }

        List<SupportRequestFile> fileList = supportRequestFileRepo.findByStatus(BatchStatusEnum.W);
        if (fileList.size() > 0) {
            logger.info("Sending request files, count of records: " + fileList.size());
            for (SupportRequestFile file : fileList)
                sendFile(file);
            updateStatuses();
        }

        List<Rating> listRatings = ratingRepo.findByTransferStatus(BatchStatusEnum.W);
        if (listRatings.size() > 0) {
            logger.info("Sending ratings, count of records: " + listRatings.size());
            for (Rating eval : listRatings)
                sendRatingQuery(eval);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void sendRequestQuery(SupportRequest req) {
        logger.info("Sending request query: " + req.getId());

        String msg;
        BatchStatusEnum status;
        QueryResponseDto responseBody;
        responseBody = null;

        //sending request
        ResponseEntity<String> response;
        try {
            QueryRequestDto insertQuery = req.buildRequestQuery();
            response = restClient.request(insertQuery);
            String responseBodyStr = response.getBody();
            if (response.getStatusCodeValue() == 200) {
                try {
                    responseBody = restClient.jsonStringToObject(responseBodyStr, QueryResponseDto.class);
                    status = BatchStatusEnum.C;
                    msg = "";
                }
                catch (Exception e) {
                    msg = e.getMessage();
                    status =  BatchStatusEnum.E;
                    responseBody = null;
                }
            }
            else {
                msg = responseBodyStr;
                status =  BatchStatusEnum.E;
                responseBody = null;
            }
        }
        catch (Exception e) {
            msg = e.getMessage();
            status =  BatchStatusEnum.E;
        }

        req.setLastUpdateDate(LocalDateTime.now());
        req.setStatus(status);
        req.setIsTransferred(false);
        if (status == BatchStatusEnum.C) {
            req.setGuid(responseBody.getId());
            logger.info("OK!");
        }
        else {
            logger.error("Error during sending request");
            logger.error(msg);
        }
        supportRequestRepo.save(req);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void sendFile(SupportRequestFile file) {
        logger.info("Sending file: " + file.getId());
        SupportRequest r = file.getSupportRequest();
        if (r.getStatus() != BatchStatusEnum.C || r.getGuid() == null)
            return;

        String msg;
        BatchStatusEnum status;
        String responseBody;

        try {
            ResponseEntity<String> response = restClient.fileUpload(file);
            responseBody = response.getBody();
            if (response.getStatusCodeValue() == 200) {
                msg = "";
                status = BatchStatusEnum.C;
            }
            else {
                msg = responseBody;
                status =  BatchStatusEnum.E;
            }
        }
        catch (Exception e) {
            msg = e.getMessage();
            status =  BatchStatusEnum.E;
        }

        file.setLastUpdateDate(LocalDateTime.now());
        file.setStatus(status);
        if (status == BatchStatusEnum.C)
            logger.info("OK!");
        else {
            logger.error("Error during sending request");
            logger.error(msg);
        }
        supportRequestFileRepo.save(file);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void sendRatingQuery(Rating rating) {
        logger.info("Sending rating query: " + rating.getRequestNumber());

        String msg;
        BatchStatusEnum status;
        String responseBody;

        //send request
        try {
            RatingRequestDto insertQuery = rating.buildRatingQuery();
            ResponseEntity<String> response = restClient.request(insertQuery);
            responseBody = response.getBody();
            if (response.getStatusCodeValue() == 200) {
                msg = "";
                status = BatchStatusEnum.C;
            }
            else {
                msg = responseBody;
                status =  BatchStatusEnum.E;
            }
        }
        catch (Exception e) {
            msg = e.getMessage();
            status =  BatchStatusEnum.E;
        }

        rating.setLastUpdateDate(LocalDateTime.now());
        rating.setTransferStatus(status);
        if (status == BatchStatusEnum.C)
            logger.info("OK!");
        else {
            logger.error("Error during sending request");
            logger.error(msg);
        }
        ratingRepo.save(rating);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateStatuses() {
        supportRequestRepo.updateStatuses();
    }
}
