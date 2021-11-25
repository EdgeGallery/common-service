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

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReverseProxy implements Cloneable{
    private String destHostIp;
    private int destHostPort;
    private int localPort;
    private String nextHopProtocol;
    private String nextHopIp;
    private int nextHopPort;
    private int linkNumber;
    private int hopIndex;

    public ReverseProxy clone() {
        return new ReverseProxy(destHostIp, destHostPort, localPort,
                nextHopProtocol, nextHopIp, nextHopPort, linkNumber, hopIndex);
    }
}