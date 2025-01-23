package com.ukayunnuo.webclient.domain.request;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * WebClient请求参数
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
public class WebClientReq {

    private String url;

    private JSONObject requestBody;
}
