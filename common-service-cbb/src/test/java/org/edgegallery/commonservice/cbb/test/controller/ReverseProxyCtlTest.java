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

package org.edgegallery.commonservice.cbb.test.controller;

import com.google.gson.Gson;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonServiceCbbTests.class)
@AutoConfigureMockMvc
public class ReverseProxyCtlTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ReverseProxyService reverseProxyService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(roles = "DEVELOPER_ADMIN")
    public void testAddReverseProxySuccess() throws Exception {
        String url = "/commonservice/cbb/v1/reverseproxies";
        ReverseProxy reverseProxy = new ReverseProxy("192.168.1.1", 6080, 0,
                "http", null, 0,0,1);
        Mockito.when(reverseProxyService
                .addReverseProxy(Mockito.any(), Mockito.anyString()))
                .thenReturn(reverseProxy);
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.post(url)
                .with(csrf()).content(new Gson().toJson(reverseProxy))
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(200, actions.andReturn().getResponse().getStatus());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER_ADMIN")
    public void testAddReverseProxyInvalidIp() throws Exception {
        String url = "/commonservice/cbb/v1/reverseproxies";
        ReverseProxy reverseProxy = new ReverseProxy("192.168.1.1000", 6080, 0,
                "http", null, 0,0,1);
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.post(url)
                .with(csrf()).content(new Gson().toJson(reverseProxy))
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().is4xxClientError());
        Assert.assertEquals(400, actions.andReturn().getResponse().getStatus());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER_ADMIN")
    public void testAddReverseProxyInvalidPort() throws Exception {
        String url = "/commonservice/cbb/v1/reverseproxies";
        ReverseProxy reverseProxy = new ReverseProxy("192.168.1.1", 70800, 0,
                "http", null, 0,0,1);
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.post(url)
                .with(csrf()).content(new Gson().toJson(reverseProxy))
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().is4xxClientError());
        Assert.assertEquals(400, actions.andReturn().getResponse().getStatus());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER_ADMIN")
    public void testDeleteReverseProxySuccess() throws Exception {
        String url = "/commonservice/cbb/v1/reverseproxies/dest-host-ip/192.168.1.1/dest-host-port/6080";
        Mockito.mock(reverseProxyService.getClass());
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.delete(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(200, actions.andReturn().getResponse().getStatus());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER_ADMIN")
    public void testGetReverseProxySuccess() throws Exception {
        String url = "/commonservice/cbb/v1/reverseproxies/dest-host-ip/192.168.1.1/dest-host-port/6080";
        Mockito.mock(reverseProxyService.getClass());
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.get(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertEquals(200, actions.andReturn().getResponse().getStatus());
    }
}
