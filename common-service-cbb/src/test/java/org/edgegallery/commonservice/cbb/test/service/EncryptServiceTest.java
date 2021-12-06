package org.edgegallery.commonservice.cbb.test.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.edgegallery.commonservice.cbb.exception.CommonServiceCbException;
import org.edgegallery.commonservice.cbb.model.EncryptAndDecryptDto;
import org.edgegallery.commonservice.cbb.service.EncryptService;
import org.edgegallery.commonservice.cbb.service.impl.EncryptServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptServiceTest {

    private EncryptService encryptService;

    @Before
    public void before() {
        encryptService = new EncryptServiceImpl();
    }

    @Test
    public void should_success_when_encrypt_text() {
        EncryptAndDecryptDto dto = new EncryptAndDecryptDto();
        EncryptAndDecryptDto.Data data = new EncryptAndDecryptDto.Data();
        dto.setData(new ArrayList<>());
        dto.getData().add(data);
        data.setPlainText("test123");
        dto.setAssociatedData("assData");
        dto = encryptService.encrypt(dto);
        Assert.assertNotNull(dto.getData().get(0).getCipherText());
    }

    @Test
    public void should_success_when_decrypt_text() {
        EncryptAndDecryptDto dto = new EncryptAndDecryptDto();
        EncryptAndDecryptDto.Data data = new EncryptAndDecryptDto.Data();
        dto.setData(new ArrayList<>());
        dto.getData().add(data);
        dto.setAssociatedData("assData");

        data.setPlainText("test123");
        dto = encryptService.encrypt(dto);
        Assert.assertNotNull(dto.getData().get(0).getCipherText());
        dto.getData().get(0).setPlainText(null);
        dto = encryptService.decrypt(dto);
        Assert.assertEquals("test123", dto.getData().get(0).getPlainText());
    }

    @Test(expected = CommonServiceCbException.class)
    public void should_failed_when_decrypt_with_different_associatedData() {
        EncryptAndDecryptDto dto = new EncryptAndDecryptDto();
        EncryptAndDecryptDto.Data data = new EncryptAndDecryptDto.Data();
        dto.setData(new ArrayList<>());
        dto.getData().add(data);
        dto.setAssociatedData("assData1");

        dto.getData().get(0).setPlainText("test123");
        // dto.setPlainText("test123");
        dto = encryptService.encrypt(dto);
        Assert.assertNotNull(dto.getData().get(0).getCipherText());
        dto.getData().get(0).setPlainText(null);
        dto.setAssociatedData("assData2");
        dto = encryptService.decrypt(dto);
        Assert.fail();
    }

    @Test
    public void should_success_when_decrypt_with_empty_associatedData() {
        EncryptAndDecryptDto dto = new EncryptAndDecryptDto();
        EncryptAndDecryptDto.Data data = new EncryptAndDecryptDto.Data();
        dto.setData(new ArrayList<>());
        dto.getData().add(data);
        dto.setAssociatedData("assData1");

        dto.getData().get(0).setPlainText("test123");
        dto = encryptService.encrypt(dto);
        Assert.assertNotNull(dto.getData().get(0).getCipherText());
        dto.getData().get(0).setPlainText(null);
        dto = encryptService.decrypt(dto);
        Assert.assertEquals("test123", dto.getData().get(0).getPlainText());
    }
}
