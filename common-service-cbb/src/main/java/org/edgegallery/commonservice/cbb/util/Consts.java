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

package org.edgegallery.commonservice.cbb.util;

public class Consts {
    public static final String ACCESS_TOKEN_STR = "access_token";

    public static final String DEFAULT_ENCODING = "utf-8";

    public static final int MAX_OS_PORT = 65535;

    public static final String REGEX_IP = "([1-9]\\d?|1\\d{2}|2[0-4]\\d|25[0-5])(\\.([1-9]\\d?|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

    public static final String REGEX_UUID = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    public static final String RP_BASE_URL = "/commonservice/cbb/v1/reverseproxies";
}
