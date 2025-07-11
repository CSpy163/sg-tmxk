package site.cspy.core.template;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class HtmlTemplateContext implements SgTemplateContext {

    Context context = new Context();

    static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }


    public HtmlTemplateContext(Object data) {
        if (data != null) {
            Map<String, Object> dataMap = objectMapper.convertValue(data, new TypeReference<>() {
            });
            LocalDateTime day = LocalDateTime.now();
            dataMap.forEach(context::setVariable);
        }
    }

    @Override
    public Context toContext(Context ctx) {
        return Optional.ofNullable(ctx)
                .map(envContext -> {
                    context.getVariableNames().forEach(name -> envContext.setVariable(name, context.getVariable(name)));
                    return envContext;
                })
                .orElse(context);
    }

    @Override
    public String getTemplate() {
        return "你好[[${name}]],\n" +
                "                我的生日是[(${#temporals.format(birthday, 'HH:mm:ss')})]。 [(${#temporals.format(birthday, 'yyyy-MM-dd')})]\n" +
                "                我今天[[${age}]]岁了。";
    }

    @Override
    public SgTemplateInfo getReportInfo() {
        return SgTemplateContext.super.getReportInfo();
    }
}
