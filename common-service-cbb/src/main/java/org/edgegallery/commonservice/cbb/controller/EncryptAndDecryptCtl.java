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

package org.edgegallery.commonservice.cbb.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.edgegallery.commonservice.cbb.model.EncryptAndDecryptDto;
import org.edgegallery.commonservice.cbb.model.restful.ErrorRespDto;
import org.edgegallery.commonservice.cbb.service.EncryptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotNull;

@Controller
@RestSchema(schemaId = "crypto")
@RequestMapping("/commonservice/cbb/v1/crypto")
@Api(tags = "crypto")
@Validated
public class EncryptAndDecryptCtl {

    @Autowired
    private EncryptService encryptService;

    /**
     * encrypt.
     */
    @ApiOperation(value = "encrypt.", response = EncryptAndDecryptDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = EncryptAndDecryptDto.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorRespDto.class)
    })
    @RequestMapping(value = "/encrypt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EncryptAndDecryptDto> encrypt(
        @NotNull @ApiParam(value = "EncryptAndDecryptDto", required = true) @RequestBody
            EncryptAndDecryptDto encryptAndDecryptDto) {
        return ResponseEntity.ok(encryptService.encrypt(encryptAndDecryptDto));
    }

    /**
     * decrypt.
     */
    @ApiOperation(value = "decrypt.", response = EncryptAndDecryptDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = EncryptAndDecryptDto.class),
        @ApiResponse(code = 400, message = "Bad Request", response = ErrorRespDto.class)
    })
    @RequestMapping(value = "/decrypt", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EncryptAndDecryptDto> decrypt(
        @NotNull @ApiParam(value = "EncryptAndDecryptDto", required = true) @RequestBody
            EncryptAndDecryptDto encryptAndDecryptDto) {
        return ResponseEntity.ok(encryptService.decrypt(encryptAndDecryptDto));
    }
}
