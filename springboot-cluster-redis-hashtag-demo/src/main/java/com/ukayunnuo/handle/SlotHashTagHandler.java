package com.ukayunnuo.handle;

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.core.ErrorCode;
import com.ukayunnuo.core.exception.ServiceException;
import com.ukayunnuo.function.GetNodeFunction;
import com.ukayunnuo.init.ClusterNodesAndSlotInitHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * slot 值 与 hashTag 的 处理器
 *
 * @author yunnuo <a href="2552846359@qq.com">Email: 2552846359@qq.com</a>
 * @date 2024-05-22
 */
@Slf4j
@Component
public class SlotHashTagHandler {

    private final GetNodeFunction getNodeFunction = new GetNodeFunction();

    public String getHashTag(Long uid) {
        Map<String, String> nodeSlotMapping = ClusterNodesAndSlotInitHandle.NODE_SLOT_MAPPING;
        Map<String, String> nodeHashtagKeyMapping = ClusterNodesAndSlotInitHandle.NODE_HASHTAG_KEY_MAPPING;
        String node = getNodeFunction.apply(String.valueOf(uid), nodeSlotMapping);
        if (node == null) {
            log.warn("getHashTag failed.. 未找到对应节点! uid:{}, nodeSlotMapping:{}", uid, JSONObject.toJSONString(nodeSlotMapping));
            throw new ServiceException(ErrorCode.PARAM_ERROR.getCode(), "未找到对应节点");
        }
        String hashtag = nodeHashtagKeyMapping.get(node);
        if (hashtag == null) {
            log.warn("getHashTag failed.. 未找到对应节点的 hashtag! uid:{}, node:{}, nodeHashtagKeyMapping:{}", uid, node, JSONObject.toJSONString(nodeHashtagKeyMapping));
        }
        return hashtag;
    }


}
