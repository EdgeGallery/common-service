/*
 *    Copyright 2020 Huawei Technologies Co., Ltd.
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

package org.edgegallery.commonservice.cbb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.edgegallery.commonservice.cbb.service.ReverseProxyService;
import org.edgegallery.commonservice.cbb.util.Consts;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReverseProxy implements Cloneable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseProxyService.class);

    @NotBlank
    @Pattern(regexp = Consts.REGEX_PROTOCOL, message = "invalid dest host protocol")
    private String destHostProtocol;

    @NotBlank
    @Pattern(regexp = Consts.REGEX_IP, message = "invalid dest host ip")
    private String destHostIp;

    @Range(min = 1, max = Consts.MAX_OS_PORT)
    private int destHostPort;
    private int localPort;
    private String nextHopProtocol;
    private String nextHopIp;
    private int nextHopPort;
    private int linkNumber;
    private int hopIndex;

    @Override
    public ReverseProxy clone() {
        try {
            return (ReverseProxy) super.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.error("failed to clone reverse proxy", e);
            return new ReverseProxy(destHostProtocol, destHostIp, destHostPort, localPort, nextHopProtocol,
                    nextHopIp, nextHopPort, linkNumber, hopIndex);
        }
    }
}
