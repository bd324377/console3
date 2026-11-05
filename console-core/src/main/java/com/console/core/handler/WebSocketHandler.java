//package com.console.core.handler;
//
//import com.alibaba.fastjson.JSON;
//import com.console.framework.domain.SocketMsg;
//import com.console.framework.utils.EncryptUtils;
//import com.console.framework.utils.RedisUtils;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//import java.util.concurrent.*;
//
//@Slf4j
//@Component
//public class WebSocketHandler extends TextWebSocketHandler {
//    private static final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();//tenantCode_userId:webSocketSession
//    private static final Map<String, String> userIdMap = new ConcurrentHashMap<>();//sessionId:tenantCode_userId
//
//    @PostConstruct
//    private void init() {
//        // 订阅消息通道
////        RedisConnection connection = RedisUtils.getRedisConnection();
////        connection.subscribe((message, pattern) -> {
////            // 异步处理，避免阻塞订阅线程
////            String msg = new String(message.getBody());
////            RedisPublishMsg redisPublishMsg = JSON.parseObject(msg,RedisPublishMsg.class);
////            CompletableFuture.runAsync(() -> sendSocketMsg(redisPublishMsg));
////        }, "websocket-channel".getBytes());
//
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8, Executors.defaultThreadFactory());
//        scheduler.scheduleAtFixedRate(this::heartBeat, 0, 15, TimeUnit.SECONDS);
//    }
//
//    /**
//     * 心跳
//     */
//    private void heartBeat() {
//        try {
//            String totalOrderAmtKey = "ORDER_AMT:TOTAL_AMT";
//            Random random = new Random();
//            // 生成5000内的随机数字
//            double incrementAmt = random.nextDouble(5000);
//            if (!RedisUtils.exists(totalOrderAmtKey)) {
//                double initAmt = 5000000000.65;
//                RedisUtils.setValue(totalOrderAmtKey,initAmt);
//            }
//            BigDecimal orderAmt = BigDecimal.valueOf(RedisUtils.incrementOfDouble(totalOrderAmtKey,incrementAmt)).setScale(2,RoundingMode.HALF_UP);
//            SocketMsg socketMsg = SocketMsg.heartBeat();
//            socketMsg.setData(orderAmt);
//            if (sessionMap.values().isEmpty()) {
//                return;
//            }
//            sessionMap.values().forEach(session -> {
//                if (session.isOpen()) {
//                    try {
//                        session.sendMessage(new TextMessage(socketMsg.toJsonString()));
//                    } catch (IOException e) {
//                        log.error("发送心跳消息异常:",e);
////                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            log.error("心跳发送异常:{}",e.getMessage());
//        }
//
//    }
//
//    /**
//     * 发送websocket消息
//     * @param redisPublishMsg 消息
//     */
//    public static void sendSocketMsg(RedisPublishMsg redisPublishMsg) {
//        try {
//            String lockKey = "REDIS_PUBLISH_MSG:" + redisPublishMsg.getTenantCode() + ":" + redisPublishMsg.getId();
//            if (RedisUtils.getAcquireLock(lockKey,3)) {
//                if (redisPublishMsg.getUserId() != null) {//单独发送
//                    WebSocketSession webSocketSession = sessionMap.get(redisPublishMsg.buildSessionKey());
//                    if (webSocketSession != null) {//session存在则继续发送消息
//                        try {
//                            webSocketSession.sendMessage(new TextMessage(redisPublishMsg.getSocketMsg().toJsonString()));
//                        } catch (IOException e) {
//                            sessionMap.remove(redisPublishMsg.buildSessionKey());
//                            userIdMap.remove(webSocketSession.getId());
//                            throw new RuntimeException(e);
//                        }
//                    }
//                } else {//公告
//                    sessionMap.values().forEach(session -> {
//                        try {
//                            if (session.isOpen()) {
//                                session.sendMessage(new TextMessage(redisPublishMsg.getSocketMsg().toJsonString()));
//                            }
//                        } catch (IOException e) {
//                            sessionMap.remove(redisPublishMsg.buildSessionKey());
//                            userIdMap.remove(session.getId());
//                        }
//                    });
//                }
//            }
//        } catch (Exception e) {
//            log.error("发送websocket推送时错误：{}",e.getMessage());
//        }
//
//    }
//
//    /**
//     * 将消息推送到redis订阅频道中
//     * @param redisPublishMsg 待发送消息
//     */
//    public static void publishWebSocketMsg(RedisPublishMsg redisPublishMsg) {
//        RedisMqMsg redisMqMsg = new RedisMqMsg();
//        redisMqMsg.setMsgData(redisPublishMsg);
//        redisMqMsg.setMsgType("WEBSOCKET");
//        RedisUtils.sendMqMsg(redisMqMsg.toObjectMap());
//    }
//
//    /**
//     * 退出websocket
//     * @param sessionKey    session连接key值 tenantCode_userId
//     */
//    public static void logOutWebSocket(String sessionKey) {
//        WebSocketSession session = sessionMap.remove(sessionKey);
//        if (session != null) {
//            userIdMap.remove(session.getId());
//            if (session.isOpen()) {
//                try {
//                    session.close();
//                } catch (IOException e) {
//                    log.info("用户{}，websocket退出登录异常：{}", sessionKey, e.getMessage());
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 连接成功后触发事件(给前端发送初始化消息)
//     * @param session       websocke链接session
//     * @throws Exception    异常信息
//     */
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        SocketMsg msg = new SocketMsg();
//        msg.setType("init");
//        msg.setMsg(session.getId());
//        session.sendMessage(new TextMessage(msg.toJsonString()));
//    }
//
//    /**
//     * 收到消息后处理函数
//     * @param session       websocket连接session
//     * @param message       接收到的消息
//     * @throws Exception    异常信息
//     */
//    @Override
//    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
//        SocketMsg msg = JSON.parseObject(message.getPayload(), SocketMsg.class);
//        if (msg.getType().equals("online")) {//在线消息——初始化
//            String token = msg.getToken();
//            if (StringUtils.hasText(token)) {
//                return;
//            }
//            String tokenKey   = EncryptUtils.decrypt(token, null);
//            RequestMember requestMember = RedisUtils.get(tokenKey, RequestMember.class);
//            if (requestMember == null) {
//                return;
//            }
//            String tenantCode = tokenKey.split(":")[1];
//            String userId = tokenKey.substring(tokenKey.lastIndexOf(":") + 1).split("_")[0];
//
//            sessionMap.put(tenantCode + "_" + userId,session);
//            userIdMap.put(session.getId(),tenantCode + "_" + userId);
//
//            SocketMsg successMsg = new SocketMsg("success","success");
//            session.sendMessage(new TextMessage(successMsg.toJsonString()));
//
//
////            log.info("requestMember is :{}",JSON.toJSONString(requestMember));
//            try {
//                UserEvent userEvent = new UserEvent(requestMember,UserEvent.EventType.BUILD_USER_UNREWARD_DATA.getKey());
//                SpringUtils.publishEvent(userEvent);
////                    DynamicTableNameHandler.setTenantCode(requestMember.getTenantCode());
////                    requestMember = userAPIService.fullRequestMemberInfo(requestMember);
////                    TokenUtils.updateTokenData(requestMember);
////                    SocketMsg unRewardPushMsg = new SocketMsg("unRewardPush", requestMember.getUnRewardData());
////                    session.sendMessage(unRewardPushMsg.toTextMessage());
//            } catch (Exception e) {
//                log.info("发送未领取奖励推送失败，异常原因：{}",e.getMessage());
//            } finally {
//                DynamicTableNameHandler.removeTenantCode();
//            }
//        } else if (msg.getType().equals("queryLotteryPool")) {//查询奖池金额
//            String tokenAndUserId = userIdMap.get(session.getId());//查询系统奖池金额
////            userIdMap.put(session.getId(),tenantCode + "_" + userId);
//            if (StrUtils.isNotBlank(tokenAndUserId) && tokenAndUserId.split("_").length >= 2) {
//                String tenantCode = tokenAndUserId.split(":")[0];
//
//                String loteryPoolAmtKey = ActivityConstants.LOTTERY_ACTIVITY_PRIZE_AMT_POOL + tenantCode + ":" + ActivityConstants.LOTTERY_ACTIVITY_ID;
//                BigDecimal prizePoolAmt = RedisUtils.get(loteryPoolAmtKey,BigDecimal.class);
//                if (prizePoolAmt == null) prizePoolAmt = new BigDecimal("0");
//                prizePoolAmt = prizePoolAmt.multiply(BigDecimal.valueOf(1000));
//                double randomNumber = ThreadLocalRandom.current().nextDouble(100.00, 1000.99);
//                prizePoolAmt = prizePoolAmt.add(BigDecimal.valueOf(randomNumber));
//                Map<String,Object> lotteryPoolAmtData = new HashMap<>();
//                lotteryPoolAmtData.put("lotteryPoolAmt",prizePoolAmt.setScale(2, RoundingMode.HALF_UP).toString());
//                SocketMsg prizePoolAmtMsg = new SocketMsg("updateLotteryPool", lotteryPoolAmtData);
//                session.sendMessage(new TextMessage(prizePoolAmtMsg.toJsonString()));
//            }
//        }
//    }
//
//    /**
//     * 连接关闭处理方法
//     * @param session       连接session
//     * @param status        关闭状态
//     */
//    @Override
//    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
//        try {
//            if (session.isOpen()) {
//                String userId = userIdMap.get(session.getId());
//                userIdMap.remove(session.getId());
//                if (StrUtils.isNotBlank(userId)) sessionMap.remove(userId);
//            }
//        } catch (Exception e) {
//            log.info("关闭session失败，原因 is :",e);
//        }
//
//    }
//}
