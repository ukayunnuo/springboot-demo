package com.ukayunnuo.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * knife4j 测试响应结果
 *
 * @author yunnuo <a href="nuo.he@backgardon.com">Email: 2552846359@qq.com</a>
 * @date 2023-09-18
 */
@Builder
@Data
@ApiModel(value = "knife4j 测试响应结果", description = "用于测试")
public class Knife4jRes {

    @ApiModelProperty(notes = "响应测试 key")
    private String key;

    @ApiModelProperty(notes = "响应测试 value")
    private Object value;

}
