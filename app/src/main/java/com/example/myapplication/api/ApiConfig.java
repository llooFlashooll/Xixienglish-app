package com.example.myapplication.api;

public class ApiConfig {
    public static final int PAGE_SIZE = 5;
    // 请求的接口
    public static final String BASE_URl = "http://192.168.31.32:8080/renren-fast";
    public static final String LOGIN = "/app/login"; // 登录
    public static final String REGISTER = "/app/register"; // 注册
    public static final String VIDEO_LIST = "/app/videolist/list"; // 所有视频列表
    public static final String VIDEO_LIST_BY_CATEGORY = "/app/videolist/getListByCategoryId"; // 各类型视频列表
    public static final String VIDEO_CATEGORY_LIST = "/app/videocategory/list"; // 视频类型列表

}
