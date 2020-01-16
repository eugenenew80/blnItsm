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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final String user = "temp40a";
    private static final String password = "Q1w2e3r4t%777";
    private final RestClient restClient;
    private final SupportRequestRepo supportRequestRepo;

    @Autowired
    private EvaluationRepo evaluationRepo;

    @Scheduled(cron = "*/15 * * * * *")
    public void startImport() {
        List<SupportRequest> list = supportRequestRepo.findByStatus(BatchStatusEnum.W);
        if (list.size() > 0)
            logger.info("Sending itsm requests, count of records: " + list.size());

        for (SupportRequest req : list) {
            sendRequestQuery(req);
            supportRequestRepo.save(req);
        }


        List<Evaluation> listEval = evaluationRepo.findByTransferStatus(BatchStatusEnum.W);
        if (listEval.size() > 0)
            logger.info("Sending ratings, count of records: " + listEval.size());

        for (Evaluation eval : listEval) {
            sendRatingQuery(eval);
            evaluationRepo.save(eval);
        }
    }

    private void sendRequestQuery(SupportRequest r) {
        logger.info("Sending request query: " + r.getId());
        QueryRequestDto insertQuery = buildRequestQuery(r);

        //sending request
        ResponseEntity<String> queryResponse = restClient.request(insertQuery);

        boolean isSuccess = false;
        String responseBodyStr = queryResponse.getBody();
        if (queryResponse.getStatusCodeValue() == 200) {
            QueryResponseDto responseBody = restClient.jsonStringToObject(responseBodyStr, QueryResponseDto.class);
            if (responseBody != null) {
                logger.info("OK!");

                r.setGuid(responseBody.getId());
                r.setStatus(BatchStatusEnum.C);
                isSuccess = true;
            }
        }

        if (!isSuccess) {
            logger.error("ERROR: ", responseBodyStr);
            r.setStatus(BatchStatusEnum.E);
        }

        if (isSuccess && !r.getRequestFiles().isEmpty()) {
            for (SupportRequestFile rf : r.getRequestFiles()) {
                logger.info("Sending file: " + rf.getId());
                ResponseEntity<String> responseBody = restClient.fileUpload(rf);
            }
        }
    }

    private void sendRatingQuery(Evaluation eval) {
        logger.info("Sending rating query: " + eval.getRequestNumber());
        RatingRequestDto insertQuery = buildRatingQuery(eval);

        //Read response
        ResponseEntity<String> queryResponse = restClient.request(insertQuery);
        String responseBodyStr = queryResponse.getBody();
        boolean isSuccess = false;
        if (queryResponse.getStatusCodeValue() == 200) {
            logger.info("OK!");
            eval.setTransferStatus(BatchStatusEnum.C);
            isSuccess = true;
        }

        if (!isSuccess) {
            logger.error("ERROR: ", responseBodyStr);
            eval.setTransferStatus(BatchStatusEnum.E);
        }
    }

    private RatingRequestDto buildRatingQuery(Evaluation e) {
        String dateStr = "\"" + e.getEvaluationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")) + "\"";

        ParameterDto requestNumberParameter = new ParameterDto(1, e.getRequestNumber());
        ParameterDto qualityCodeParameter = new ParameterDto(1, e.getQualityCode());
        ParameterDto evaluationDateParameter = new ParameterDto(8, dateStr);

        ItemValueDto requestNumberItemValue = new ItemValueDto(2, requestNumberParameter);
        ItemValueDto qualityCodeItemValue = new ItemValueDto(2, qualityCodeParameter);
        ItemValueDto evaluationDateItemValue = new ItemValueDto(2, evaluationDateParameter);

        RatingItemDto item = new RatingItemDto(requestNumberItemValue, qualityCodeItemValue, evaluationDateItemValue);
        RatingColumnValueDto ratingColumnValueDto = new RatingColumnValueDto(item);
        return new RatingRequestDto("INFBISRating", 1, ratingColumnValueDto);
    }

    private QueryRequestDto buildRequestQuery(SupportRequest r) {
        ParameterDto descriptionParam = new ParameterDto(1, r.getDescription());
        ParameterDto authorParam = new ParameterDto(1, r.getAuthor());
        ParameterDto companyParam = new ParameterDto(1, r.getCompany());
        ParameterDto categoryParam = new ParameterDto(1, r.getCategory());
        ParameterDto subjectParam = new ParameterDto(1, r.getSubject());

        ItemValueDto descriptionItemValue = new ItemValueDto(2, descriptionParam);
        ItemValueDto authorItemValue = new ItemValueDto(2, authorParam);
        ItemValueDto companyItemValue = new ItemValueDto(2, companyParam);
        ItemValueDto categoryItemValue = new ItemValueDto(2, categoryParam);
        ItemValueDto subjectItemValue = new ItemValueDto(2, subjectParam);
        QueryItemDto item = new QueryItemDto();

        item.setDescription(descriptionItemValue);
        item.setRequestAuthor(authorItemValue);
        item.setCompany(companyItemValue);
        item.setCategory(categoryItemValue);
        item.setSubject(subjectItemValue);

        QueryColumnValueDto columnValue = new QueryColumnValueDto(item);
        return new QueryRequestDto("INFBISRequest", 1, columnValue);
    }
}
