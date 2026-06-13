package cn.bugstack.ai.domain.llm.model.valobj;

public enum McpTypeEnumVO {

    SSE("sse"),
    STREAMABLE("streamable");

    private final String code;

    McpTypeEnumVO(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    @Override
    public String toString(){
        return code;
    }
}
