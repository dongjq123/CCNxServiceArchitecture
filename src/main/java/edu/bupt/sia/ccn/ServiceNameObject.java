package edu.bupt.sia.ccn;

import java.util.Arrays;

/**
 * Created by fish on 16-4-22.
 */
public class ServiceNameObject {
    private String contentName;
    private String serviceName;
    private String[] args;
    private String type;
    private String version;

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ServiceNameObject{" +
                "contentName='" + contentName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
