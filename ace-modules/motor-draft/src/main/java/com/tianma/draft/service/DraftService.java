package com.tianma.draft.service;

import com.tianma.draft.entity.Messages;
import com.tianma.draft.utils.ResultUtil;
import org.springframework.stereotype.Service;

/**
 * @Auther: JJY
 * @Date: 2018/9/24 22:29
 * @Description:
 */
@Service
public interface DraftService  {
    ResultUtil addDraft(Messages messages, Long uid);

    ResultUtil deleteDraft(Long message_id, Long uid);

    ResultUtil listDraft(Long uid);

    ResultUtil getDratft(Long message_id, Long uid);
}
