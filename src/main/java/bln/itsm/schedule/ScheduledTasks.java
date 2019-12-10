package bln.itsm.schedule;

import bln.itsm.client.ItemValueDto;
import bln.itsm.client.ParameterDto;
import bln.itsm.client.query.QueryColumnValueDto;
import bln.itsm.client.query.QueryItemDto;
import bln.itsm.client.query.QueryRequestDto;
import bln.itsm.client.query.QueryResponseDto;
import bln.itsm.client.rating.RatingColumnValueDto;
import bln.itsm.client.rating.RatingItemDto;
import bln.itsm.client.rating.RatingRequestDto;
import bln.itsm.client.RestClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final RestClient restClient;

    //@Scheduled(cron = "*/15 * * * * *")
    public void startImport() {

        //Формируем объект для передачи
        ItemValueDto descriptionDto = new ItemValueDto(2, new ParameterDto(1, "Проблема есть в договорах"));
        ItemValueDto requestAuthorDto = new ItemValueDto(2, new ParameterDto(1, "Иванов Иван Иванович"));
        ItemValueDto companyDto = new ItemValueDto(2, new ParameterDto(1, "ИП Маша Метелкина"));
        ItemValueDto categoryDto = new ItemValueDto(2, new ParameterDto(1, "Договора"));
        ItemValueDto subjectDto = new ItemValueDto(2, new ParameterDto(1, "Проблема"));
        QueryItemDto itemDto = new QueryItemDto();
        itemDto.setDescription(descriptionDto);
        itemDto.setRequestAuthor(requestAuthorDto);
        itemDto.setCompany(companyDto);
        itemDto.setCategory(categoryDto);
        itemDto.setSubject(subjectDto);
        QueryColumnValueDto columnValueDto = new QueryColumnValueDto(itemDto);
        QueryRequestDto queryDto = new QueryRequestDto("INFBISRequest", 1, columnValueDto);

        //Выводим ответ
        ResponseEntity<QueryResponseDto> queryResponse = restClient.request("temp40a", "Q1w2e3r4t%777", queryDto);
        logger.info("status:" + queryResponse.getStatusCodeValue());
        logger.info("body:" + queryResponse.getBody());


        ItemValueDto requestNumberDto = new ItemValueDto(2, new ParameterDto(1, queryResponse.getBody().getId()));
        ItemValueDto qualityCodeDto = new ItemValueDto(2, new ParameterDto(1, "5"));
        ItemValueDto evaluationDateDto = new ItemValueDto(2, new ParameterDto(8, "2019-12-10T14:50:00"));

        RatingItemDto itemRatingDto = new RatingItemDto();
        itemRatingDto.setRequestNumber(requestNumberDto);
        itemRatingDto.setQualityCode(qualityCodeDto);
        itemRatingDto.setEvalutionDate(evaluationDateDto);
        RatingColumnValueDto ratigColumnValueDto = new RatingColumnValueDto(itemRatingDto);
        RatingRequestDto ratingDto = new RatingRequestDto("INFBISRating", 1, ratigColumnValueDto);

        restClient.rating("temp40a", "Q1w2e3r4t%777", ratingDto);
    }

}
