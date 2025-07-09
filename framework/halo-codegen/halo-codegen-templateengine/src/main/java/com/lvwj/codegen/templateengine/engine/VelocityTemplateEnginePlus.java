package com.lvwj.codegen.templateengine.engine;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.lvwj.codegen.templateengine.config.ConfigBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author lvweijie
 * @date 2024年11月04日 15:45
 */
@Slf4j
public class VelocityTemplateEnginePlus  extends AbstractTemplateEnginePlus {

    private VelocityEngine velocityEngine;

    {
        try {
            Class.forName("org.apache.velocity.util.DuckType");
        } catch (ClassNotFoundException e) {
            // velocity1.x的生成格式错乱 https://github.com/baomidou/generator/issues/5
            log.warn("Velocity 1.x is outdated, please upgrade to 2.x or later.");
        }
    }

    @Override
    public VelocityTemplateEnginePlus init(ConfigBuilder configBuilder) {
        if (null == velocityEngine) {
            Properties p = new Properties();
            p.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", "true");
            velocityEngine = new VelocityEngine(p);
        }
        return this;
    }


    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, File outputFile) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter ow = new OutputStreamWriter(fos, ConstVal.UTF8);
             BufferedWriter writer = new BufferedWriter(ow)) {
            template.merge(new VelocityContext(objectMap), writer);
        }
        log.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }


    @Override
    public String templateFilePath(String filePath) {
        final String dotVm = ".vm";
        return filePath.endsWith(dotVm) ? filePath : filePath + dotVm;
    }
}
