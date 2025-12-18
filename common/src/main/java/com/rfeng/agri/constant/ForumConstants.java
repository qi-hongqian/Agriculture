package com.rfeng.agri.constant;

/**
 * 论坛常量类
 * 定义论坛系统中使用的各类常量值
 *
 * @author 齐洪乾
 * @version 1.00
 * @time 2025/12/18
 */
public class ForumConstants {

    /**
     * 审核状态常量
     * 用于帖子和评论的审核流程
     */
    public static class AuditStatus {
        /** 待审核 */
        public static final int PENDING = 0;
        /** 已通过 */
        public static final int APPROVED = 1;
        /** 审核拒绝 */
        public static final int REJECTED = 2;
    }

    /**
     * 可见状态常量
     * 控制帖子和评论的显示状态
     */
    public static class VisibleStatus {
        /** 已删除 */
        public static final int DELETED = 0;
        /** 正常显示 */
        public static final int NORMAL = 1;
        /** 作者隐藏 */
        public static final int AUTHOR_HIDDEN = 2;
        /** 管理员隐藏 */
        public static final int ADMIN_HIDDEN = 3;
    }

    /**
     * 是否标记常量
     * 通用的布尔值标记（置顶、精华、推荐、锁定等）
     */
    public static class Flag {
        /** 否 */
        public static final int NO = 0;
        /** 是 */
        public static final int YES = 1;
    }

    /**
     * 评论层级常量
     * 限制评论的嵌套深度
     */
    public static class CommentLevel {
        /** 一级评论（直接评论帖子） */
        public static final int LEVEL_1 = 1;
        /** 二级评论（回复其他评论） */
        public static final int LEVEL_2 = 2;
    }

    /**
     * 用户互动类型常量
     * 记录用户的各类互动行为
     */
    public static class ActionType {
        /** 点赞帖子 */
        public static final int LIKE_POST = 1;
        /** 收藏帖子 */
        public static final int COLLECT_POST = 2;
        /** 点赞评论 */
        public static final int LIKE_COMMENT = 3;
    }

    /**
     * 权限类型常量
     * 帖子级别的用户权限控制
     */
    public static class PermissionType {
        /** 禁止评论（禁言） */
        public static final int BLOCK_COMMENT = 1;
        /** 只看此人评论 */
        public static final int VIEW_ONLY = 2;
    }

    /**
     * 黑名单关系类型常量
     * 定义用户间的屏蔽关系
     */
    public static class BlacklistRelationType {
        /** 单向拉黑（仅拉黑方屏蔽被拉黑方） */
        public static final int ONE_WAY = 1;
        /** 双向拉黑（互相屏蔽） */
        public static final int TWO_WAY = 2;
    }

    /**
     * 黑名单拉黑类型常量
     * 定义不同程度的屏蔽策略
     */
    public static class BlacklistBlockType {
        /** 隐藏内容（仅不显示该用户的内容） */
        public static final int HIDE_CONTENT = 1;
        /** 完全屏蔽（彻底隔离，包括互动） */
        public static final int FULL_BLOCK = 2;
    }

    /**
     * 状态常量
     * 通用的启用/禁用状态
     */
    public static class Status {
        /** 禁用/已解除 */
        public static final int DISABLED = 0;
        /** 启用/正常 */
        public static final int ENABLED = 1;
    }

    /**
     * 媒体类型常量
     */
    public static class MediaType {
        /** 图片 */
        public static final int IMAGE = 1;
        /** 视频 */
        public static final int VIDEO = 2;
    }

    /**
     * 排序类型常量
     */
    public static class SortType {
        /** 最新 */
        public static final String NEW = "new";
        /** 最热 */
        public static final String HOT = "hot";
        /** 置顶 */
        public static final String TOP = "top";
    }
}
