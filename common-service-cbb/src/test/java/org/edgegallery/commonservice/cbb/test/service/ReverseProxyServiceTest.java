package org.edgegallery.commonservice.cbb.test.service;

import com.google.gson.Gson;
import org.apache.commons.exec.DefaultExecutor;
import org.edgegallery.commonservice.cbb.model.ReverseProxy;
import org.edgegallery.commonservice.cbb.service.ReverseProxyService;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonServiceCbbTests.class)
@AutoConfigureMockMvc
public class ReverseProxyServiceTest {
    @Autowired
    private ReverseProxyServiceImpl reverseProxyService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddReverseProxySuccess1() throws Exception {
        DefaultExecutor executor = Mockito.mock(DefaultExecutor.class);
        Mockito.when(executor.execute(Mockito.any())).thenReturn(0);
        ReverseProxy reverseProxy = new ReverseProxy("192.168.1.100", 6080, 0,
                "http", null, 0,0,1);
        reverseProxyService.setExecutor(executor);
        reverseProxyService.addReverseProxy(reverseProxy, Mockito.anyString());
    }

    @Test
    public void testAddReverseProxySuccess2() throws Exception {
        DefaultExecutor executor = Mockito.mock(DefaultExecutor.class);
        Mockito.when(executor.execute(Mockito.any())).thenReturn(0);
        ReverseProxy reverseProxy = new ReverseProxy("192.168.1.100", 6080, 0,
                "http", null, 0,0,1);
        reverseProxyService.setExecutor(executor);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when((restTemplate.exchange(Mockito.anyString(), Mockito.any(),
                new HttpEntity<>(Mockito.any(), Mockito.any()), String.class))).thenReturn(response);
        reverseProxyService.setRestTemplate(restTemplate);
        reverseProxyService.addReverseProxy(reverseProxy, Mockito.anyString());
    }
}
