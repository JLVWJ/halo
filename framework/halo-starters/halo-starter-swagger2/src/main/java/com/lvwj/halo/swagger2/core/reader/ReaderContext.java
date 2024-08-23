package com.lvwj.halo.swagger2.core.reader;

import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import lombok.Getter;

import java.util.List;

/**
 * The <code>ReaderContext</code> class is wrapper for the <code>Reader</code>
 * parameters.
 */
@Getter
public class ReaderContext {

    private Swagger swagger;
    private Class<?> refCls;
    private Class<?> interfaceCls;
    private String parentPath;
    private String parentHttpMethod;
    private boolean readHidden;
    private List<String> parentConsumes;
    private List<String> parentProduces;
    private List<String> parentTags;
    private List<Parameter> parentParameters;

    public ReaderContext(Swagger swagger, Class<?> refCls, Class<?> interfaceCls, String parentPath,
                         String parentHttpMethod, boolean readHidden, List<String> parentConsumes,
                         List<String> parentProduces, List<String> parentTags,
                         List<Parameter> parentParameters) {
        setSwagger(swagger);
        setRefCls(refCls);
        setInterfaceCls(interfaceCls);
        setParentPath(parentPath);
        setParentHttpMethod(parentHttpMethod);
        setReadHidden(readHidden);
        setParentConsumes(parentConsumes);
        setParentProduces(parentProduces);
        setParentTags(parentTags);
        setParentParameters(parentParameters);
    }

    public void setSwagger(Swagger swagger) {
        this.swagger = swagger;
    }

    public void setRefCls(Class<?> cls) {
        this.refCls = cls;
    }

    public Class<?> getCls() {
        return refCls;
    }

    public void setInterfaceCls(Class<?> interfaceCls) {
        this.interfaceCls = interfaceCls;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setParentHttpMethod(String parentHttpMethod) {
        this.parentHttpMethod = parentHttpMethod;
    }

    public void setReadHidden(boolean readHidden) {
        this.readHidden = readHidden;
    }

    public void setParentConsumes(List<String> parentConsumes) {
        this.parentConsumes = parentConsumes;
    }

    public void setParentProduces(List<String> parentProduces) {
        this.parentProduces = parentProduces;
    }

    public void setParentTags(List<String> parentTags) {
        this.parentTags = parentTags;
    }

    public void setParentParameters(List<Parameter> parentParameters) {
        this.parentParameters = parentParameters;
    }

}
