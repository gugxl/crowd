package com.gugu.crowd.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users") // 对应数据库表
public class Users {
    @TableId
    private Long id;
    private String name;
    private Integer age;
}
