package com.shaktipumplimted.smsreader;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerificationCodeModel {
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message-Id")
    @Expose
    private String messageId;
    @SerializedName("Description")
    @Expose
    private String description;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
