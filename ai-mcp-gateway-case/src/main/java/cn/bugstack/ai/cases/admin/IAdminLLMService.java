package cn.bugstack.ai.cases.admin;

import cn.bugstack.ai.api.dto.GatewayLLMRequestDTO;
import cn.bugstack.ai.api.dto.GatewayLLMResponseDTO;

public interface IAdminLLMService {

    GatewayLLMResponseDTO testCallGateway(GatewayLLMRequestDTO requestDTO);
}
