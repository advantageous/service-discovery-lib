package com.redbullmediahouse.platform.discovery;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.TreeMap;

@DataObject
public class ServiceDefinition {

    private final String address;
    private final int port;

    public ServiceDefinition() {
        this.address = null;
        this.port = 0;
    }

    public ServiceDefinition(final ServiceDefinition serviceDefinition) {
        this.address = serviceDefinition.getAddress();
        this.port = serviceDefinition.getPort();
    }

    public ServiceDefinition(final JsonObject jsonObject) {
        this.address = jsonObject.getString("address");
        this.port = jsonObject.getInteger("port");
    }

    public ServiceDefinition(final String address, final int port) {
        this.address = address;
        this.port = port;
    }

    public JsonObject toJson() {
        return new JsonObject(new TreeMap<String, Object>() {
            {
                put("address", address);
                put("port", port);
            }
        });
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ServiceDefinition{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceDefinition that = (ServiceDefinition) o;

        if (port == that.port) if (address != null ? address.equals(that.address) : that.address == null) return true;
        return false;

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
