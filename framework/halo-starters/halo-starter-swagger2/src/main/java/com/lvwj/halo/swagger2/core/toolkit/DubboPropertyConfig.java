package com.lvwj.halo.swagger2.core.toolkit;

import com.lvwj.halo.swagger2.config.properties.Swagger2Properties;
import io.swagger.config.SwaggerConfig;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class DubboPropertyConfig implements SwaggerConfig {

    @Autowired
    private ReferenceManager referenceManager;

    @Autowired
    private Swagger2Properties swagger2Properties;

    @Autowired
    private ServerProperties serverProperties;

    private static final String mavenDependency = "&lt;dependency&gt;<br/>"
            + "&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;{0}&lt;/groupId&gt;<br/>"
            + "&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;{1}&lt;/artifactId&gt;<br/>"
            + "&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;{2}&lt;/version&gt;<br/>"
            + "&lt;/dependency&gt;<br/>";

    @Override
    public Swagger configure(Swagger swagger) {
        ApplicationConfig application = referenceManager.getApplication();
        if (null != application) {
            Info info = swagger.getInfo();
            if (info == null) {
                info = new Info();
                swagger.setInfo(info);
            }
            info.setTitle(application.getName());

            String artifactId = swagger2Properties.getDubbo().getApplication().getArtifactId();
            String groupId = swagger2Properties.getDubbo().getApplication().getGroupId();
            String version = swagger2Properties.getDubbo().getApplication().getVersion();
            if(StringUtils.isBlank(version)){
                version = application.getVersion();
            }
            if(StringUtils.isBlank(groupId)){
                groupId = application.getOrganization();
            }
            if(StringUtils.isBlank(artifactId)){
                artifactId = application.getArchitecture();
            }
            if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(artifactId) && StringUtils.isNotBlank(version)) {
                info.setDescription(MessageFormat.format(mavenDependency, groupId, artifactId, version));
            }
            info.setVersion(StringUtils.isNotBlank(version) ? version : "");

            Contact contact = new Contact();
            info.setContact(contact);
            contact.setName(application.getOwner());
        }
        setBashPath(swagger);
        return swagger;
    }

    private void setBashPath(Swagger swagger) {
        if (StringUtils.isEmpty(swagger.getBasePath())) {
            String contextPath = serverProperties.getServlet().getContextPath();
            swagger.setBasePath(StringUtils.isEmpty(contextPath) ? "/" : contextPath);
        }
    }

    @Override
    public String getFilterClass() {
        return null;
    }
}
