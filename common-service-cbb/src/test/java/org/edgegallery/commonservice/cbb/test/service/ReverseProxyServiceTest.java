/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.edgegallery.commonservice.cbb.test.service;

import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.edgegallery.commonservice.cbb.exception.CommonServiceCbbException;
import org.edgegallery.commonservice.cbb.mapper.ReverseProxyMapper;
import org.edgegallery.commonservice.cbb.model.ReverseProxy;
import org.edgegallery.commonservice.cbb.service.impl.ReverseProxyServiceImpl;
import org.edgegallery.commonservice.cbb.test.CommonServiceCbbTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonServiceCbbTests.class)
@AutoConfigureMockMvc
public class ReverseProxyServiceTest{
    @Autowired
    private ReverseProxyServiceImpl reverseProxyService;

    private ReverseProxyMapper mockMapper = new ReverseProxyMapper() {
        @Override
        public int createReverseProxy(ReverseProxy reverseProxy) {
            return -1;
        }

        @Override
        public int deleteReverseProxy(String destHostIp, int destHostPort) {
            return -1;
        }

        @Override
        public int modifyLinkNumber(ReverseProxy reverseProxy) {
            return -1;
        }

        @Override
        public ReverseProxy getReverseProxy(String destHostIp, int destHostPort) {
            return null;
        }

        @Override
        public Set<Integer> getAllLocalPorts() {
            return new HashSet<>();
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Class.forName("mockit.integration.junit4.JMockit");
        new MockUp<FileUtils>() {
            @Mock
            public void writeStringToFile(final File file, final String data, final Charset encoding)
                    throws IOException {

            }
        };

        DefaultExecutor executor = Mockito.mock(DefaultExecutor.class);
        Mockito.when(executor.execute(Mockito.any())).thenReturn(0);
        reverseProxyService.setExecutor(executor);

        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        String body = "{\"destHostIp\":\"192.168.1.101\",\"destHostPort\":6080,\"localPort\":30111," +
                "\"nextHopProtocol\":\"http\",\"nextHopIp\":\"192.168.1.2\",\"nextHopPort\":30111," +
                "\"linkNumber\":1,\"hopIndex\":1}";
        ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.OK);
        Mockito.when((restTemplate.exchange(Mockito.anyString(), Mockito.any(),
                new HttpEntity<>(Mockito.any(), Mockito.any()), String.class))).thenReturn(response);
        reverseProxyService.setRestTemplate(restTemplate);
    }

    @Test
    public void testAddReverseProxySuccess1() throws Exception {
        ReverseProxy reverseProxy = new ReverseProxy("https", "192.168.1.100", 6080, 0,
                "http", null, 0,0,1);
        ReverseProxy result = reverseProxyService.addReverseProxy(reverseProxy, "token");
        Assert.assertEquals(result.getLocalPort(), 30111);
        reverseProxy = new ReverseProxy("https", "192.168.1.102", 6080, 0,
                "http", null, 0,0,1);
        result = reverseProxyService.addReverseProxy(reverseProxy, "token");
        Assert.assertEquals(result.getLocalPort(), 30113);
        reverseProxy = new ReverseProxy("https", "192.168.1.103", 6080, 0,
                "http", "192.168.1.110", 6080,0,2);
        result = reverseProxyService.addReverseProxy(reverseProxy, "token");
        Assert.assertEquals(result.getLocalPort(), 30114);
        reverseProxyService.addReverseProxy(reverseProxy, "token");
        reverseProxyService.deleteReverseProxy("192.168.1.103", 6080, "token");

        reverseProxy = new ReverseProxy("https", "192.168.1.105", 6080, 0,
                "http", "192.168.1.110", 6080,0,1);
        try {
            reverseProxyService.addReverseProxy(reverseProxy, "token");
        }catch (Exception e) {
            Assert.assertEquals(e.getClass(), CommonServiceCbbException.class);
        }
    }

    @Test
    public void testAddReverseProxyFail1() throws Exception {
        reverseProxyService.setReverseProxyMapper(mockMapper);
        ReverseProxy reverseProxy = new ReverseProxy("https", "192.168.1.105", 6080, 0,
                "https", "192.168.1.110", 6080,0,1);
        try {
            reverseProxyService.addReverseProxy(reverseProxy, "token");
        }catch (Exception e) {
            Assert.assertEquals(e.getClass(), CommonServiceCbbException.class);
        }
    }

    @Test
    public void testAddReverseProxyFail2() throws Exception {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        String body = "{\"destHostIp\":\"192.168.1.101\",\"destHostPort\":6080,\"localPort\":30111," +
                "\"nextHopProtocol\":\"http\",\"nextHopIp\":\"192.168.1.2\",\"nextHopPort\":30111," +
                "\"linkNumber\":1,\"hopIndex\":1}";
        ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.OK);
        Mockito.when((restTemplate.exchange(Mockito.anyString(), Mockito.any(),
                new HttpEntity<>(Mockito.any(), Mockito.any()), String.class)))
                .thenThrow(new RestClientException("err"));
        reverseProxyService.setRestTemplate(restTemplate);
        ReverseProxy reverseProxy = new ReverseProxy("https", "192.168.1.105", 6080, 0,
                "https", "192.168.1.110", 6080,0,1);
        try {
            reverseProxyService.addReverseProxy(reverseProxy, "token");
        }catch (Exception e) {
            Assert.assertEquals(e.getClass(), CommonServiceCbbException.class);
        }
    }

    @Test
    public void testAddReverseProxyFail3() throws Exception {
        DefaultExecutor executor = Mockito.mock(DefaultExecutor.class);
        Mockito.when(executor.execute(Mockito.any())).thenThrow(new IOException("err"));
        reverseProxyService.setExecutor(executor);
        ReverseProxy reverseProxy = new ReverseProxy("https", "192.168.1.105", 6080, 0,
                "https", "", 6080,0,1);
        try {
            reverseProxyService.addReverseProxy(reverseProxy, "token");
        }catch (Exception e) {
            Assert.assertEquals(e.getClass(), CommonServiceCbbException.class);
        }
    }

    @Test
    public void testAddReverseProxyFail4() throws Exception {
        DefaultExecutor executor = Mockito.mock(DefaultExecutor.class);
        Mockito.when(executor.execute(Mockito.any())).thenReturn(-1);
        reverseProxyService.setExecutor(executor);
        ReverseProxy reverseProxy = new ReverseProxy("https", "192.168.1.105", 6080, 0,
                "https", "", 6080,0,1);
        try {
            reverseProxyService.addReverseProxy(reverseProxy, "token");
        }catch (Exception e) {
            Assert.assertEquals(e.getClass(), CommonServiceCbbException.class);
        }
    }

    @Test
    public void testAddReverseProxyFail5() throws Exception {
        new MockUp<FileUtils>() {
            @Mock
            public void writeStringToFile(final File file, final String data, final Charset encoding)
                    throws IOException {
                throw new IOException("err");
            }
        };
        ReverseProxy reverseProxy = new ReverseProxy("https", "192.168.1.105", 6080, 0,
                "https", "", 6080,0,1);
        try {
            reverseProxyService.addReverseProxy(reverseProxy, "token");
        }catch (Exception e) {
            Assert.assertEquals(e.getClass(), CommonServiceCbbException.class);
        }
    }

    @Test
    public void testDeleteReverseProxySuccess1() throws Exception {
        reverseProxyService.deleteReverseProxy("192.168.1.101", 6080, "token");
        reverseProxyService.deleteReverseProxy("192.168.1.101", 6080, "token");
        Assert.assertTrue(true);
    }

    @Test
    public void testDeleteReverseProxySuccess2() throws Exception {
        reverseProxyService.deleteReverseProxy("192.168.1.100", 6080, "token");
        reverseProxyService.deleteReverseProxy("192.168.1.100", 6080, "token");
        Assert.assertTrue(true);
    }

    @Test
    public void testGetReverseProxy() throws Exception {
        ReverseProxy result = reverseProxyService.getReverseProxy("192.168.1.103", 6080);
        Assert.assertEquals(result.getDestHostIp(), "192.168.1.103");
        result = reverseProxyService.getReverseProxy("192.168.1.200", 6080);
        Assert.assertNull(result);
    }
}
