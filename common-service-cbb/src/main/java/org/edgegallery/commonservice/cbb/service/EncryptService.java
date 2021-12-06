package org.edgegallery.commonservice.cbb.service;

import org.edgegallery.commonservice.cbb.model.EncryptAndDecryptDto;

public interface EncryptService {

    EncryptAndDecryptDto encrypt(EncryptAndDecryptDto dto);

    EncryptAndDecryptDto decrypt(EncryptAndDecryptDto dto);
}
