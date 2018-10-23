package com.syxgo.electrombile.http;

/**
 * Created by tangchujia on 2017/7/20.
 */

public class HttpUrl {
//private static final String DEFAULT_DOMAIN = "http://dev.ops.syxgo.com";
    private static final String DEFAULT_DOMAIN = "http://ops.syxgo.com";
//private static final String DEFAULT_DOMAIN = "http://ops.intl.syxgo.com";
//private static final String DEFAULT_DOMAIN = "http://dev.ops.intl.syxgo.com";
//private static final String DEFAULT_DOMAIN = "http://sl.ops.syxgo.com";

    public static boolean isDebug = true;
    public static int COUNT = 10;

    /**
     * c
     * 获取验证码
     * phone 手机号
     * ********JSON POST请求********
     */
    public static final String GET_CODE = DEFAULT_DOMAIN + "/smsCode";

    /**
     * 注册登录
     * phone 手机号
     * code: 手机得到的验证码
     * noncestr: 登录发送短信请求中得到的随机串
     * ********JSON POST请求********
     */
    public static final String REGISTER_LOGIN = DEFAULT_DOMAIN + "/login";
    /**
     * 创建ECU
     * ********JSON POST请求********
     */
    public static final String CREATE_ECU = DEFAULT_DOMAIN + "/admin/ecus";

    /**
     * 创建电池
     * ********JSON POST请求********
     */
    public static final String CREATE_BATTERY = DEFAULT_DOMAIN + "/admin/batterys";
    /**
     * 绑定电池
     * ********JSON put请求********
     */
    public static final String BIND_BATTERY = DEFAULT_DOMAIN + "/admin/batterys";
    /**
     * 创建车辆
     * ********JSON POST请求********
     */
    public static final String CREATE_BIKE = DEFAULT_DOMAIN + "/admin/bikes";
    /**
     * 查询车辆
     * ********JSON GET请求********
     */
    public static final String SEARCH_RIDES = DEFAULT_DOMAIN + "/admin/rides";
    /**
     * 查询用户
     * ********JSON GET请求********
     */
    public static final String SEARCH_USERS = DEFAULT_DOMAIN + "/admin/users";
    /**
     * 查询行程
     * ********JSON GET请求********
     */
    public static final String SEARCH_BIKE = DEFAULT_DOMAIN + "/admin/bikes";
    /**
     * 绑定ECU
     * ********JSON put请求********
     */
    public static final String BIND_ECU = DEFAULT_DOMAIN + "/admin/ecus";
    /**
     * 绑定ECU
     * ********JSON get请求********
     */
    public static final String SEARCH_ECU = DEFAULT_DOMAIN + "/admin/ecu";
    /**
     * 绑定车辆
     * ********JSON post请求********
     */
    public static final String BIND_BIKE = DEFAULT_DOMAIN + "/admin/bikes/bind";
    /**
     * 绑定车辆
     * ********JSON post请求********
     */
    public static final String UNBIND_BIKE = DEFAULT_DOMAIN + "/admin/bikes/unbind";
    /**
     * 创建停车点
     * ********JSON post请求********
     */
    public static final String SET_STATION = DEFAULT_DOMAIN + "/admin/stations";

    /**
     * 查询附近停车点
     * ********JSON get请求********
     */
    public static final String SEARCH_STATION_NEARBY = DEFAULT_DOMAIN + "/admin/stations/nearby?lng=%f&lat=%f&distance=%d";
    /**
     * 删除停车点
     * ********JSON delete请求********
     */
    public static final String DELETE_STATION = DEFAULT_DOMAIN + "/admin/stations";

    /**
     * 查询停车点
     * ********JSON get请求********
     */
    public static final String SEARCH_STATION = DEFAULT_DOMAIN + "/admin/stations";
    /**
     * 操作车辆 put
     */
    public static final String GET_RPC = DEFAULT_DOMAIN + "/staff/bike/rpc";

    /**
     * 获取个人信息 get
     */
    public static final String GET_STAFF = DEFAULT_DOMAIN + "/admin/staffs";
    /**
     * 修改密码
     * PASSWORD 用户新密码
     * ********JSON POST请求********
     */
    public static final String CHANGE_PWD = DEFAULT_DOMAIN + "/changePassword";
    /**
     * 查询订单
     * user_id
     * order_type int 订单类型（1:押金 2:充值 3:消费)
     * page	int	第几页数据(从1开始)
     * per_page	int	每页多少条记录
     */
    public static final String GET_ORDERS = DEFAULT_DOMAIN + "/admin/orders";
    /**
     * 结束行程 POST
     * ride_id int
     * user_id int
     * fee int
     */
    public static final String RIDE_FINISH = DEFAULT_DOMAIN + "/admin/ride/finish";
    /**
     * 查询行程
     * ride_id
     * page	int	第几页数据(从1开始)
     * per_page	int	每页多少条记录
     */
    public static final String GET_RIDES = DEFAULT_DOMAIN + "/admin/rides";
    /**
     * 查询用户
     * user_id
     * page	int	第几页数据(从1开始)
     * per_page	int	每页多少条记录
     */
    public static final String GET_USERS = DEFAULT_DOMAIN + "/admin/users";
    /**
     * 黑名单 POST
     * user_id int
     * action	string	block、unblock
     */
    public static final String USER_BLOCK = DEFAULT_DOMAIN + "/admin/users/block";
    /**
     * 退押金 POST
     * user_id int
     */
    public static final String REFUND_DEPOSIT = DEFAULT_DOMAIN + "/admin/refund/deposit";
    /**
     * 退余额 POST
     * user_id int
     */
    public static final String REFUND_BALANCE = DEFAULT_DOMAIN + "/admin/refund/balance";
    /**
     * 添加用户赠送余额 post
     * user_id	int	用户编号
     * gift_balance	int	增加金额
     * subject string 详情描述
     */
    public static final String ADD_BALANCE = DEFAULT_DOMAIN + "/admin/users/add_balance";

    /**
     * 用户扣费 post
     * user_id	int	用户编号
     * real_balance	int	增加扣费金额
     * subject string 详情描述
     */
    public static final String SUB_BALANCE = DEFAULT_DOMAIN + "/admin/users/sub_balance";
    /**
     * 查询行程
     * ride_id
     * page	int	第几页数据(从1开始)
     * per_page	int	每页多少条记录
     */
    public static final String GET_BIKES = DEFAULT_DOMAIN + "/admin/bikes";
    /**
     * 查询附近停车点 get
     * lng	float	经度
     * lat	float	纬度
     * distance	int	查询的距离范围
     */
    public static final String STATION_NEARBY = DEFAULT_DOMAIN + "/admin/stations/nearby";
    /**
     * 查询车辆轨迹 get
     * lng	float	经度
     * lat	float	纬度
     */
    public static final String GET_ORBIT = DEFAULT_DOMAIN + "/admin/bike/locations";
    /**
     * 上线车辆 POST
     * bike_id int
     */
    public static final String BIKE_ONLINE = DEFAULT_DOMAIN + "/admin/bikes/online";
    /**
     * 下线车辆 POST
     * bike_id int
     */
    public static final String BIKE_OFFLINE = DEFAULT_DOMAIN + "/admin/bikes/offline";
    /**
     * 操作车辆 POST
     * lock、unlock、beep、lock_backseat、unlock_backseat
     */
    public static final String BIKE_RPC = DEFAULT_DOMAIN + "/admin/bikes/rpc";

    /**
     * 控制ECU[post]
     * ecu_id
     * action(lock unlock beep unlock_backseat)
     */
    public static final String ECU_RPC = DEFAULT_DOMAIN + "/admin/ecus/rpc";
    /**
     * 绑定车辆
     * ********JSON post请求********
     */
    public static final String STAFF_BIND_BIKE = DEFAULT_DOMAIN + "/staff/bikes/bind";
    /**
     * 绑定车辆
     * ********JSON post请求********
     */
    public static final String STAFF_UNBIND_BIKE = DEFAULT_DOMAIN + "/staff/bikes/unbind";
    /**
     * 绑定电池
     * ********JSON put请求********
     */
    public static final String STAFF_BIND_BATTERY = DEFAULT_DOMAIN + "/staff/batterys";
}
