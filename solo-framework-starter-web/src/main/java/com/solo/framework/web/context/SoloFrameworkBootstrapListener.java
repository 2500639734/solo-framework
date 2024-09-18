package com.solo.framework.web.context;

import cn.hutool.core.util.ObjectUtil;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import com.solo.framework.core.properties.SoloFrameworkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
public class SoloFrameworkBootstrapListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            ApplicationReadyEvent readyEvent = (ApplicationReadyEvent) event;
            ConfigurableApplicationContext applicationContext = readyEvent.getApplicationContext();
            if(! (applicationContext instanceof WebApplicationContext) ){
                return;
            }

            ConfigurableEnvironment environment = readyEvent.getApplicationContext().getEnvironment();
            SoloFrameworkProperties soloFrameworkProperties = SoloFrameworkContextHolder.getApplicationContext().getBean(SoloFrameworkProperties.class);
            printStartInfo(environment, soloFrameworkProperties);
        }
    }

    private static void printStartInfo(ConfigurableEnvironment environment, SoloFrameworkProperties soloFrameworkProperties) {
        Binder binder = Binder.get(environment);
        ServerProperties serverProperties = binder.bind("server", Bindable.of(ServerProperties.class))
                .orElse(new ServerProperties());

        String applicationName = ObjectUtil.defaultIfBlank(SoloFrameworkRuntimeInfo.INSTANCE.getApplicationName(), "null(建议配置)");
        Integer serverPort = ObjectUtil.defaultIfNull(serverProperties.getPort(), 8080);
        String contextPath = ObjectUtil.defaultIfBlank(serverProperties.getServlet().getContextPath(), "/");
        if (log.isDebugEnabled() || log.isInfoEnabled()) {
            log.info("Solo Framework Start Success! applicationName: {}, serverPort: {},  contextPath: {}", applicationName, serverPort, contextPath);
        }
        printSwagger(soloFrameworkProperties, serverPort, contextPath);
    }

    private static void printSwagger(SoloFrameworkProperties soloFrameworkProperties, Integer serverPort, String contextPath) {
        if (soloFrameworkProperties.getWeb().getSwagger().isEnabled()) {
            if (log.isDebugEnabled() || log.isInfoEnabled()) {
                log.info("Swagger UI 接口文档地址: [http://localhost:{}{}/swagger-ui/index.html], Knife4j UI 接口文档地址: [http://localhost:{}{}/doc.html]",
                        serverPort, contextPath, serverPort, contextPath);
            }
        } else {
            if (log.isDebugEnabled() || log.isInfoEnabled()) {
                log.info("Swagger UI 未开启, 请检查${solo.framework.web.swagger.enabled}配置(生产环境忽略)!");
            }
        }
    }

}
