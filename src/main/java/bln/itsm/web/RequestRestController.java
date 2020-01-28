package bln.itsm.web;

import bln.itsm.entity.ActionRequest;
import bln.itsm.entity.ActionFile;
import bln.itsm.entity.enums.BatchStatusEnum;
import bln.itsm.repo.ActionFileRepo;
import bln.itsm.repo.ActionRequestRepo;
import bln.itsm.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import static java.time.LocalDateTime.now;

@RestController
@RequiredArgsConstructor
public class RequestRestController {
    private static final Logger logger = LoggerFactory.getLogger(RequestRestController.class);
    private final ActionRequestRepo actionRepo;
    private final ActionFileRepo actionFileRepo;
    private final DozerBeanMapper mapper;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @PostMapping(value = "/api/v1/itsm/requests/take", produces = "application/json")
    public ResponseEntity<MessageDto> take(@RequestBody RequestTakeDto requestDto) {
        if (requestDto.getRequestNumber() == null || requestDto.getRequestNumber().equals(""))
            throw new ControllerException("request number is null", "RequestRestController.take");

        ActionRequest actionRequest = mapper.map(requestDto, ActionRequest.class);
        buildRequest(actionRequest);
        saveRequest(actionRequest);
        updateStatuses();

        MessageDto messageDto = new MessageDto("info", "RequestRestController.take", "status successfully updated");
        return ResponseEntity.ok(messageDto);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @PostMapping(value = "/api/v1/itsm/requests/suspense", produces = "application/json")
    public ResponseEntity<MessageDto> suspense(@RequestBody RequestSuspenseDto requestDto) {
        ActionRequest actionRequest = mapper.map(requestDto, ActionRequest.class);
        buildRequest(actionRequest);
        saveRequest(actionRequest);
        updateStatuses();

        MessageDto messageDto = new MessageDto("info", "RequestRestController.suspense", "suspense status successfully updated");
        return ResponseEntity
            .ok(messageDto);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @PostMapping(value = "/api/v1/itsm/requests/complete", produces = "application/json")
    public ResponseEntity<MessageDto> complete(@RequestBody RequestCompleteDto requestDto) {
        logger.debug("RequestRestController.complete started");
        logger.trace("request body: {}", requestDto);
        logger.trace("file counts: {}", requestDto.getFiles().size());

        ActionRequest actionRequest = mapper.map(requestDto, ActionRequest.class);
        buildRequest(actionRequest);
        saveRequest(actionRequest);

        //Получаем список файлов
        List<ActionFile> actionFiles = new ArrayList<>();
        if (requestDto.getFiles() != null) {
            for (RequestFileDto requestFileDto : requestDto.getFiles()) {
                logger.trace("file: {}", requestFileDto.getFileName());
                byte[] contentAsBytes = Base64.decodeBase64(requestFileDto.getFileContentBase64());
                ActionFile file = new ActionFile();
                file.setActionRequest(actionRequest);
                file.setFileName(requestFileDto.getFileName());
                file.setFileSize(requestFileDto.getFileSize());
                file.setFileType(requestFileDto.getFileType());
                file.setFileContent(contentAsBytes);
                actionFiles.add(file);
            }
        }
        saveFiles(actionFiles);
        updateStatuses();

        logger.debug("RequestRestController.complete completed");
        MessageDto messageDto = new MessageDto("info", "RequestRestController.complete", "complete status successfully updated");
        return ResponseEntity
            .ok(messageDto);

    }

    private void buildRequest(ActionRequest actionRequest) {
        ActionRequest oldActionRequest = actionRepo.findByRequestNumberAndStatus(actionRequest.getRequestNumber(), BatchStatusEnum.W);
        if (oldActionRequest != null) {
            actionRequest.setId(oldActionRequest.getId());
            actionRequest.setCreateDate(oldActionRequest.getCreateDate());
            actionRequest.setLastUpdateDate(now());
            actionRequest.setStatus(BatchStatusEnum.W);
        } else {
            actionRequest.setStatus(BatchStatusEnum.W);
            actionRequest.setCreateDate(now());
        }
        actionRequest.setIsTransferred(false);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveRequest(ActionRequest actionRequest) {
        actionRepo.save(actionRequest);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveFiles(List<ActionFile> files) {
        if (files.size() == 0)
            return;

        logger.trace("saving files, count: {}", files.size());
        actionFileRepo.save(files);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateStatuses() {
        actionRepo.updateStatuses();
    }

    @ExceptionHandler( { Throwable.class } )
    public ResponseEntity<MessageDto> handleException(Throwable exc) {
        MessageDto messageDto;
        HttpStatus status;
        if (exc instanceof ControllerException) {
            messageDto = new MessageDto("error", ((ControllerException) (exc)).getPath(), exc.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        else {
            messageDto = new MessageDto("error", "RequestRestController", exc.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(messageDto, status);
    }
}
