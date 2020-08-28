package com.xin.jvm.jvm02;

/**
 * github 更多操作
 *  1. 常用词汇含义
 *      watch : 持续追踪该项目
 *      fork : 复制项目到自己的github仓库
 *      star : 点赞
 *      clone : 下载项目到本地
 *      follow : 关注项目作者
 *
 *  2. in 精确搜索查询
 *      xxx关键词 in:name或description或readme
 *      seckill in:name  -- 项目名包含seckill
 *      seckill in:name,readme  -- 项目名和readme 包含seckill
 *      seckill in:name,readme,description
 *
 *  3. stars 或 fork数量查找
 *      xxx关键词 stars 通配符(:> :>=) 或者区间数字  xxx关键词 stars 1..3
 *      springboot stars :>5000 -- 点赞大于5000的spring boot项目
 *      springboot forks :>500 -- fork大于500的spring boot项目
 *      springboot stars:100..500 forks:>50 -- fork大于50 且 stars在100-500 的spring boot项目
 *
 *  4. awesome 加强搜索
 *      awesome 关键字 : 收集框架 / 技术 / 教程 ...
 *
 *  5. url#L行号 高亮显示一行代码  url#L行号L行号 高亮显示一段代码
 *
 *      https://github.com/codingXiaxw/seckill/blob/master/src/main/java/cn/codingxiaxw/dto/Exposer.java#L9 高亮显示该项目的第九行
 *      https://github.com/codingXiaxw/seckill/blob/master/src/main/java/cn/codingxiaxw/dto/Exposer.java#L28L32  高亮显示28-32行
 *
 *  6. T搜索 项目内搜索
 *      项目内按t
 *
 *  7. location 地区 language 语言 ： 查看某个地区的活跃用户
 */
public class GitHubOper {

    public static void main(String[] args) {

    }
}
