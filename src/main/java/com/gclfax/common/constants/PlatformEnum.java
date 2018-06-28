package com.gclfax.common.constants;

public enum PlatformEnum {
    GXS_CG("国鑫所存管平台","gxs_cg"),
    GXS_HF("国鑫所汇付平台","gxs_hf");

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private PlatformEnum(String name, String code){
        this.name = name;
        this.code = code;
    }
}
