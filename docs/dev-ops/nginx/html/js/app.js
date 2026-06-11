// js/app.js
$(document).ready(function() {
    // 检查登录状态
    if(localStorage.getItem('mcp_admin_logged_in') !== 'true') {
        window.location.href = 'index.html';
        return;
    }

    // 退出登录
    $('#logoutBtn').on('click', function(e) {
        e.preventDefault();
        localStorage.removeItem('mcp_admin_logged_in');
        window.location.href = 'index.html';
    });

    // 侧边栏导航切换和动态加载页面
    $('.nav-link[data-target]').on('click', function(e) {
        e.preventDefault();
        
        // 更新激活状态
        $('.nav-link').removeClass('active');
        $(this).addClass('active');
        
        const targetId = $(this).data('target');
        loadView(targetId);
    });

    // 动态加载视图
    function loadView(targetId) {
        const viewPath = `views/${targetId}.html`;
        $('#main-content-wrapper').html('<div class="text-center py-5"><div class="spinner-border text-primary" role="status"></div><div class="mt-2 text-muted">加载中...</div></div>');
        
        $('#main-content-wrapper').load(viewPath, function(response, status, xhr) {
            if (status == "error") {
                $('#main-content-wrapper').html(`<div class="alert alert-danger m-4">页面加载失败：${xhr.status} ${xhr.statusText}</div>`);
                return;
            }
            
            // 页面加载后的初始化逻辑
            initViewLogic(targetId);
        });
    }

    // 初始化各个页面的逻辑
    function initViewLogic(targetId) {
        if (targetId === 'dashboard') {
            $('#display-api-url').text(API_BASE_URL);
            const dateOptions = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
            const dateEl = document.getElementById('current-date');
            if(dateEl) dateEl.textContent = new Date().toLocaleDateString('zh-CN', dateOptions);
            
            // 尝试获取网关总数
            $.ajax({
                url: API_ENDPOINTS.GET_GATEWAY_LIST,
                type: 'GET',
                success: function(response) {
                    if(response && response.code === '0000' && response.data) {
                        $('#stat-gateway-count').text(response.data.length);
                    }
                }
            });
        } else if (targetId === 'gateway-list') {
            loadGatewayList();
        } else if (targetId === 'gateway-tool') {
            loadGatewayToolList();
        } else if (targetId === 'gateway-protocol') {
            loadGatewayProtocolList();
        } else if (targetId === 'gateway-auth') {
            loadGatewayAuthList();
        }
    }

    // 初始加载 Dashboard
    loadView('dashboard');

    // 显示 Toast 通知
    function showToast(message, isSuccess = true) {
        const toastEl = $('#liveToast');
        const iconHtml = isSuccess ? '<i class="bi bi-check-circle-fill"></i>' : '<i class="bi bi-exclamation-triangle-fill"></i>';
        
        $('#toastMessage').html(`${iconHtml} <span>${message}</span>`);
        
        if(isSuccess) {
            toastEl.removeClass('bg-danger').addClass('bg-success');
        } else {
            toastEl.removeClass('bg-success').addClass('bg-danger');
        }
        
        const toast = new bootstrap.Toast(toastEl[0]);
        toast.show();
    }

    // 表单提交通用处理 - 使用事件委托
    function handleFormSubmitDelegated(formId, endpoint, dataProcessor, onSuccess) {
        // 先解绑以防重复绑定
        $(document).off('submit', '#' + formId);
        $(document).on('submit', '#' + formId, function(e) {
            e.preventDefault();
            
            const $btn = $(this).find('button[type="submit"]');
            const originalHtml = $btn.html();
            $btn.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>保存中...').prop('disabled', true);
            
            // 序列化表单数据为对象
            const formDataArray = $(this).serializeArray();
            const rawData = {};
            $.map(formDataArray, function(n, i){
                rawData[n['name']] = n['value'];
            });
            
            // 数据处理（如果需要转换类型或结构）
            let requestData;
            try {
                requestData = dataProcessor ? dataProcessor(rawData) : rawData;
            } catch(error) {
                showToast('数据格式错误: ' + error.message, false);
                $btn.html(originalHtml).prop('disabled', false);
                return;
            }

            // 发送请求
            $.ajax({
                url: endpoint,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(requestData),
                success: function(response) {
                    if(response && response.code === '0000') {
                        showToast('配置保存成功！');
                        if (onSuccess) onSuccess();
                    } else {
                        showToast('保存失败：' + (response.info || '未知错误'), false);
                    }
                },
                error: function(xhr, status, error) {
                    showToast('请求失败：' + error, false);
                },
                complete: function() {
                    $btn.html(originalHtml).prop('disabled', false);
                }
            });
        });
    }

    // 1. 保存网关基础配置
    handleFormSubmitDelegated('form-gateway-config', API_ENDPOINTS.SAVE_GATEWAY_CONFIG, function(data) {
        return {
            gatewayId: data.gatewayId,
            gatewayName: data.gatewayName,
            gatewayDesc: data.gatewayDesc,
            version: data.version,
            auth: parseInt(data.auth),
            status: parseInt(data.status)
        };
    }, function() {
        $('#gatewayConfigModal').modal('hide');
        setTimeout(loadGatewayList, 300);
    });

    // 2. 保存网关工具配置
    handleFormSubmitDelegated('form-gateway-tool', API_ENDPOINTS.SAVE_GATEWAY_TOOL_CONFIG, function(data) {
        return {
            gatewayId: data.gatewayId,
            toolId: data.toolId,
            toolName: data.toolName,
            toolType: data.toolType,
            toolDescription: data.toolDescription,
            toolVersion: data.toolVersion,
            protocolId: data.protocolId ? parseInt(data.protocolId) : null,
            protocolType: data.protocolType
        };
    }, function() {
        $('#gatewayToolModal').modal('hide');
        // 由于模态框关闭动画有延迟，稍微延时刷新列表避免遮罩问题
        setTimeout(loadGatewayToolList, 300);
    });

    // 3. 保存网关协议配置
    handleFormSubmitDelegated('form-gateway-protocol', API_ENDPOINTS.SAVE_GATEWAY_PROTOCOL, function(data) {
        let mappings = null;
        if(data.mappingsJson && data.mappingsJson.trim() !== '') {
            try {
                mappings = JSON.parse(data.mappingsJson);
            } catch(e) {
                throw new Error("Mappings JSON 格式不正确");
            }
        }
        
        return {
            httpProtocols: [
                {
                    protocolId: data.protocolId ? parseInt(data.protocolId) : null,
                    httpUrl: data.httpUrl,
                    httpMethod: data.httpMethod,
                    timeout: parseInt(data.timeout) || 5000,
                    httpHeaders: data.httpHeaders,
                    mappings: mappings
                }
            ]
        };
    }, function() {
        $('#gatewayProtocolModal').modal('hide');
        setTimeout(loadGatewayProtocolList, 300);
    });

    // 4. 保存网关认证配置
    handleFormSubmitDelegated('form-gateway-auth', API_ENDPOINTS.SAVE_GATEWAY_AUTH, function(data) {
        return {
            gatewayId: data.gatewayId,
            rateLimit: parseInt(data.rateLimit),
            expireTime: data.expireTime ? new Date(data.expireTime).getTime() : null
        };
    }, function() {
        $('#gatewayAuthModal').modal('hide');
        setTimeout(loadGatewayAuthList, 300);
    });

    // ==========================================
    // 网关列表相关
    // ==========================================
    $(document).on('click', '#refreshGatewayList', function() {
        const $btn = $(this);
        const originalHtml = $btn.html();
        $btn.html('<i class="bi bi-arrow-clockwise fa-spin"></i> 刷新中...').prop('disabled', true);
        
        loadGatewayList(() => {
            $btn.html(originalHtml).prop('disabled', false);
        });
    });

    $(document).on('click', '#addGatewayBtn', function() {
        $('#form-gateway-config')[0].reset();
        $('#config-gatewayId').prop('readonly', false);
        $('#gatewayConfigModalLabel').html('<i class="bi bi-plus-lg me-2"></i>新增网关基础配置');
    });

    // 修改网关配置
    $(document).on('click', '.btn-edit-gateway', function() {
        try {
            const itemDataStr = decodeURIComponent($(this).data('item'));
            const item = JSON.parse(itemDataStr);
            
            // 填充表单
            $('#config-gatewayId').val(item.gatewayId).prop('readonly', true);
            $('#config-gatewayName').val(item.gatewayName);
            $('#config-gatewayDesc').val(item.gatewayDesc);
            $('#config-version').val(item.version);
            $('#config-auth').val(item.auth);
            $('#config-status').val(item.status);
            
            $('#gatewayConfigModalLabel').html('<i class="bi bi-pencil-square me-2"></i>修改网关基础配置');
            $('#gatewayConfigModal').modal('show');
        } catch (e) {
            console.error("解析数据失败", e);
            showToast("解析数据失败", false);
        }
    });

    // 查看关联工具
    $(document).on('click', '.btn-view-tools', function() {
        const gatewayId = $(this).data('gateway-id');
        $('#gatewayToolsModalLabel').html(`<i class="bi bi-tools me-2"></i>网关 [${gatewayId}] 关联工具列表`);
        const tbody = $('#gatewayToolsTableBody');
        tbody.html('<tr><td colspan="7" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>加载中...</td></tr>');
        $('#gatewayToolsModal').modal('show');

        // 同时请求工具列表和协议列表
        $.when(
            $.ajax({ url: `${API_ENDPOINTS.GET_GATEWAY_TOOL_LIST_BY_ID}?gatewayId=${encodeURIComponent(gatewayId)}`, type: 'GET' }),
            $.ajax({ url: `${API_ENDPOINTS.GET_GATEWAY_PROTOCOL_LIST_BY_ID}?gatewayId=${encodeURIComponent(gatewayId)}`, type: 'GET' })
        ).done(function(toolsResponse, protocolsResponse) {
            const toolsResult = toolsResponse[0];
            const protocolsResult = protocolsResponse[0];

            if(toolsResult && toolsResult.code === '0000' && toolsResult.data) {
                const toolsList = toolsResult.data;
                const protocolsList = (protocolsResult && protocolsResult.code === '0000' && protocolsResult.data) ? protocolsResult.data : [];
                
                // 将协议列表转换为以 protocolId 为 key 的字典，方便查找
                const protocolsMap = {};
                protocolsList.forEach(p => {
                    protocolsMap[p.protocolId] = p;
                });

                if(toolsList.length === 0) {
                    tbody.html('<tr><td colspan="7" class="text-center text-muted py-4"><i class="bi bi-inbox fs-4 d-block mb-2"></i>暂无关联工具</td></tr>');
                } else {
                    let html = '';
                    toolsList.forEach(function(item, index) {
                        const protocol = item.protocolId ? protocolsMap[item.protocolId] : null;
                        const collapseId = `collapse-protocol-${index}`;
                        
                        // 主行
                        html += `
                            <tr class="align-middle">
                                <td class="text-center">
                                    <button class="btn btn-sm btn-link text-decoration-none text-muted p-0 toggle-protocol-details" type="button" data-bs-toggle="collapse" data-bs-target="#${collapseId}" aria-expanded="false" aria-controls="${collapseId}">
                                        <i class="bi bi-chevron-right"></i>
                                    </button>
                                </td>
                                <td><span class="badge bg-secondary">${item.toolId || '-'}</span></td>
                                <td class="fw-bold">${item.toolName || '-'}</td>
                                <td>${item.toolType || '-'}</td>
                                <td><span class="text-truncate d-inline-block text-muted" style="max-width: 150px;" title="${item.toolDescription || ''}">${item.toolDescription || '-'}</span></td>
                                <td>${item.toolVersion || '-'}</td>
                                <td><span class="badge bg-info text-dark">${item.protocolType || '-'}</span></td>
                            </tr>
                        `;

                        // 子行（可折叠）
                        let protocolDetailsHtml = '';
                        if (protocol) {
                            let mappingsHtml = '';
                            if (protocol.mappings && protocol.mappings.length > 0) {
                                let rows = protocol.mappings.map(m => `
                                    <tr>
                                        <td><code>${m.mappingType || '-'}</code></td>
                                        <td>${m.parentPath || '-'}</td>
                                        <td class="fw-bold">${m.fieldName || '-'}</td>
                                        <td><code>${m.mcpPath || '-'}</code></td>
                                        <td><span class="badge bg-light text-dark border">${m.mcpType || '-'}</span></td>
                                        <td>${m.isRequired === 1 ? '<span class="text-danger">是</span>' : '<span class="text-muted">否</span>'}</td>
                                        <td><span class="text-truncate d-inline-block" style="max-width: 150px;" title="${m.mcpDesc || ''}">${m.mcpDesc || '-'}</span></td>
                                    </tr>
                                `).join('');
                                
                                mappingsHtml = `
                                    <div class="mt-3">
                                        <h6 class="mb-2 text-muted" style="font-size: 0.85rem;"><i class="bi bi-list-columns-reverse me-1"></i>参数映射 (Mappings)</h6>
                                        <div class="table-responsive bg-white rounded border">
                                            <table class="table table-sm table-hover mb-0" style="font-size: 0.85rem;">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>映射类型</th>
                                                        <th>父级路径</th>
                                                        <th>字段名</th>
                                                        <th>MCP路径</th>
                                                        <th>MCP类型</th>
                                                        <th>必填</th>
                                                        <th>描述</th>
                                                    </tr>
                                                </thead>
                                                <tbody>${rows}</tbody>
                                            </table>
                                        </div>
                                    </div>
                                `;
                            } else {
                                mappingsHtml = `<div class="mt-2 text-muted" style="font-size: 0.85rem;"><i class="bi bi-info-circle me-1"></i>暂无参数映射配置。</div>`;
                            }

                            protocolDetailsHtml = `
                                <div class="card card-body bg-light border-0 p-3 my-2 shadow-sm">
                                    <div class="row g-3">
                                        <div class="col-md-8">
                                            <div class="d-flex align-items-center gap-2 mb-2">
                                                <span class="badge bg-primary fs-6">${protocol.httpMethod || '-'}</span>
                                                <code class="fs-6 text-dark bg-white px-2 py-1 rounded border">${protocol.httpUrl || '-'}</code>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="d-flex flex-column gap-1 text-muted" style="font-size: 0.85rem;">
                                                <div><strong>超时时间:</strong> ${protocol.timeout || '-'} ms</div>
                                                <div><strong>Headers:</strong> <code>${protocol.httpHeaders || '{}'}</code></div>
                                            </div>
                                        </div>
                                    </div>
                                    ${mappingsHtml}
                                </div>
                            `;
                        } else {
                            protocolDetailsHtml = `
                                <div class="alert alert-warning py-2 mb-2" role="alert" style="font-size: 0.9rem;">
                                    <i class="bi bi-exclamation-triangle me-2"></i>该工具尚未关联有效的协议配置，或关联的协议 ID (${item.protocolId || '无'}) 不存在。
                                </div>
                            `;
                        }

                        html += `
                            <tr class="collapse" id="${collapseId}">
                                <td colspan="7" class="p-0 border-bottom-0">
                                    <div class="px-4 py-2 bg-white">
                                        ${protocolDetailsHtml}
                                    </div>
                                </td>
                            </tr>
                        `;
                    });
                    tbody.html(html);

                    // 绑定折叠图标切换事件
                    $('.collapse').on('show.bs.collapse', function () {
                        $(this).prev('tr').find('.bi-chevron-right').removeClass('bi-chevron-right').addClass('bi-chevron-down');
                        $(this).prev('tr').addClass('table-active');
                    }).on('hide.bs.collapse', function () {
                        $(this).prev('tr').find('.bi-chevron-down').removeClass('bi-chevron-down').addClass('bi-chevron-right');
                        $(this).prev('tr').removeClass('table-active');
                    });

                }
            } else {
                tbody.html(`<tr><td colspan="7" class="text-center text-danger py-4">加载失败: ${toolsResult.info || '未知错误'}</td></tr>`);
            }
        }).fail(function() {
            tbody.html('<tr><td colspan="7" class="text-center text-danger py-4">网络请求失败，请检查服务是否启动</td></tr>');
        });
    });

    // 复制网关地址
    $(document).on('click', '.btn-copy-gateway-url', function() {
        const gatewayId = $(this).data('gateway-id');
        const sseUrl = `${API_BASE_URL}/${gatewayId}/mcp/sse`;
        
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard.writeText(sseUrl).then(() => {
                showToast('网关 SSE 地址已复制到剪贴板！');
            }).catch(err => {
                console.error('无法复制文本: ', err);
                showToast('复制失败，请手动复制', false);
            });
        } else {
            // Fallback
            const textArea = document.createElement("textarea");
            textArea.value = sseUrl;
            textArea.style.position = "fixed";
            textArea.style.top = "0";
            textArea.style.left = "0";
            textArea.style.width = "2em";
            textArea.style.height = "2em";
            textArea.style.padding = "0";
            textArea.style.border = "none";
            textArea.style.outline = "none";
            textArea.style.boxShadow = "none";
            textArea.style.background = "transparent";
            document.body.appendChild(textArea);
            textArea.focus();
            textArea.select();
            try {
                const successful = document.execCommand('copy');
                if(successful) {
                    showToast('网关 SSE 地址已复制到剪贴板！');
                } else {
                    showToast('复制失败，请手动复制', false);
                }
            } catch (err) {
                console.error('无法复制文本: ', err);
                showToast('复制失败，请手动复制', false);
            }
            document.body.removeChild(textArea);
        }
    });

    // 分页状态
    let gatewayCurrentPage = 1;
    const gatewayPageSize = 10;

    // 搜索表单提交
    $(document).on('submit', '#form-gateway-search', function(e) {
        e.preventDefault();
        gatewayCurrentPage = 1; // 搜索时重置为第一页
        loadGatewayList();
    });

    // 重置搜索
    $(document).on('click', '#btn-reset-search', function() {
        $('#form-gateway-search')[0].reset();
        gatewayCurrentPage = 1;
        loadGatewayList();
    });

    // 分页点击
    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();
        const page = $(this).data('page');
        const type = $(this).closest('nav').attr('id');
        
        if (type === 'pagination-container') {
            if (page && page !== gatewayCurrentPage) {
                gatewayCurrentPage = page;
                loadGatewayList();
            }
        } else if (type === 'tool-pagination-container') {
            if (page && page !== toolCurrentPage) {
                toolCurrentPage = page;
                loadGatewayToolList();
            }
        } else if (type === 'protocol-pagination-container') {
            if (page && page !== protocolCurrentPage) {
                protocolCurrentPage = page;
                loadGatewayProtocolList();
            }
        } else if (type === 'auth-pagination-container') {
            if (page && page !== authCurrentPage) {
                authCurrentPage = page;
                loadGatewayAuthList();
            }
        }
    });

    function loadGatewayList(callback) {
        const tbody = $('#gatewayTableBody');
        if(!callback) {
            tbody.html('<tr><td colspan="7" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>加载中...</td></tr>');
        }
        
        // 收集搜索参数
        const gatewayId = $('#search-gatewayId').val() || '';
        const gatewayName = $('#search-gatewayName').val() || '';
        
        const params = {
            page: gatewayCurrentPage,
            rows: gatewayPageSize
        };
        if (gatewayId) params.gatewayId = gatewayId;
        if (gatewayName) params.gatewayName = gatewayName;
        
        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_PAGE,
            type: 'GET',
            data: params,
            success: function(response) {
                if(response && response.code === '0000') {
                    const list = response.data || [];
                    const total = response.total || 0;
                    
                    if(list.length === 0) {
                        tbody.html('<tr><td colspan="7" class="text-center text-muted py-4"><i class="bi bi-inbox fs-4 d-block mb-2"></i>暂无网关数据</td></tr>');
                    } else {
                        let html = '';
                        list.forEach(function(item) {
                            const authLabel = item.auth === 1 ? '<span class="badge bg-success bg-opacity-10 text-success border border-success">启用</span>' : '<span class="badge bg-secondary bg-opacity-10 text-secondary border border-secondary">禁用</span>';
                            const statusLabel = item.status === 1 ? '<span class="badge bg-primary bg-opacity-10 text-primary border border-primary">强校验</span>' : '<span class="badge bg-warning bg-opacity-10 text-warning border border-warning">不校验</span>';
                            
                            html += `
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <code>${item.gatewayId || '-'}</code>
                                            <button type="button" class="btn btn-sm btn-outline-secondary btn-copy-gateway-url border-0" data-gateway-id="${item.gatewayId}" title="复制网关 SSE 地址"><i class="bi bi-clipboard"></i></button>
                                        </div>
                                    </td>
                                    <td class="fw-bold">${item.gatewayName || '-'}</td>
                                    <td><span class="text-truncate d-inline-block text-muted" style="max-width: 200px;" title="${item.gatewayDesc || ''}">${item.gatewayDesc || '-'}</span></td>
                                    <td><span class="badge bg-light text-dark">${item.version || '-'}</span></td>
                                    <td>${authLabel}</td>
                                    <td>${statusLabel}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button type="button" class="btn btn-outline-secondary btn-edit-gateway" data-item="${encodeURIComponent(JSON.stringify(item))}">
                                                <i class="bi bi-pencil-square"></i> 修改
                                            </button>
                                            <button type="button" class="btn btn-outline-primary btn-view-tools" data-gateway-id="${item.gatewayId}">
                                                <i class="bi bi-tools"></i> 工具
                                            </button>
                                            <button type="button" class="btn btn-outline-info btn-view-auth" data-gateway-id="${item.gatewayId}">
                                                <i class="bi bi-shield-lock"></i> 认证
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        });
                        tbody.html(html);
                    }
                    
                    // 渲染分页控件
                    renderPagination(total, gatewayCurrentPage, gatewayPageSize);
                } else {
                    tbody.html(`<tr><td colspan="7" class="text-center text-danger py-4"><i class="bi bi-exclamation-triangle me-2"></i>加载失败: ${response.info || '未知错误'}</td></tr>`);
                }
            },
            error: function() {
                tbody.html('<tr><td colspan="7" class="text-center text-danger py-4"><i class="bi bi-wifi-off me-2"></i>网络请求失败，请检查服务是否启动</td></tr>');
            },
            complete: function() {
                if(callback) callback();
            }
        });
    }

    // 渲染分页组件
    function renderPagination(total, currentPage, pageSize) {
        const totalPages = Math.ceil(total / pageSize);
        
        // 更新信息文本
        const start = total === 0 ? 0 : (currentPage - 1) * pageSize + 1;
        const end = Math.min(currentPage * pageSize, total);
        $('#pagination-info').html(`显示 ${start} 到 ${end} 条，共 <span class="fw-bold text-dark">${total}</span> 条数据`);
        
        // 生成分页按钮
        const $container = $('#pagination-container');
        $container.empty();
        
        if (totalPages <= 1) return; // 只有一页不显示按钮
        
        // 上一页
        $container.append(`
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">上一页</a>
            </li>
        `);
        
        // 页码按钮 (简单逻辑：显示最多5个页码)
        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, startPage + 4);
        
        if (endPage - startPage < 4) {
            startPage = Math.max(1, endPage - 4);
        }
        
        for (let i = startPage; i <= endPage; i++) {
            $container.append(`
                <li class="page-item ${currentPage === i ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }
        
        // 下一页
        $container.append(`
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">下一页</a>
            </li>
        `);
    }

    // ==========================================
    // 网关工具相关
    // ==========================================
    let toolCurrentPage = 1;
    const toolPageSize = 10;

    // 搜索表单提交
    $(document).on('submit', '#form-gateway-tool-search', function(e) {
        e.preventDefault();
        toolCurrentPage = 1;
        loadGatewayToolList();
    });

    // 重置搜索
    $(document).on('click', '#btn-reset-tool-search', function() {
        $('#form-gateway-tool-search')[0].reset();
        toolCurrentPage = 1;
        loadGatewayToolList();
    });

    $(document).on('click', '#refreshGatewayToolList', function() {
        const $btn = $(this);
        const originalHtml = $btn.html();
        $btn.html('<i class="bi bi-arrow-clockwise fa-spin"></i> 刷新中...').prop('disabled', true);
        
        loadGatewayToolList(() => {
            $btn.html(originalHtml).prop('disabled', false);
        });
    });

    $(document).on('click', '#addGatewayToolBtn', function() {
        $('#form-gateway-tool')[0].reset();
        
        // 自动生成8位数字工具ID
        const generatedToolId = Math.floor(10000000 + Math.random() * 90000000);
        $('#tool-toolId').val(generatedToolId);
        
        $('#tool-gatewayId-help').text('网关ID: -');
        $('#tool-protocolId-help').text('协议ID: -');
        $('#gatewayToolModalLabel').html('<i class="bi bi-tools me-2"></i>新增网关工具配置');
        
        loadGatewayOptions();
        loadProtocolOptions();
    });

    // 事件委托 - 修改工具
    $(document).on('click', '.btn-edit-tool', function() {
        try {
            const itemDataStr = decodeURIComponent($(this).data('item'));
            const item = JSON.parse(itemDataStr);
            
            // 填充表单
            $('#tool-toolId').val(item.toolId).prop('readonly', true);
            $('#tool-toolName').val(item.toolName);
            $('#tool-toolType').val(item.toolType);
            $('#tool-toolDescription').val(item.toolDescription);
            $('#tool-toolVersion').val(item.toolVersion);
            $('#tool-protocolType').val(item.protocolType);
            
            $('#gatewayToolModalLabel').html('<i class="bi bi-pencil-square me-2"></i>修改网关工具配置');
            
            // 加载下拉框选项并设置选中值
            loadGatewayOptions(item.gatewayId);
            loadProtocolOptions(item.protocolId);
            
            $('#gatewayToolModal').modal('show');
        } catch (e) {
            console.error("解析数据失败", e);
            showToast("解析数据失败", false);
        }
    });

    // 动态加载网关配置选项
    function loadGatewayOptions(selectedGatewayId = null) {
        const $select = $('#tool-gatewayId');
        const $helpText = $('#tool-gatewayId-help');
        
        // 保持现有选项，只更新"加载中"状态
        $select.html('<option value="">加载网关列表中...</option>');
        
        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_LIST,
            type: 'GET',
            success: function(response) {
                if(response && response.code === '0000' && response.data) {
                    let optionsHtml = '<option value="">请选择网关...</option>';
                    response.data.forEach(function(gw) {
                        // 使用松散比较(==)或转换类型，因为从后端传来的类型可能和本地解析的不一致
                        const isSelected = selectedGatewayId == gw.gatewayId ? 'selected' : '';
                        optionsHtml += `<option value="${gw.gatewayId}" ${isSelected}>${gw.gatewayName}</option>`;
                    });
                    $select.html(optionsHtml);
                    
                    // 如果有选中值，触发 change 事件以更新小字提示
                    if (selectedGatewayId) {
                        $helpText.text(`网关ID: ${selectedGatewayId}`);
                    } else {
                        $helpText.text('网关ID: -');
                    }
                } else {
                    $select.html('<option value="">加载失败，请重试</option>');
                }
            },
            error: function() {
                $select.html('<option value="">加载失败，请检查网络</option>');
            }
        });
    }

    // 监听下拉框改变事件更新小字
    $(document).on('change', '#tool-gatewayId', function() {
        const selectedId = $(this).val();
        if (selectedId) {
            $('#tool-gatewayId-help').text(`网关ID: ${selectedId}`);
        } else {
            $('#tool-gatewayId-help').text('网关ID: -');
        }
    });

    // 动态加载关联协议选项
    function loadProtocolOptions(selectedProtocolId = null) {
        const $select = $('#tool-protocolId');
        const $helpText = $('#tool-protocolId-help');
        
        $select.html('<option value="">加载协议列表中...</option>');
        
        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_PROTOCOL_LIST,
            type: 'GET',
            success: function(response) {
                if(response && response.code === '0000' && response.data) {
                    let optionsHtml = '<option value="">请选择关联协议...</option>';
                    response.data.forEach(function(protocol) {
                        const isSelected = selectedProtocolId == protocol.protocolId ? 'selected' : '';
                        optionsHtml += `<option value="${protocol.protocolId}" ${isSelected}>${protocol.httpUrl}</option>`;
                    });
                    $select.html(optionsHtml);
                    
                    if (selectedProtocolId) {
                        $helpText.text(`协议ID: ${selectedProtocolId}`);
                    } else {
                        $helpText.text('协议ID: -');
                    }
                } else {
                    $select.html('<option value="">加载失败，请重试</option>');
                }
            },
            error: function() {
                $select.html('<option value="">加载失败，请检查网络</option>');
            }
        });
    }

    // 监听协议下拉框改变事件
    $(document).on('change', '#tool-protocolId', function() {
        const selectedId = $(this).val();
        if (selectedId) {
            $('#tool-protocolId-help').text(`协议ID: ${selectedId}`);
        } else {
            $('#tool-protocolId-help').text('协议ID: -');
        }
    });

    // 事件委托 - 删除工具
    $(document).on('click', '.btn-delete-tool', function() {
        const gatewayId = $(this).data('gateway-id');
        const toolId = $(this).data('tool-id');
        
        if(confirm(`确定要删除工具 ID: ${toolId} 吗？`)) {
            const $btn = $(this);
            const originalHtml = $btn.html();
            $btn.html('<i class="bi bi-hourglass-split"></i>').prop('disabled', true);
            
            $.ajax({
                url: `${API_ENDPOINTS.DELETE_GATEWAY_TOOL}?gatewayId=${encodeURIComponent(gatewayId)}&toolId=${encodeURIComponent(toolId)}`,
                type: 'POST',
                success: function(response) {
                    if(response && response.code === '0000') {
                        showToast('删除成功！');
                        loadGatewayToolList();
                    } else {
                        showToast('删除失败：' + (response.info || '未知错误'), false);
                        $btn.html(originalHtml).prop('disabled', false);
                    }
                },
                error: function(xhr, status, error) {
                    showToast('请求失败：' + error, false);
                    $btn.html(originalHtml).prop('disabled', false);
                }
            });
        }
    });

    function loadGatewayToolList(callback) {
        const tbody = $('#gatewayToolTableBody');
        if(!callback) {
            tbody.html('<tr><td colspan="8" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>加载中...</td></tr>');
        }
        
        // 收集搜索参数
        const gatewayId = $('#search-tool-gatewayId').val() || '';
        const toolId = $('#search-tool-toolId').val() || '';
        
        const params = {
            page: toolCurrentPage,
            rows: toolPageSize
        };
        if (gatewayId) params.gatewayId = gatewayId;
        if (toolId) params.toolId = toolId;
        
        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_TOOL_PAGE,
            type: 'GET',
            data: params,
            success: function(response) {
                if(response && response.code === '0000') {
                    const list = response.data || [];
                    const total = response.total || 0;
                    
                    if(list.length === 0) {
                        tbody.html('<tr><td colspan="8" class="text-center text-muted py-4"><i class="bi bi-inbox fs-4 d-block mb-2"></i>暂无网关工具数据</td></tr>');
                    } else {
                        let html = '';
                        list.forEach(function(item) {
                            const itemData = encodeURIComponent(JSON.stringify(item));
                            html += `
                                <tr>
                                    <td><code>${item.gatewayId || '-'}</code></td>
                                    <td><span class="badge bg-secondary">${item.toolId || '-'}</span></td>
                                    <td class="fw-bold text-truncate" style="max-width: 150px;" title="${item.toolName || ''}">${item.toolName || '-'}</td>
                                    <td>${item.toolType || '-'}</td>
                                    <td><span class="text-truncate d-inline-block text-muted" style="max-width: 150px;" title="${item.toolDescription || ''}">${item.toolDescription || '-'}</span></td>
                                    <td>${item.toolVersion || '-'}</td>
                                    <td><span class="badge bg-info text-dark">${item.protocolType || '-'}</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button type="button" class="btn btn-outline-primary btn-edit-tool" data-item="${itemData}">
                                                <i class="bi bi-pencil-square"></i> 修改
                                            </button>
                                            <button type="button" class="btn btn-outline-danger btn-delete-tool" data-gateway-id="${item.gatewayId}" data-tool-id="${item.toolId}">
                                                <i class="bi bi-trash"></i> 删除
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        });
                        tbody.html(html);
                    }
                    
                    renderPaginationTool(total, toolCurrentPage, toolPageSize, 'tool-pagination-info', 'tool-pagination-container');
                } else {
                    tbody.html(`<tr><td colspan="8" class="text-center text-danger py-4"><i class="bi bi-exclamation-triangle me-2"></i>加载失败: ${response.info || '未知错误'}</td></tr>`);
                }
            },
            error: function() {
                tbody.html('<tr><td colspan="8" class="text-center text-danger py-4"><i class="bi bi-wifi-off me-2"></i>网络请求失败，请检查服务是否启动</td></tr>');
            },
            complete: function() {
                if(callback) callback();
            }
        });
    }

    // ==========================================
    // 网关协议列表相关
    // ==========================================
    let protocolCurrentPage = 1;
    const protocolPageSize = 10;

    // 搜索表单提交
    $(document).on('submit', '#form-gateway-protocol-search', function(e) {
        e.preventDefault();
        protocolCurrentPage = 1;
        loadGatewayProtocolList();
    });

    // 重置搜索
    $(document).on('click', '#btn-reset-protocol-search', function() {
        $('#form-gateway-protocol-search')[0].reset();
        protocolCurrentPage = 1;
        loadGatewayProtocolList();
    });

    let uploadedOpenApiJson = ''; // 用于存储上传的 JSON 字符串

    $(document).on('click', '#refreshGatewayProtocolList', function() {
        const $btn = $(this);
        const originalHtml = $btn.html();
        $btn.html('<i class="bi bi-arrow-clockwise fa-spin"></i> 刷新中...').prop('disabled', true);
        
        loadGatewayProtocolList(() => {
            $btn.html(originalHtml).prop('disabled', false);
        });
    });

    // 导入协议按钮点击
    $(document).on('click', '#importProtocolBtn', function() {
        $('#form-import-protocol')[0].reset();
        $('#endpoints-selection-container').addClass('d-none');
        $('#endpoints-list').empty();
        $('#btn-submit-import').prop('disabled', true);
        uploadedOpenApiJson = '';
    });

    // 监听文件上传
    $(document).on('change', '#import-json-file', function(e) {
        const file = e.target.files[0];
        if (!file) {
            $('#endpoints-selection-container').addClass('d-none');
            $('#btn-submit-import').prop('disabled', true);
            return;
        }

        const reader = new FileReader();
        reader.onload = function(event) {
            try {
                const jsonStr = event.target.result;
                const jsonObj = JSON.parse(jsonStr);
                uploadedOpenApiJson = jsonStr;

                if (!jsonObj.paths || Object.keys(jsonObj.paths).length === 0) {
                    showToast('文件中未找到有效的接口路径 (paths)', false);
                    return;
                }

                // 渲染接口列表
                let listHtml = '<div class="list-group">';
                Object.keys(jsonObj.paths).forEach((path, index) => {
                    const methods = Object.keys(jsonObj.paths[path]).join(', ').toUpperCase();
                    listHtml += `
                        <label class="list-group-item d-flex gap-2">
                            <input class="form-check-input flex-shrink-0 endpoint-checkbox" type="checkbox" value="${path}" checked>
                            <span>
                                <strong>${path}</strong>
                                <small class="d-block text-muted">Methods: ${methods}</small>
                            </span>
                        </label>
                    `;
                });
                listHtml += '</div>';

                $('#endpoints-list').html(listHtml);
                $('#endpoints-selection-container').removeClass('d-none');
                $('#btn-submit-import').prop('disabled', false);

            } catch (err) {
                console.error(err);
                showToast('JSON 文件解析失败，请检查格式', false);
            }
        };
        reader.readAsText(file);
    });

    // 提交导入
    $(document).on('submit', '#form-import-protocol', function(e) {
        e.preventDefault();
        
        const selectedEndpoints = [];
        $('.endpoint-checkbox:checked').each(function() {
            selectedEndpoints.push($(this).val());
        });

        if (selectedEndpoints.length === 0) {
            showToast('请至少选择一个要导入的接口', false);
            return;
        }

        const $btn = $('#btn-submit-import');
        const originalHtml = $btn.html();
        $btn.html('<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>导入中...').prop('disabled', true);

        const requestData = {
            openApiJson: uploadedOpenApiJson,
            endpoints: selectedEndpoints
        };

        $.ajax({
            url: API_ENDPOINTS.IMPORT_GATEWAY_PROTOCOL,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            success: function(response) {
                if(response && response.code === '0000') {
                    showToast('协议导入成功！');
                    $('#importProtocolModal').modal('hide');
                    setTimeout(loadGatewayProtocolList, 300);
                } else {
                    showToast('导入失败：' + (response.info || '未知错误'), false);
                }
            },
            error: function(xhr, status, error) {
                showToast('请求失败：' + error, false);
            },
            complete: function() {
                $btn.html(originalHtml).prop('disabled', false);
            }
        });
    });

    $(document).on('click', '.btn-edit-protocol', function() {
        try {
            const itemDataStr = decodeURIComponent($(this).data('item'));
            const item = JSON.parse(itemDataStr);
            
            $('#protocol-protocolId').val(item.protocolId);
            $('#protocol-httpUrl').val(item.httpUrl);
            $('#protocol-httpMethod').val(item.httpMethod);
            $('#protocol-timeout').val(item.timeout);
            $('#protocol-httpHeaders').val(item.httpHeaders);
            
            if (item.mappings && item.mappings.length > 0) {
                $('#protocol-mappingsJson').val(JSON.stringify(item.mappings, null, 2));
            } else {
                $('#protocol-mappingsJson').val('');
            }

            // 更新解析目标地址
            updateProtocolTargetEndpoint();
            $('#protocol-import-json-file').val('');
            
            $('#gatewayProtocolModalLabel').html('<i class="bi bi-pencil-square me-2"></i>修改网关协议配置');
            $('#gatewayProtocolModal').modal('show');
        } catch (e) {
            console.error("解析数据失败", e);
            showToast("解析数据失败", false);
        }
    });

    // 监听协议 URL 输入框变化，更新解析目标地址
    $(document).on('input', '#protocol-httpUrl', function() {
        updateProtocolTargetEndpoint();
    });

    function updateProtocolTargetEndpoint() {
        const fullUrl = $('#protocol-httpUrl').val() || '';
        try {
            if (fullUrl) {
                const urlObj = new URL(fullUrl);
                const targetEndpoint = urlObj.pathname;
                $('#protocol-target-endpoint').text(targetEndpoint);
                return targetEndpoint;
            }
        } catch (e) {
            // URL 不合法时不报错，保持原样或给出提示
        }
        $('#protocol-target-endpoint').text('-');
        return null;
    }

    // 解析并导入协议 JSON
    $(document).on('click', '#btn-parse-protocol-json', function() {
        const fileInput = document.getElementById('protocol-import-json-file');
        const file = fileInput.files[0];
        
        if (!file) {
            showToast('请先选择要上传的 JSON 文件', false);
            return;
        }

        const targetEndpoint = updateProtocolTargetEndpoint();
        if (!targetEndpoint || targetEndpoint === '-') {
            showToast('请先填写有效的请求地址 (URL)', false);
            return;
        }

        const $btn = $(this);
        const originalHtml = $btn.html();
        $btn.html('<i class="spinner-border spinner-border-sm"></i> 解析中...').prop('disabled', true);

        const reader = new FileReader();
        reader.onload = function(event) {
            const jsonStr = event.target.result;
            
            try {
                // 验证是否为合法 JSON
                JSON.parse(jsonStr);
                
                // 构造请求体
                const requestData = {
                    openApiJson: jsonStr,
                    endpoints: [targetEndpoint]
                };

                $.ajax({
                    url: API_ENDPOINTS.ANALYSIS_PROTOCOL,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(requestData),
                    success: function(response) {
                        if (response && response.code === '0000' && response.data && response.data.length > 0) {
                            const parsedProtocol = response.data[0];
                            
                            // 填充解析后的数据
                            if (parsedProtocol.httpMethod) {
                                $('#protocol-httpMethod').val(parsedProtocol.httpMethod.toLowerCase());
                            }
                            if (parsedProtocol.httpHeaders) {
                                $('#protocol-httpHeaders').val(parsedProtocol.httpHeaders);
                            }
                            if (parsedProtocol.timeout) {
                                $('#protocol-timeout').val(parsedProtocol.timeout);
                            }
                            if (parsedProtocol.mappings && parsedProtocol.mappings.length > 0) {
                                $('#protocol-mappingsJson').val(JSON.stringify(parsedProtocol.mappings, null, 2));
                            }
                            
                            showToast('解析成功，已填充配置信息');
                        } else {
                            showToast('解析失败：未找到对应的接口配置或解析结果为空', false);
                        }
                    },
                    error: function(xhr, status, error) {
                        showToast('请求解析接口失败：' + error, false);
                    },
                    complete: function() {
                        $btn.html(originalHtml).prop('disabled', false);
                    }
                });

            } catch (e) {
                showToast('JSON 文件格式不正确，请检查', false);
                $btn.html(originalHtml).prop('disabled', false);
            }
        };
        
        reader.onerror = function() {
            showToast('读取文件失败', false);
            $btn.html(originalHtml).prop('disabled', false);
        };
        
        reader.readAsText(file);
    });

    $(document).on('click', '.btn-delete-protocol', function() {
        const protocolId = $(this).data('protocol-id');
        
        if(confirm(`确定要删除协议 ID: ${protocolId} 吗？`)) {
            const $btn = $(this);
            const originalHtml = $btn.html();
            $btn.html('<i class="bi bi-hourglass-split"></i>').prop('disabled', true);
            
            $.ajax({
                url: `${API_ENDPOINTS.DELETE_GATEWAY_PROTOCOL}?protocolId=${encodeURIComponent(protocolId)}`,
                type: 'POST',
                success: function(response) {
                    if(response && response.code === '0000') {
                        showToast('删除成功！');
                        loadGatewayProtocolList();
                    } else {
                        showToast('删除失败：' + (response.info || '未知错误'), false);
                        $btn.html(originalHtml).prop('disabled', false);
                    }
                },
                error: function(xhr, status, error) {
                    showToast('请求失败：' + error, false);
                    $btn.html(originalHtml).prop('disabled', false);
                }
            });
        }
    });

    function loadGatewayProtocolList(callback) {
        const tbody = $('#gatewayProtocolTableBody');
        if(!callback) {
            tbody.html('<tr><td colspan="5" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>加载中...</td></tr>');
        }
        
        // 收集搜索参数
        const protocolId = $('#search-protocol-protocolId').val() || '';
        const httpUrl = $('#search-protocol-httpUrl').val() || '';
        
        const params = {
            page: protocolCurrentPage,
            rows: protocolPageSize
        };
        if (protocolId) params.protocolId = protocolId;
        if (httpUrl) params.httpUrl = httpUrl;
        
        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_PROTOCOL_PAGE,
            type: 'GET',
            data: params,
            success: function(response) {
                if(response && response.code === '0000') {
                    const list = response.data || [];
                    const total = response.total || 0;
                    
                    if(list.length === 0) {
                        tbody.html('<tr><td colspan="5" class="text-center text-muted py-4"><i class="bi bi-inbox fs-4 d-block mb-2"></i>暂无网关协议数据</td></tr>');
                    } else {
                        let html = '';
                        list.forEach(function(item) {
                            const itemData = encodeURIComponent(JSON.stringify(item));
                            html += `
                                <tr>
                                    <td><code>${item.protocolId || '-'}</code></td>
                                    <td class="text-truncate" style="max-width: 250px;" title="${item.httpUrl || ''}">${item.httpUrl || '-'}</td>
                                    <td><span class="badge bg-secondary">${item.httpMethod || '-'}</span></td>
                                    <td>${item.timeout || '-'} ms</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button type="button" class="btn btn-outline-primary btn-edit-protocol" data-item="${itemData}">
                                                <i class="bi bi-pencil-square"></i> 修改
                                            </button>
                                            <button type="button" class="btn btn-outline-danger btn-delete-protocol" data-protocol-id="${item.protocolId}">
                                                <i class="bi bi-trash"></i> 删除
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        });
                        tbody.html(html);
                    }
                    
                    renderPaginationTool(total, protocolCurrentPage, protocolPageSize, 'protocol-pagination-info', 'protocol-pagination-container');
                } else {
                    tbody.html(`<tr><td colspan="5" class="text-center text-danger py-4"><i class="bi bi-exclamation-triangle me-2"></i>加载失败: ${response.info || '未知错误'}</td></tr>`);
                }
            },
            error: function() {
                tbody.html('<tr><td colspan="5" class="text-center text-danger py-4"><i class="bi bi-wifi-off me-2"></i>网络请求失败，请检查服务是否启动</td></tr>');
            },
            complete: function() {
                if(callback) callback();
            }
        });
    }

    // ==========================================
    // 网关认证列表相关
    // ==========================================
    let authCurrentPage = 1;
    const authPageSize = 10;

    // 搜索表单提交
    $(document).on('submit', '#form-gateway-auth-search', function(e) {
        e.preventDefault();
        authCurrentPage = 1;
        loadGatewayAuthList();
    });

    // 重置搜索
    $(document).on('click', '#btn-reset-auth-search', function() {
        $('#form-gateway-auth-search')[0].reset();
        authCurrentPage = 1;
        loadGatewayAuthList();
    });

    $(document).on('click', '#refreshGatewayAuthList', function() {
        const $btn = $(this);
        const originalHtml = $btn.html();
        $btn.html('<i class="bi bi-arrow-clockwise fa-spin"></i> 刷新中...').prop('disabled', true);
        
        loadGatewayAuthList(() => {
            $btn.html(originalHtml).prop('disabled', false);
        });
    });

    $(document).on('click', '#addGatewayAuthBtn', function() {
        $('#form-gateway-auth')[0].reset();
        $('#auth-gatewayId').prop('readonly', false);
        $('#gatewayAuthModalLabel').html('<i class="bi bi-shield-check me-2"></i>新增认证配置');
        
        // 默认过期时间设置为1个月后
        const d = new Date();
        d.setMonth(d.getMonth() + 1);
        const localIsoString = new Date(d.getTime() - (d.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
        $('#auth-expireTime').val(localIsoString);
    });

    $(document).on('click', '.btn-edit-auth', function() {
        try {
            const itemDataStr = decodeURIComponent($(this).data('item'));
            const item = JSON.parse(itemDataStr);
            
            $('#auth-gatewayId').val(item.gatewayId).prop('readonly', true);
            $('#auth-rateLimit').val(item.rateLimit);
            
            if (item.expireTime) {
                const d = new Date(item.expireTime);
                const localIsoString = new Date(d.getTime() - (d.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
                $('#auth-expireTime').val(localIsoString);
            } else {
                $('#auth-expireTime').val('');
            }
            
            $('#gatewayAuthModalLabel').html('<i class="bi bi-pencil-square me-2"></i>修改网关认证配置');
            $('#gatewayAuthModal').modal('show');
        } catch (e) {
            console.error("解析数据失败", e);
            showToast("解析数据失败", false);
        }
    });

    $(document).on('click', '.btn-delete-auth', function() {
        const gatewayId = $(this).data('gateway-id');
        
        if(confirm(`确定要删除网关 ID: ${gatewayId} 的认证配置吗？`)) {
            const $btn = $(this);
            const originalHtml = $btn.html();
            $btn.html('<i class="bi bi-hourglass-split"></i>').prop('disabled', true);
            
            $.ajax({
                url: `${API_ENDPOINTS.DELETE_GATEWAY_AUTH}?gatewayId=${encodeURIComponent(gatewayId)}`,
                type: 'POST',
                success: function(response) {
                    if(response && response.code === '0000') {
                        showToast('删除成功！');
                        loadGatewayAuthList();
                    } else {
                        showToast('删除失败：' + (response.info || '未知错误'), false);
                        $btn.html(originalHtml).prop('disabled', false);
                    }
                },
                error: function(xhr, status, error) {
                    showToast('请求失败：' + error, false);
                    $btn.html(originalHtml).prop('disabled', false);
                }
            });
        }
    });

    $(document).on('click', '.btn-copy-key', function() {
        const apiKey = $(this).data('key');
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard.writeText(apiKey).then(() => {
                showToast('API Key 已复制到剪贴板！');
            }).catch(err => {
                console.error('无法复制文本: ', err);
                showToast('复制失败，请手动复制', false);
            });
        } else {
            // Fallback
            const textArea = document.createElement("textarea");
            textArea.value = apiKey;
            // 确保不导致页面滚动
            textArea.style.position = "fixed";
            textArea.style.top = "0";
            textArea.style.left = "0";
            textArea.style.width = "2em";
            textArea.style.height = "2em";
            textArea.style.padding = "0";
            textArea.style.border = "none";
            textArea.style.outline = "none";
            textArea.style.boxShadow = "none";
            textArea.style.background = "transparent";
            document.body.appendChild(textArea);
            textArea.focus();
            textArea.select();
            try {
                const successful = document.execCommand('copy');
                if(successful) {
                    showToast('API Key 已复制到剪贴板！');
                } else {
                    showToast('复制失败，请手动复制', false);
                }
            } catch (err) {
                console.error('无法复制文本: ', err);
                showToast('复制失败，请手动复制', false);
            }
            document.body.removeChild(textArea);
        }
    });

    function loadGatewayAuthList(callback) {
        const tbody = $('#gatewayAuthTableBody');
        if(!callback) {
            tbody.html('<tr><td colspan="5" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>加载中...</td></tr>');
        }
        
        // 收集搜索参数
        const gatewayId = $('#search-auth-gatewayId').val() || '';
        
        const params = {
            page: authCurrentPage,
            rows: authPageSize
        };
        if (gatewayId) params.gatewayId = gatewayId;
        
        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_AUTH_PAGE,
            type: 'GET',
            data: params,
            success: function(response) {
                if(response && response.code === '0000') {
                    const list = response.data || [];
                    const total = response.total || 0;
                    
                    if(list.length === 0) {
                        tbody.html('<tr><td colspan="5" class="text-center text-muted py-4"><i class="bi bi-inbox fs-4 d-block mb-2"></i>暂无网关认证数据</td></tr>');
                    } else {
                        let html = '';
                        list.forEach(function(item) {
                            const itemData = encodeURIComponent(JSON.stringify(item));
                            // 格式化时间戳
                            let expireTimeStr = '-';
                            if (item.expireTime) {
                                const d = new Date(item.expireTime);
                                expireTimeStr = d.toLocaleString();
                            }
                            html += `
                                <tr>
                                    <td><code>${item.gatewayId || '-'}</code></td>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <span class="text-truncate d-inline-block" style="max-width: 200px;" title="${item.apiKey || ''}">${item.apiKey || '-'}</span>
                                            ${item.apiKey ? `<button type="button" class="btn btn-sm btn-outline-secondary btn-copy-key border-0" data-key="${item.apiKey}" title="复制 API Key"><i class="bi bi-clipboard"></i></button>` : ''}
                                        </div>
                                    </td>
                                    <td>${item.rateLimit || '-'} 次/秒</td>
                                    <td>${expireTimeStr}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button type="button" class="btn btn-outline-primary btn-edit-auth" data-item="${itemData}">
                                                <i class="bi bi-pencil-square"></i> 修改
                                            </button>
                                            <button type="button" class="btn btn-outline-danger btn-delete-auth" data-gateway-id="${item.gatewayId}">
                                                <i class="bi bi-trash"></i> 删除
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            `;
                        });
                        tbody.html(html);
                    }
                    
                    renderPaginationTool(total, authCurrentPage, authPageSize, 'auth-pagination-info', 'auth-pagination-container');
                } else {
                    tbody.html(`<tr><td colspan="5" class="text-center text-danger py-4"><i class="bi bi-exclamation-triangle me-2"></i>加载失败: ${response.info || '未知错误'}</td></tr>`);
                }
            },
            error: function() {
                tbody.html('<tr><td colspan="5" class="text-center text-danger py-4"><i class="bi bi-wifi-off me-2"></i>网络请求失败，请检查服务是否启动</td></tr>');
            },
            complete: function() {
                if(callback) callback();
            }
        });
    }

    // 通用的渲染分页组件（网关、工具、协议、认证共用结构）
    function renderPaginationTool(total, currentPage, pageSize, infoId, containerId) {
        const totalPages = Math.ceil(total / pageSize);
        
        // 更新信息文本
        const start = total === 0 ? 0 : (currentPage - 1) * pageSize + 1;
        const end = Math.min(currentPage * pageSize, total);
        $(`#${infoId}`).html(`显示 ${start} 到 ${end} 条，共 <span class="fw-bold text-dark">${total}</span> 条数据`);
        
        // 生成分页按钮
        const $container = $(`#${containerId}`);
        $container.empty();
        
        if (totalPages <= 1) return; // 只有一页不显示按钮
        
        // 上一页
        $container.append(`
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">上一页</a>
            </li>
        `);
        
        // 页码按钮
        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, startPage + 4);
        
        if (endPage - startPage < 4) {
            startPage = Math.max(1, endPage - 4);
        }
        
        for (let i = startPage; i <= endPage; i++) {
            $container.append(`
                <li class="page-item ${currentPage === i ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }
        
        // 下一页
        $container.append(`
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">下一页</a>
            </li>
        `);
    }

    // 查看网关认证配置
    $(document).on('click', '.btn-view-auth', function() {
        const gatewayId = $(this).data('gateway-id');
        $('#gatewayAuthViewModalLabel').html(`<i class="bi bi-shield-lock me-2"></i>网关 [${gatewayId}] 认证配置`);
        const tbody = $('#gatewayAuthViewTableBody');
        tbody.html('<tr><td colspan="3" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary me-2" role="status"></div>加载中...</td></tr>');
        $('#gatewayAuthViewModal').modal('show');

        $.ajax({
            url: API_ENDPOINTS.GET_GATEWAY_AUTH_PAGE,
            type: 'GET',
            data: { gatewayId: gatewayId, page: 1, rows: 10 },
            success: function(response) {
                if (response && response.code === '0000') {
                    const list = response.data || [];
                    if (list.length === 0) {
                        tbody.html('<tr><td colspan="3" class="text-center text-muted py-4"><i class="bi bi-inbox fs-4 d-block mb-2"></i>该网关未配置认证信息</td></tr>');
                    } else {
                        let html = '';
                        list.forEach(item => {
                            html += `
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center gap-2">
                                            <code>${item.apiKey || '-'}</code>
                                            <button type="button" class="btn btn-sm btn-outline-secondary btn-copy-key border-0" data-key="${item.apiKey}" title="复制 API Key"><i class="bi bi-clipboard"></i></button>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-info bg-opacity-10 text-info border border-info">${item.rateLimit || '-'} 次/秒</span></td>
                                    <td><span class="text-muted small"><i class="bi bi-clock me-1"></i>${item.expireTime ? new Date(item.expireTime).toLocaleString() : '永久'}</span></td>
                                </tr>
                            `;
                        });
                        tbody.html(html);
                    }
                } else {
                    tbody.html(`<tr><td colspan="3" class="text-center text-danger py-4"><i class="bi bi-exclamation-triangle me-2"></i>加载失败: ${response.info || '未知错误'}</td></tr>`);
                }
            },
            error: function() {
                tbody.html('<tr><td colspan="3" class="text-center text-danger py-4"><i class="bi bi-wifi-off me-2"></i>网络请求失败</td></tr>');
            }
        });
    });

});
