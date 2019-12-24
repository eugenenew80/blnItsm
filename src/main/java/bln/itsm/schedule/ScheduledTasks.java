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
import bln.itsm.entity.SupportRequest;
import bln.itsm.entity.enums.BatchStatusEnum;
import bln.itsm.repo.EvaluationRepo;
import bln.itsm.repo.SupportRequestRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

//    @Scheduled(cron = "*/15 * * * * *")
    public void startImport() {

        //Вызываем метод авторизации в системе ITSM
        ResponseEntity<LoginResponseDto> loginResponse = restClient.login(user, password);
        if (loginResponse.getStatusCodeValue() != 200) {
            logger.error("Ршибка авторизации}");
            return;
        }

        //Ищем список записей, ожидающих обработки и вызываем метод передачи данных в систему ITSM
        List<SupportRequest> list = supportRequestRepo.findByStatus(BatchStatusEnum.W);
        for (SupportRequest r : list) {
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
            QueryRequestDto insertQuery = new QueryRequestDto("INFBISRequest", 1, columnValue);

            //Выводим ответ
            ResponseEntity<QueryResponseDto> queryResponse = restClient.request(loginResponse, insertQuery);
            if (queryResponse.getStatusCodeValue() == 200) {
                QueryResponseDto responseBody = queryResponse.getBody();

                logger.info("status:" + queryResponse.getStatusCodeValue());
                logger.info("body:" + responseBody);

                r.setGuid(responseBody.getId());
                r.setStatus(BatchStatusEnum.C);
            }
            else {
                r.setStatus(BatchStatusEnum.E);
            }

            supportRequestRepo.save(r);
        }

        /*
        * send evaluations
        * */
        evaluationRepo.findByTransferStatus(BatchStatusEnum.W).forEach(evaluation -> {

            //requestNumber
            ParameterDto requestNumberParameter = new ParameterDto(1, evaluation.getRequestNumber());
            ItemValueDto requestNumberItemValue = new ItemValueDto(2, requestNumberParameter);

            //qualityCode
            ParameterDto qualityCodeParameter = new ParameterDto(1, evaluation.getQualityCode());
            ItemValueDto qualityCodeItemValue = new ItemValueDto(2, qualityCodeParameter);

            //evalutionDate
            ParameterDto evalutionDateParameter = new ParameterDto(8,
                                                        evaluation.getEvaluationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
            ItemValueDto evalutionDateItemValue = new ItemValueDto(2, qualityCodeParameter);

            RatingItemDto item = new RatingItemDto(requestNumberItemValue, qualityCodeItemValue, evalutionDateItemValue);
            RatingColumnValueDto ratingColumnValueDto = new RatingColumnValueDto(item);

            RatingRequestDto insertQuery =
                                new RatingRequestDto("INFBISRating", 1, ratingColumnValueDto);

            //send to itsm
            ResponseEntity<QueryResponseDto> queryResponse = restClient.request(loginResponse, insertQuery);

            if(queryResponse.getStatusCode() == HttpStatus.OK) {
                evaluation.setTransferStatus(BatchStatusEnum.C);
            } else {
                evaluation.setTransferStatus(BatchStatusEnum.E);
            }

        });
    }
}
