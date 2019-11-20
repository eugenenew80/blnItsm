package itdesign.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import itdesign.entity.ReportCode;
import itdesign.repo.ReportCodeRepo;
import itdesign.web.dto.ReportCodeDto;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Api(tags = "API для работы с кодами отчётов")
@RestController
@RequiredArgsConstructor
public class ReportCodeRestController extends BaseController {
    private static final String className = ReportCodeRestController.class.getName();
    private final ReportCodeRepo repo;
    private final DozerBeanMapper mapper;

    @PostConstruct
    private void init() {
        logger.debug(className + ".init()");
        transformToDto = t -> mapper.map(t, ReportCodeDto.class);
    }

    @ApiOperation(value="Получить список всех записей")
    @GetMapping(value = "/api/v1/{lang}/slices/reportCodes", produces = "application/json")
    public List<ReportCodeDto> getAll(@PathVariable(value = "lang") @ApiParam(value = "Язык", example = "RU") String lang) {
        logger.debug(className + ".getAll()");
        logger.trace("lang: " + lang);

        return repo.findAllByLang(lang.toUpperCase())
            .stream()
            .map(transformToDto::apply)
            .collect(Collectors.toList());
    }

    private Function<ReportCode, ReportCodeDto> transformToDto;
}
