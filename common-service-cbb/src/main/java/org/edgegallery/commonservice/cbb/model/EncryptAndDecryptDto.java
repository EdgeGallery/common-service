package org.edgegallery.commonservice.cbb.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncryptAndDecryptDto {

    // Unnecessary, default is 'EdgeGallery', use this data to encrypt and decrypt data.
    private String associatedData;

    private List<Data> data;

    // // 待加密数据 or 原始数据
    // private String plainText;
    //
    // // 加密后数据
    // private String cipherText;

    @Setter
    @Getter
    public static class Data {
        // 待加密数据 or 原始数据
        private String plainText;

        // 加密后数据
        private String cipherText;
    }
}


