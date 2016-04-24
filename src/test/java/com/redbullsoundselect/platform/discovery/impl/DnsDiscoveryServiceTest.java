package com.redbullsoundselect.platform.discovery.impl;

import com.redbullsoundselect.platform.discovery.DiscoveryService;
import io.advantageous.reakt.exception.RejectedPromiseException;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.List;

public class DnsDiscoveryServiceTest {

    private static final URI TEST_A_CONFIG = URI.create("dns://ns-620.awsdns-13.net:53");
    private static final URI TEST_SRV_CONFIG = URI.create("dns://192.168.99.100:8600");

    @Test
    public void testConstruct() throws Exception {
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        Assert.assertNotNull(service);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNoConfig() throws Exception {
        new DnsDiscoveryService();
    }

    @Test(expected = RejectedPromiseException.class)
    public void testWithNullQuery() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService((URI) null).invokeWithPromise(promise);
        promise.get();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithWrongScheme() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService("bogus://bogus").invokeWithPromise(promise);
        promise.get();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithWrongSubScheme() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService("dns:bogus://bogus").invokeWithPromise(promise);
        promise.get();
    }

    @Test
    public void testQueryA() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService("dns:A:///ipsec1.rbss.staging.rbmhops.net?port=100").invokeWithPromise(promise);
        List<URI> result = promise.get();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.get(0).getHost().isEmpty());
    }

    @Test(expected = RejectedPromiseException.class)
    public void testQueryANoPort() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService("dns:A:///ipsec1.rbss.staging.rbmhops.net").invokeWithPromise(promise);
        promise.get();
    }

    @Test(expected = RejectedPromiseException.class)
    public void testQueryABadPort() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService("dns:A:///ipsec1.rbss.staging.rbmhops.net?port=bogus").invokeWithPromise(promise);
        promise.get();
    }

    @Test
    public void testQueryAThatDoesNotExist() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_A_CONFIG);
        service.lookupService("dns:A:///potato.redbull.com?port=100").invokeWithPromise(promise);
        List<URI> result = promise.get();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testQueryAWithBadPrimary() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(URI.create("dns://0.0.0.0:53"), TEST_A_CONFIG);
        service.lookupService("dns:A:///ipsec1.rbss.staging.rbmhops.net?port=100").invokeWithPromise(promise);
        List<URI> result = promise.get();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.get(0).getHost().isEmpty());
    }

    @Test
    public void testQuerySRV() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_SRV_CONFIG);
        service.lookupService("dns:SRV:///consul.service.consul").invokeWithPromise(promise);
        List<URI> result = promise.get();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertFalse(result.get(0).getHost().isEmpty());
        Assert.assertEquals("/consul", result.get(0).getPath());
    }

    @Test
    public void testQuerySRVWithUnknownHost() throws Exception {
        Promise<List<URI>> promise = Promises.blockingPromise();
        DiscoveryService service = new DnsDiscoveryService(TEST_SRV_CONFIG);
        service.lookupService("dns:SRV:///bogus.consul").invokeWithPromise(promise);
        List<URI> result = promise.get();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

}