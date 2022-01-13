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

package org.edgegallery.commonservice.cbb.service.impl;

import com.google.gson.Gson;
import lombok.Setter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.edgegallery.commonservice.cbb.exception.CommonServiceCbbException;
import org.edgegallery.commonservice.cbb.mapper.ReverseProxyMapper;
import org.edgegallery.commonservice.cbb.model.ReverseProxy;
import org.edgegallery.commonservice.cbb.service.ReverseProxyService;
import org.edgegallery.commonservice.cbb.util.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

@Service
@Setter
public class ReverseProxyServiceImpl implements ReverseProxyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseProxyService.class);
    private static final String NGINX_HTTP_CONFIG_FILE = "/reverse_proxy/nginx_http.conf";
    private static final String NGINX_HTTPS_CONFIG_FILE = "/reverse_proxy/nginx_https.conf";
    private static final String NGINX_CONFIG_FILE_SUFFIX = ".conf";
    private static final String NGINX_CONFIG_DIR = "/etc/nginx/conf.d/";
    private static final int HEAD_HOP_INDEX = 1;
    private static final int MIDDLE_HOP_INDEX = 2;

    @Autowired
    private ReverseProxyMapper reverseProxyMapper;

    private static Gson gson = new Gson();

    @Value("${proxy.server.port.min:}")
    private int minReverseProxyPort;

    @Value("${proxy.server.port.max:}")
    private int maxReverseProxyPort;

    @Value("${proxy.next-hop.port:}")
    private int nextHopPort;

    private RestTemplate restTemplate = new RestTemplate();

    private Executor executor = new DefaultExecutor();

    /**
     * add reverse proxy
     * @param reverseProxy
     */
    @Override
    public ReverseProxy addReverseProxy(ReverseProxy reverseProxy, String token) {
        ReverseProxy dbProxy = reverseProxyMapper.getReverseProxy(reverseProxy.getDestHostIp(),
                reverseProxy.getDestHostPort());
        if (dbProxy != null) {
            if (dbProxy.getHopIndex() == 1) {
                LOGGER.info("reverse proxy already exist, dest ip : {}, dest port : {}",
                        dbProxy.getDestHostIp(), dbProxy.getDestHostPort());
                return dbProxy;
            }
            int linkNumber = dbProxy.getLinkNumber() + 1;
            dbProxy.setLinkNumber(linkNumber);
            reverseProxyMapper.modifyLinkNumber(dbProxy);
            LOGGER.info("another reverse proxy link is added, dest ip: {}, dest port: {}, link number: {}",
                    dbProxy.getDestHostIp(), dbProxy.getDestHostPort(), linkNumber);
            return dbProxy;
        }
        int proxyHostPort = getUsableProxyPort();

        String nextHopIp = reverseProxy.getNextHopIp();
        if (StringUtils.isNotBlank(nextHopIp)) {
            String url = new StringBuffer(reverseProxy.getNextHopProtocol()).append("://")
                    .append(reverseProxy.getNextHopIp()).append(":")
                    .append(nextHopPort).append(Consts.RP_BASE_URL).toString();
            ReverseProxy reqBody = reverseProxy.clone();
            reqBody.setHopIndex(MIDDLE_HOP_INDEX);
            reqBody.setNextHopIp(null);
            reqBody.setLinkNumber(1);
            String resp = sendHttpRequest(url, token, HttpMethod.POST, reqBody);
            ReverseProxy hopReverseProxy = gson.fromJson(resp, ReverseProxy.class);
            reverseProxy.setNextHopPort(hopReverseProxy.getLocalPort());
            addReverseProxyConfigFile(reverseProxy.getDestHostProtocol(), proxyHostPort,
                    reverseProxy.getNextHopIp(), reverseProxy.getNextHopPort());
        } else {
            addReverseProxyConfigFile(reverseProxy.getDestHostProtocol(), proxyHostPort,
                    reverseProxy.getDestHostIp(), reverseProxy.getDestHostPort());
        }
        reloadNginxConfig();
        reverseProxy.setLocalPort(proxyHostPort);
        reverseProxy.setLinkNumber(1);

        // if insert data failed, nginx config should be roll back.
        if (reverseProxyMapper.createReverseProxy(reverseProxy) != 1) {
            LOGGER.error("failed to insert reverse proxy data, nginx config will be roll back! proxy data : {}",
                    reverseProxy.toString());
            deleteReverseProxyConfigFile(proxyHostPort);
            reloadNginxConfig();
            throw new CommonServiceCbbException("failed to insert reverse proxy data");
        }
        return reverseProxy;
    }

    @Override
    public void deleteReverseProxy(String destHostIp, int destHostPort, String token) {
        ReverseProxy reverseProxy = reverseProxyMapper.getReverseProxy(destHostIp, destHostPort);
        if (reverseProxy == null) {
            LOGGER.info("the proxy was already deleted, destHostIp : {}, destHostPort : {}", destHostIp, destHostPort);
            return;
        }

        String nextHopIp = reverseProxy.getNextHopIp();
        if (reverseProxy.getHopIndex() == HEAD_HOP_INDEX && StringUtils.isNotBlank(nextHopIp)) {
            String url = new StringBuffer(reverseProxy.getNextHopProtocol()).append("://")
                    .append(nextHopIp).append(":").append(nextHopPort).append(Consts.RP_BASE_URL)
                    .append("/dest-host-ip/").append(reverseProxy.getDestHostIp()).append("/dest-host-port/")
                    .append(reverseProxy.getDestHostPort()).toString();
            sendHttpRequest(url, token, HttpMethod.DELETE, null);
        } else {
            int linkNumber = reverseProxy.getLinkNumber();
            if (linkNumber > 1) {
                reverseProxy.setLinkNumber(linkNumber - 1);
                reverseProxyMapper.modifyLinkNumber(reverseProxy);
                LOGGER.info("{} clients connect this middle reverse proxy, just update the link number", linkNumber);
                return;
            }
        }

        reverseProxyMapper.deleteReverseProxy(destHostIp, destHostPort);
        deleteReverseProxyConfigFile(reverseProxy.getLocalPort());
        reloadNginxConfig();
    }

    @Override
    public ReverseProxy getReverseProxy(String destHostIp, int destHostPort) {
        return reverseProxyMapper.getReverseProxy(destHostIp, destHostPort);
    }

    private String sendHttpRequest(String url, String token, HttpMethod method, ReverseProxy body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Consts.ACCESS_TOKEN_STR, token);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, method, new HttpEntity<>(body, headers), String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (RestClientException e) {
            LOGGER.error("Failed to send http request", e);
        }
        LOGGER.error("Failed to send http request, url is : {}, method is : {}", url, method);
        throw new CommonServiceCbbException("Failed to send http request");
    }

    private void reloadNginxConfig() {
         String cmd = "nginx -s reload";
         CommandLine cmdLine = CommandLine.parse(cmd);
         executor.setExitValues(null);
         ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
         executor.setWatchdog(watchdog);

        try {
            int retCode = executor.execute(cmdLine);
            if (retCode != 0) {
                LOGGER.error("failed to reload nginx config, return code is : {}", retCode);
                throw new CommonServiceCbbException("failed to reload nginx config");
            }
        } catch (IOException e) {
            LOGGER.error("failed to reload nginx config.", e);
            throw new CommonServiceCbbException("failed to reload nginx config", e);
        }
    }

    private int getUsableProxyPort() {
        Set<Integer> usedPorts = reverseProxyMapper.getAllLocalPorts();
        for (int i = minReverseProxyPort; i <= maxReverseProxyPort; i++) {
            if (!usedPorts.contains(i)) {
                LOGGER.debug("proxy port {} is usable.", i);
                return i;
            }
        }
        LOGGER.error("no usable proxy port left.");
        throw new CommonServiceCbbException("there is no usable proxy port left.");
    }

    private void addReverseProxyConfigFile(String protocol, int proxyPort, String hostIp, int hostConsolePort){
        try {
            InputStream is = ReverseProxyServiceImpl.class.getResourceAsStream(
                    Consts.HTTPS_PROTOCOL.equals(protocol) ? NGINX_HTTPS_CONFIG_FILE : NGINX_HTTP_CONFIG_FILE);
            Charset charset = Charset.forName(Consts.DEFAULT_ENCODING);
            String content = StreamUtils.copyToString(is, charset);
            String url = new StringBuffer(protocol).append("://").append(hostIp)
                    .append(":").append(hostConsolePort).toString();
            String nginxConfig = String.format(content, proxyPort, url);
            File file = new File(NGINX_CONFIG_DIR + proxyPort + NGINX_CONFIG_FILE_SUFFIX);
            FileUtils.writeStringToFile(file, nginxConfig, charset);
            LOGGER.info("reverse proxy config file {} is created.", file.getName());
        } catch(IOException e) {
            LOGGER.error("failed to make reverse proxy conf", e);
            throw new CommonServiceCbbException("failed to make reverse proxy conf.");
        }
    }

    private void deleteReverseProxyConfigFile(int proxyPort) {
        File file = new File(NGINX_CONFIG_DIR + proxyPort + NGINX_CONFIG_FILE_SUFFIX);
        if (file.exists() && !file.delete()) {
            LOGGER.error("failed to delete reverse config file {}.", file.getName());
            throw new CommonServiceCbbException("failed to delete config file.");
        }
        LOGGER.info("reverse proxy file {} is deleted.", file.getName());
    }
}
