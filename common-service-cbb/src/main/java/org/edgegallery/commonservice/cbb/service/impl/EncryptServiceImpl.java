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

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.subtle.AesGcmJce;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;
import org.edgegallery.commonservice.cbb.exception.CommonServiceCbbException;
import org.edgegallery.commonservice.cbb.model.EncryptAndDecryptDto;
import org.edgegallery.commonservice.cbb.service.EncryptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EncryptServiceImpl implements EncryptService {

    @Value("${encrypt.key-component}")
    private String inputComponent;

    @Override
    public EncryptAndDecryptDto encrypt(EncryptAndDecryptDto dto) {
        try {
            KeysetHandle keysetHandle = getKeySet();
            Aead aead = keysetHandle.getPrimitive(Aead.class);
            byte[] associatedData = dto.getAssociatedData() == null
                ? "EdgeGallery".getBytes()
                : dto.getAssociatedData().getBytes();
            for (EncryptAndDecryptDto.Data data : dto.getData()) {
                byte[] ciphertext = aead.encrypt(data.getPlainText().getBytes(), associatedData);
                String encrypted = Base64.getEncoder().encodeToString(ciphertext);
                data.setCipherText(encrypted);
            }
            return dto;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new CommonServiceCbbException(e.getMessage());
        }
    }

    @Override
    public EncryptAndDecryptDto decrypt(EncryptAndDecryptDto dto) {
        try {
            KeysetHandle keysetHandle = getKeySet();
            Aead aead = keysetHandle.getPrimitive(Aead.class);
            byte[] associatedData = dto.getAssociatedData() == null
                ? "EdgeGallery".getBytes()
                : dto.getAssociatedData().getBytes();
            for (EncryptAndDecryptDto.Data data : dto.getData()) {
                byte[] decrypted = aead.decrypt(Base64.getDecoder().decode(data.getCipherText()), associatedData);
                data.setPlainText(new String(decrypted));
            }
            return dto;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new CommonServiceCbbException(e.getMessage());
        }
    }

    private KeysetHandle getKeySet() throws GeneralSecurityException, IOException {
        AeadConfig.register();
        File keySetFile = new File("eg_keyset.json");
        KeysetHandle keysetHandle = null;

        String component3 = FileUtils.readFileToString(Resources.getResourceAsFile("component.txt"), "UTF-8");

        String[] components = {inputComponent, this.getClass().toString(), component3};
        String com = StringUtils.join(components, "");
        DigestUtils.md5Digest(com.getBytes(StandardCharsets.UTF_8));

        if (!keySetFile.exists()) {
            keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES128_GCM"));
            keysetHandle.write(JsonKeysetWriter.withFile(keySetFile),
                new AesGcmJce(DigestUtils.md5Digest(com.getBytes(StandardCharsets.UTF_8))));
        } else {
            keysetHandle = KeysetHandle.read(JsonKeysetReader.withFile(keySetFile),
                new AesGcmJce(DigestUtils.md5Digest(com.getBytes(StandardCharsets.UTF_8))));
        }
        return keysetHandle;
    }
}
