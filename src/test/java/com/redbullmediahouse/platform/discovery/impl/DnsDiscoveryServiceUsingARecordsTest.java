package com.redbullmediahouse.platform.discovery.impl;

import com.redbullmediahouse.platform.discovery.ServiceDefinition;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(VertxUnitRunner.class)
public class DnsDiscoveryServiceUsingARecordsTest {

    @Rule
    public final RunTestOnContext rule = new RunTestOnContext();
    private final String dnsHost = "ns-171.awsdns-21.com";
    private final int dnsPort = 53;
    private final Map<String, Integer> nameToPorts = new HashMap<String, Integer>() {
        {
            this.put("cassandra", 9042);
            this.put("api1", 100);
        }
    };
    private Vertx vertx;
    private DnsDiscoveryServiceUsingARecords dnsDiscoveryService;

    @Before
    public void setupVerticle(final TestContext context) throws Exception {
        vertx = rule.vertx();

        dnsDiscoveryService = new DnsDiscoveryServiceUsingARecords(vertx,
                ".rbtv.preprod.data.metriculo.us", dnsHost, dnsPort, nameToPorts);
    }

    @Test
    public void testConversion() throws Exception {

        final List<String> addresses = new ArrayList<>();
        addresses.add("192.168.99.100");
        ServiceDefinition serviceDefinition = dnsDiscoveryService.createServiceDefinition(addresses, "cassandra");

        assertEquals(9042, serviceDefinition.getPort());
        assertEquals("192.168.99.100", serviceDefinition.getAddress());
    }


    @Test
    public void testManyConversions() throws Exception {

        final List<String> addresses = new ArrayList<>();

        for (int index = 0; index < 200; index++) {
            addresses.add("192.168.99." + index);
        }

        for (int index = 0; index < 1000; index++) {
            ServiceDefinition serviceDefinition = dnsDiscoveryService.createServiceDefinition(addresses, "cassandra");
            assertEquals(9042, serviceDefinition.getPort());
            assertTrue(serviceDefinition.getAddress().startsWith("192.168"));
        }

    }

    @Test
    public void testIntegration(final TestContext context) throws Exception {

        final Async async = context.async();

        dnsDiscoveryService.lookupServiceByName("api1",
                serviceDefinitionAsyncResult -> {
                    context.assertEquals(100, serviceDefinitionAsyncResult.result().getPort());
                    async.complete();
                });

    }


    @Test
    public void testNotFound(final TestContext context) throws Exception {

        final Async async = context.async();

        dnsDiscoveryService.lookupServiceByName("bullshit",
                serviceDefinitionAsyncResult -> {
                    context.assertTrue(serviceDefinitionAsyncResult.failed());
                    serviceDefinitionAsyncResult.cause().printStackTrace();
                    async.complete();
                });

    }

    @Test
    public void testIntegrationUsingPort(final TestContext context) throws Exception {

        final Async async = context.async();

        dnsDiscoveryService.lookupServiceByNameAndContainerPort("api1", 101,
                serviceDefinitionAsyncResult -> {
                    context.assertEquals(101, serviceDefinitionAsyncResult.result().getPort());
                    async.complete();
                });

    }

}