package bln.itsm.schedule;

import bln.itsm.client.ItemValueDto;
import bln.itsm.client.ParameterDto;
import bln.itsm.client.login.LoginResponseDto;
import bln.itsm.client.query.QueryColumnValueDto;
import bln.itsm.client.query.QueryItemDto;
import bln.itsm.client.query.QueryRequestDto;
import bln.itsm.client.query.QueryResponseDto;
import bln.itsm.client.RestClient;
import bln.itsm.client.rating.RatingColumnValueDto;
import bln.itsm.client.rating.RatingItemDto;
import bln.itsm.client.rating.RatingRequestDto;
import bln.itsm.entity.Evaluation;
import bln.itsm.entity.RequestFile;
import bln.itsm.entity.SupportRequest;
import bln.itsm.entity.SupportRequestFile;
import bln.itsm.entity.enums.BatchStatusEnum;
import bln.itsm.repo.EvaluationRepo;
import bln.itsm.repo.SupportRequestRepo;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final RestClient restClient;
    private final SupportRequestRepo supportRequestRepo;
    private final EvaluationRepo evaluationRepo;

    @Scheduled(cron = "*/15 * * * * *")
    public void startImport() {
        List<SupportRequest> list = supportRequestRepo.findByStatus(BatchStatusEnum.W);
        if (list.size() > 0)
            logger.info("Sending requests, count of records: " + list.size());

        for (SupportRequest req : list)
            sendRequestQuery(req);

        List<Evaluation> listEval = evaluationRepo.findByTransferStatus(BatchStatusEnum.W);
        if (listEval.size() > 0)
            logger.info("Sending ratings, count of records: " + listEval.size());

        for (Evaluation eval : listEval)
            sendRatingQuery(eval);
    }

    private void sendRequestQuery(SupportRequest r) {
        logger.info("Sending request query: " + r.getId());
        QueryRequestDto insertQuery = r.buildRequestQuery();

        //sending request
        ResponseEntity<String> response = restClient.request(insertQuery);

        boolean isSuccess = false;
        String responseBodyStr = response.getBody();
        if (response.getStatusCodeValue() == 200) {
            QueryResponseDto responseBody = restClient.jsonStringToObject(responseBodyStr, QueryResponseDto.class);
            if (responseBody != null) {
                logger.info("OK!");

                r.setGuid(responseBody.getId());
                r.setStatus(BatchStatusEnum.C);
                r.setLastUpdateDate(LocalDateTime.now());
                isSuccess = true;
            }
        }

        if (!isSuccess) {
            logger.error("ERROR: ", responseBodyStr);
            r.setStatus(BatchStatusEnum.E);
            r.setLastUpdateDate(LocalDateTime.now());
        }
        supportRequestRepo.save(r);

        if (isSuccess && !r.getRequestFiles().isEmpty())
            sendFiles(r);
    }

    private void sendFiles(SupportRequest r) {
        for (SupportRequestFile rf : r.getRequestFiles()) {
            logger.info("Sending file: " + rf.getId());
            ResponseEntity<String> response = restClient.fileUpload(rf);
        }
    }

    private void sendRatingQuery(Evaluation eval) {
        logger.info("Sending rating query: " + eval.getRequestNumber());
        RatingRequestDto insertQuery = eval.buildRatingQuery();

        //send request
        ResponseEntity<String> response = restClient.request(insertQuery);

        //read response
        String responseBody = response.getBody();
        boolean isSuccess = false;
        if (response.getStatusCodeValue() == 200) {
            logger.info("OK!");
            eval.setTransferStatus(BatchStatusEnum.C);
            eval.setLastUpdateDate(LocalDateTime.now());
            isSuccess = true;
        }

        if (!isSuccess) {
            logger.error("ERROR: ", responseBody);
            eval.setTransferStatus(BatchStatusEnum.E);
            eval.setLastUpdateDate(LocalDateTime.now());
        }
        evaluationRepo.save(eval);
    }
}
