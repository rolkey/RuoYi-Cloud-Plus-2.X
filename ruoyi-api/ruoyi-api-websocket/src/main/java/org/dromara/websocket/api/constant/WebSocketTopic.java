package org.dromara.websocket.api.constant;

/**
 * 实时消息主题常量（三级：主类/子类/具体业务ID）
 *
 * @author ruoyi
 */
public interface WebSocketTopic {

    // ========== 主类（模块） ==========

    /** 系统模块 */
    String SYSTEM = "system";

    /** HIS 模块 */
    String HIS = "his";

    /** 定时任务模块 */
    String JOB = "job";

    /** 工作流模块 */
    String WORKFLOW = "workflow";

    /** 代码生成模块 */
    String GEN = "gen";

}
