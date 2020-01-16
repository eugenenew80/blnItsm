package bln.itsm.web;

import bln.itsm.entity.Request;
import bln.itsm.entity.RequestFile;
import bln.itsm.entity.enums.BatchStatusEnum;
import bln.itsm.repo.RequestFileRepo;
import bln.itsm.repo.RequestRepo;
import bln.itsm.web.dto.RequestCompleteDto;
import bln.itsm.web.dto.RequestFileDto;
import bln.itsm.web.dto.RequestSuspenseDto;
import bln.itsm.web.dto.RequestTakeDto;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.dozer.DozerBeanMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import static java.time.LocalDateTime.now;

@RestController
@RequiredArgsConstructor
public class RequestRestController {
    private final RequestRepo repo;
    private final RequestFileRepo requestFileRepo;
    private final DozerBeanMapper mapper;

    @PostMapping(value = "/api/v1/itsm/requests/take", produces = "application/json")
    public ResponseEntity<Void> take(@RequestBody RequestTakeDto requestDto) {
        Request request = mapper.map(requestDto, Request.class);
        Request oldRequest = repo.findByRequestNumberAndStatus(requestDto.getRequestNumber(), BatchStatusEnum.W);
        if (oldRequest != null) {
            request.setId(oldRequest.getId());
            request.setCreateDate(oldRequest.getCreateDate());
            request.setLastUpdateDate(now());
            request.setStatus(BatchStatusEnum.W);
        }
        else {
            request.setStatus(BatchStatusEnum.W);
            request.setCreateDate(now());
        }

        repo.save(request);
        return ResponseEntity.ok()
            .build();
    }

    @PostMapping(value = "/api/v1/itsm/requests/suspense", produces = "application/json")
    public ResponseEntity<Void> suspense(@RequestBody RequestSuspenseDto requestDto) {
        Request request = mapper.map(requestDto, Request.class);
        Request oldRequest = repo.findByRequestNumberAndStatus(requestDto.getRequestNumber(), BatchStatusEnum.W);
        if (oldRequest != null) {
            request.setId(oldRequest.getId());
            request.setCreateDate(oldRequest.getCreateDate());
            request.setLastUpdateDate(now());
            request.setStatus(BatchStatusEnum.W);
        }
        else {
            request.setStatus(BatchStatusEnum.W);
            request.setCreateDate(now());
        }

        repo.save(request);
        return ResponseEntity.ok()
            .build();
    }

    @PostMapping(value = "/api/v1/itsm/requests/complete", produces = "application/json")
    public ResponseEntity<Void> complete(@RequestBody RequestCompleteDto requestDto) {
        Request request = mapper.map(requestDto, Request.class);
        Request oldRequest = repo.findByRequestNumberAndStatus(requestDto.getRequestNumber(), BatchStatusEnum.W);
        if (oldRequest != null) {
            request.setId(oldRequest.getId());
            request.setCreateDate(oldRequest.getCreateDate());
            request.setLastUpdateDate(now());
            request.setStatus(BatchStatusEnum.W);
        }
        else {
            request.setStatus(BatchStatusEnum.W);
            request.setCreateDate(now());
        }

        //Получаем список файлов
        List<RequestFile> requestFiles = new ArrayList<>();
        if (requestDto.getFiles() != null) {
            for (RequestFileDto requestFileDto : requestDto.getFiles()) {
                byte[] contentAsBytes = Base64.decodeBase64(requestFileDto.getFileContentBase64());
                RequestFile file = new RequestFile();
                file.setRequest(request);
                file.setFileName(requestFileDto.getFileName());
                file.setFileSize(requestFileDto.getFileSize());
                file.setFileType(requestFileDto.getFileType());
                file.setFileContent(contentAsBytes);
                requestFiles.add(file);
            }
        }

        //Сохраняем данные
        repo.save(request);
        if (requestFiles.size() > 0)
            requestFileRepo.save(requestFiles);

        return ResponseEntity.ok()
            .build();
    }
}
