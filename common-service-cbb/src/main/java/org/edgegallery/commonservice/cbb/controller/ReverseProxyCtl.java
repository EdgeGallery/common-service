/*
 *    Copyright 2021 Huawei Technologies Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.edgegallery.commonservice.cbb.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.commonservice.cbb.model.ReverseProxy;
import org.edgegallery.commonservice.cbb.model.restful.ErrorRespDto;
import org.edgegallery.commonservice.cbb.model.restful.OperationInfoRep;
import org.edgegallery.commonservice.cbb.service.ReverseProxyService;
import org.edgegallery.commonservice.cbb.util.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Controller
@RestSchema(schemaId = "ReverseProxies")
@RequestMapping(Consts.RP_BASE_URL)
@Api(tags = "ReverseProxies")
@Validated
public class ReverseProxyCtl {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseProxyCtl.class);

    @Autowired
    private ReverseProxyService reverseProxyService;

    /**
     * add reverse proxy
     */
    @ApiOperation(value = "add reverse proxy.", response = OperationInfoRep.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = OperationInfoRep.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorRespDto.class)
    })
    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('DEVELOPER_TENANT') || hasRole('DEVELOPER_ADMIN')")
    public ResponseEntity<Object> addReverseProxy(
            @NotNull @ApiParam(value = "ReverseProxy", required = true)
            @RequestBody ReverseProxy reverseProxy, HttpServletRequest request) {
        String ip = reverseProxy.getDestHostIp();
        if (ip == null || !ip.matches(Consts.REGEX_IP)) {
            LOGGER.error("invalid ip address : {}", ip);
            return new ResponseEntity<>(new ErrorRespDto(1, "invalid ip address", ip),
                    HttpStatus.BAD_REQUEST);
        }

        int port = reverseProxy.getDestHostPort();
        if (port < 0 || port > Consts.MAX_OS_PORT) {
            LOGGER.error("invalid port : {}", port);
            return new ResponseEntity<>(new ErrorRespDto(1, "invalid port", String.valueOf(port)),
                    HttpStatus.BAD_REQUEST);
        }
        String accessToken = request.getHeader(Consts.ACCESS_TOKEN_STR);
        return ResponseEntity.ok(reverseProxyService.addReverseProxy(reverseProxy, accessToken));
    }

    /**
     * delete reverse proxy
     */
    @ApiOperation(value = "delete reverse proxy.", response = OperationInfoRep.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = OperationInfoRep.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorRespDto.class)
    })
    @RequestMapping(value = "/dest-host-ip/{destHostIp}/dest-host-port/{destHostPort}", method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('DEVELOPER_TENANT') || hasRole('DEVELOPER_ADMIN')")
    public ResponseEntity<Object> deleteReverseProxy(
        @Pattern(regexp = Consts.REGEX_IP, message = "invalid ip address")
        @ApiParam(value = "destHostIp", required = true) @PathVariable("destHostIp") String destHostIp,
        @Max(value = Consts.MAX_OS_PORT, message = "invalid port") @Min(value = 0, message = "invalid port")
        @ApiParam(value = "destHostPort", required = true) @PathVariable("destHostPort") int destHostPort,
        HttpServletRequest request) {

        String accessToken = request.getHeader(Consts.ACCESS_TOKEN_STR);
        reverseProxyService.deleteReverseProxy(destHostIp, destHostPort, accessToken);
        return ResponseEntity.ok("");
    }

    /**
     * get vnc console url
     */
    @ApiOperation(value = "get vnc console url.", response = OperationInfoRep.class)
    @ApiResponses(value = {

            @ApiResponse(code = 200, message = "OK", response = OperationInfoRep.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ErrorRespDto.class)
    })
    @RequestMapping(value = "/dest-host-ip/{destHostIp}/dest-host-port/{destHostPort}",
            method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('DEVELOPER_TENANT') || hasRole('DEVELOPER_ADMIN')")
    public ResponseEntity<Object> getReverseProxy(
            @Pattern(regexp = Consts.REGEX_IP, message = "invalid ip address")
            @ApiParam(value = "destHostIp", required = true) @PathVariable("destHostIp") String destHostIp,
            @Max(value = Consts.MAX_OS_PORT, message = "invalid port") @Min(value = 0, message = "invalid port")
            @ApiParam(value = "destHostPort", required = true) @PathVariable("destHostPort") int destHostPort) {
        return ResponseEntity.ok(reverseProxyService.getReverseProxy(destHostIp, destHostPort));
    }
}
