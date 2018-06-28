package com.gclfax.common.constants;

/**
 * Created by chenmy on 2018/4/27.
 */
public enum PactFlagEnum {
    BID("原始协议","bid"),
    O2M_BID("转让协议","o2m_bid"),
    INVEST("投资合同","invest"),
    O2M_INVEST("转让投资合同","o2m_invest");

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

    private PactFlagEnum(String name, String code){
        this.name = name;
        this.code = code;
    }
}
