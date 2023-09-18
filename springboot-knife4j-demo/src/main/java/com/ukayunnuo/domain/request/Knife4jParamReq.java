package com.ukayunnuo.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * knife4j 请求参数
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
@ApiModel(value = "knife4j 请求参数", description = "用于测试")
public class Knife4jParamReq {

    @ApiModelProperty(notes = "测试 key", required = true)
    private String key;

    @ApiModelProperty(notes = "测试 value值")
    private String value;

}
