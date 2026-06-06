package cn.bugstack.ai.domain.session.model.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP 架构值对象
 * <p>
 * public：访问修饰符，表示该接口和 record 是公开的，任何地方都可以访问。
 * sealed：限制继承，实现密封接口的类必须在 permits 中声明。
 * interface：定义接口。
 * record：定义不可变数据载体类。
 * implements：表示实现接口。
 * <p>
 * Jackson 注解用于控制 JSON 序列化和反序列化行为。
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/20 09:07
 */
@Slf4j
public final class McpSchemaVO {

    public static final String LATEST_PROTOCOL_VERSION = "2024-11-05";

    public static final String JSONRPC_VERSION = "2.0";

    private static final TypeReference<HashMap<String, Object>> MAP_TYPE_REF = new TypeReference<>() {
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JSONRPCMessage deserializeJsonRpcMessage(String jsonText)
            throws IOException {

        log.debug("Received JSON message: {}", jsonText);

        var map = objectMapper.readValue(jsonText, MAP_TYPE_REF);

        if (map.containsKey("method") && map.containsKey("id")) {
            return objectMapper.convertValue(map, JSONRPCRequest.class);
        } else if (map.containsKey("method") && !map.containsKey("id")) {
            return objectMapper.convertValue(map, JSONRPCNotification.class);
        } else if (map.containsKey("result") || map.containsKey("error")) {
            return objectMapper.convertValue(map, JSONRPCResponse.class);
        }

        throw new IllegalArgumentException("Cannot deserialize JSONRPCMessage: " + jsonText);
    }

    public static  <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return objectMapper.convertValue(data, typeRef);
    }

    /**
     * JSON-RPC 2.0 Message Types
     */
    public sealed interface JSONRPCMessage permits JSONRPCRequest, JSONRPCNotification, JSONRPCResponse {

        String jsonrpc();

    }

    /**
     * 请求对象
     *
     * @param jsonrpc 协议版本 2.0
     * @param method  请求方法；initialize、tools/list、tools/call、resources/list
     * @param id      请求ID
     * @param params  请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCRequest(@JsonProperty("jsonrpc") String jsonrpc,
                                 @JsonProperty("method") String method,
                                 @JsonProperty("id") Object id,
                                 @JsonProperty("params") Object params
    ) implements JSONRPCMessage {
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCNotification(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("method") String method,
            @JsonProperty("params") Object params) implements JSONRPCMessage {
    }

    /**
     * 响应对象
     *
     * @param jsonrpc 协议版本 2.0
     * @param id      请求ID
     * @param result  响应结果
     * @param error   异常结果
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCResponse(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("id") Object id,
            @JsonProperty("result") Object result,
            @JsonProperty("error") JSONRPCError error
    ) implements JSONRPCMessage {
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record JSONRPCError(
                @JsonProperty("code") int code,
                @JsonProperty("message") String message,
                @JsonProperty("data") Object data) {
        }
    }

    public sealed interface Request
            permits InitializeRequest, CallToolRequest {

    }

    // ---------------------------
    // Initialization
    // ---------------------------
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InitializeRequest( // @formatter:off
                                     @JsonProperty("protocolVersion") String protocolVersion,
                                     @JsonProperty("capabilities") ClientCapabilities capabilities,
                                     @JsonProperty("clientInfo") Implementation clientInfo) implements Request {
    } // @formatter:on

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InitializeResult( // @formatter:off
                                    @JsonProperty("protocolVersion") String protocolVersion,
                                    @JsonProperty("capabilities") ServerCapabilities capabilities,
                                    @JsonProperty("serverInfo") Implementation serverInfo,
                                    @JsonProperty("instructions") String instructions) {
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClientCapabilities( // @formatter:off
                                      @JsonProperty("experimental") Map<String, Object> experimental,
                                      @JsonProperty("roots") RootCapabilities roots,
                                      @JsonProperty("sampling") Sampling sampling) {

        /**
         * Roots define the boundaries of where servers can operate within the filesystem,
         * allowing them to understand which directories and files they have access to.
         * Servers can request the list of roots from supporting clients and
         * receive notifications when that list changes.
         *
         * @param listChanged Whether the client would send notification about roots
         * 		  has changed since the last time the server checked.
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RootCapabilities(
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        /**
         * Provides a standardized way for servers to request LLM
         * sampling ("completions" or "generations") from language
         * models via clients. This flow allows clients to maintain
         * control over model access, selection, and permissions
         * while enabling servers to leverage AI capabilities—with
         * no server API keys necessary. Servers can request text or
         * image-based interactions and optionally include context
         * from MCP servers in their prompts.
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record Sampling() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Map<String, Object> experimental;
            private RootCapabilities roots;
            private Sampling sampling;

            public Builder experimental(Map<String, Object> experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder roots(Boolean listChanged) {
                this.roots = new RootCapabilities(listChanged);
                return this;
            }

            public Builder sampling() {
                this.sampling = new Sampling();
                return this;
            }

            public ClientCapabilities build() {
                return new ClientCapabilities(experimental, roots, sampling);
            }
        }
    }// @formatter:on

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServerCapabilities( // @formatter:off
                                      @JsonProperty("completions") CompletionCapabilities completions,
                                      @JsonProperty("experimental") Map<String, Object> experimental,
                                      @JsonProperty("logging") LoggingCapabilities logging,
                                      @JsonProperty("prompts") PromptCapabilities prompts,
                                      @JsonProperty("resources") ResourceCapabilities resources,
                                      @JsonProperty("tools") ToolCapabilities tools) {

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record CompletionCapabilities() {
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record LoggingCapabilities() {
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record PromptCapabilities(
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record ResourceCapabilities(
                @JsonProperty("subscribe") Boolean subscribe,
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record ToolCapabilities(
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private CompletionCapabilities completions;
            private Map<String, Object> experimental;
            private LoggingCapabilities logging = new LoggingCapabilities();
            private PromptCapabilities prompts;
            private ResourceCapabilities resources;
            private ToolCapabilities tools;

            public Builder completions() {
                this.completions = new CompletionCapabilities();
                return this;
            }

            public Builder experimental(Map<String, Object> experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder logging() {
                this.logging = new LoggingCapabilities();
                return this;
            }

            public Builder prompts(Boolean listChanged) {
                this.prompts = new PromptCapabilities(listChanged);
                return this;
            }

            public Builder resources(Boolean subscribe, Boolean listChanged) {
                this.resources = new ResourceCapabilities(subscribe, listChanged);
                return this;
            }

            public Builder tools(Boolean listChanged) {
                this.tools = new ToolCapabilities(listChanged);
                return this;
            }

            public ServerCapabilities build() {
                return new ServerCapabilities(completions, experimental, logging, prompts, resources, tools);
            }
        }
    } // @formatter:on

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Implementation(// @formatter:off
                                 @JsonProperty("name") String name,
                                 @JsonProperty("version") String version) {
    } // @formatter:on

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListToolsResult( // @formatter:off
                                   @JsonProperty("tools") List<Tool> tools,
                                   @JsonProperty("nextCursor") String nextCursor) {
    }// @formatter:on

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tool( // @formatter:off
                        @JsonProperty("name") String name,
                        @JsonProperty("description") String description,
                        @JsonProperty("inputSchema") JsonSchema inputSchema) {

        public Tool(String name, String description, String schema) {
            this(name, description, parseSchema(schema));
        }

    } // @formatter:on


    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JsonSchema( // @formatter:off
                              @JsonProperty("type") String type,
                              @JsonProperty("properties") Map<String, Object> properties,
                              @JsonProperty("required") List<String> required,
                              @JsonProperty("additionalProperties") Boolean additionalProperties,
                              @JsonProperty("$defs") Map<String, Object> defs,
                              @JsonProperty("definitions") Map<String, Object> definitions) {
    } // @formatter:on

    private static JsonSchema parseSchema(String schema) {
        try {
            return objectMapper.readValue(schema, JsonSchema.class);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid schema: " + schema, e);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CallToolRequest(// @formatter:off
                                  @JsonProperty("name") String name,
                                  @JsonProperty("arguments") Map<String, Object> arguments) implements Request {

        public CallToolRequest(String name, String jsonArguments) {
            this(name, parseJsonArguments(jsonArguments));
        }

        private static Map<String, Object> parseJsonArguments(String jsonArguments) {
            try {
                return objectMapper.readValue(jsonArguments, MAP_TYPE_REF);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Invalid arguments: " + jsonArguments, e);
            }
        }
    }// @formatter:off

}
