package com.fmisser.gtc.social.controller;

import com.alibaba.fastjson.JSON;
import com.fmisser.fpp.cache.redis.service.RedisService;
import com.fmisser.fpp.oss.minio.service.MinioService;
import com.fmisser.gtc.base.dto.im.ImMsgFactory;
import com.fmisser.gtc.base.dto.im.ImSendMsgDto;
import com.fmisser.gtc.social.domain.DynamicComment;
import com.fmisser.gtc.social.mq.WxWebHookBinding;
import com.fmisser.gtc.social.repository.BlockRepository;
import com.fmisser.gtc.social.repository.DynamicCommentRepository;
import com.fmisser.gtc.social.repository.DynamicHeartRepository;
import com.fmisser.gtc.social.repository.FollowRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(description = "TEST API")
@RequestMapping("/test")
@RestController
@AllArgsConstructor
public class TestController {

    private final WxWebHookBinding wxWebHookBinding;
//    private final OssService ossService;
    private final MinioService minioService;
    private final  RedisService redisService;

    private final RedisTemplate<String, Object> redisTemplate;

    private  final  DynamicCommentRepository dynamicCommentRepository;
    private  final DynamicHeartRepository dynamicHeartRepository;
    private final FollowRepository followRepository;
    private final BlockRepository blockRepository;

    @GetMapping("/slow")
    public Object slowTest() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ok";
    }

    @PostMapping("/post-msg")
    public Object postMsg() {
        String message = String.format("1,%d,%d", 933, 22);
        Message<String> tipMsg = MessageBuilder.withPayload(message).build();
        return wxWebHookBinding.wxWebHookOutputChannel().send(tipMsg);
    }

    @ApiOperation("fpp-test")
    @GetMapping("fpp-test")
    public String fppTest() {
        return minioService.getName();
    }

    @ApiOperation("redis-test")
    @GetMapping("redis-test")
    public Object redisTest() {
        redisService.listLeftPush("test-object",
                ImMsgFactory.buildCallMsg("123", "456", "789", "000", false, false));
        ImSendMsgDto msgDto = (ImSendMsgDto) redisService.listRightPop("test-object");

        redisService.set("111","1",65);
        return msgDto.getFrom_Account();
    }

    @ApiOperation("redis-set")
    @PostMapping("redis-set")
    public int setRedis(){
        //往redis中写入评论数据
        List<DynamicComment> commentList=dynamicCommentRepository.findAll();
        List<Object> results= redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (DynamicComment dynamicComment:commentList) {
                    DynamicComment comment=new DynamicComment();
                    comment.setCreateTime(dynamicComment.getCreateTime());
                    comment.setDynamicId(dynamicComment.getDynamicId());
                    comment.setContent(dynamicComment.getContent());
                    comment.setUserIdFrom(dynamicComment.getUserIdFrom());
                    comment.setUserIdTo(dynamicComment.getUserIdTo());
                    comment.setId(dynamicComment.getId());
                    redisService.set("binlog:tdmq:gtc-social-db:t_dynamic_comment:"+comment.getId(), JSON.toJSONString(comment),2592000);
                }
                return null;
            }
        });
//     //往redis中写入点赞数据
//        List<DynamicHeart> heartList=dynamicHeartRepository.findAll();
//        List<Object> results2= redisTemplate.executePipelined(new RedisCallback<String>() {
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                for (DynamicHeart dynamicHeart:heartList) {
//                    redisService.set("binlog:tdmq:gtc-social-db:t_dynamic_heart:"+dynamicHeart.getId(),JSON.toJSONString(dynamicHeart),2592000);
//               }
//                return null;
//            }
//        });
////     // 往redis中写入关注的数据
//        List<Follow> followList=followRepository.findAll();
//       List<Object> results3= redisTemplate.executePipelined(new RedisCallback<String>() {
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//                for (Follow follow:followList) {
//                    redisService.set("binlog:tdmq:gtc-social-db:t_follow:"+follow.getId(),JSON.toJSONString(follow),2592000);
//                }
//                return null;
//           }
//        });
//     //往redis中写入屏蔽的数据
//        List<Block> blockList=blockRepository.findAll();
//        List<Object> results4= redisTemplate.executePipelined(new RedisCallback<String>() {
//            @Override
//            public String doInRedis(RedisConnection connection) throws DataAccessException {
//               for (Block block:blockList) {
//                    redisService.set("binlog:tdmq:gtc-social-db:t_block:"+block.getId(),JSON.toJSONString(block),2592000);
//                }
//                return null;
//            }
//        });

        return 1;
    }

    /**
     * 判断字符串相似
     *
     * @param before
     *            比较串
     * @param latest
     *            被比较串
     * @return
     */
    @ApiOperation("compare-str")
    @PostMapping("compare-str")
    public static String  isLike(@RequestHeader(value = "before", required = false)String before, @RequestHeader(value = "latest", required = false)String latest) {
        before="000313.865c51bb4c9746cbbba7da14423aa585.1446\n";
        latest="000313.865c51bb4c9746cbbba7da14423aa585.1446";
        if (before.equals(latest)) {return "";}
        String[] s1 = before.split("@");
        String[] s2 = latest.split("@");
        StringBuilder contrastData = new StringBuilder();
        Map<String, String> pack_before = new HashMap<String, String>();
        Map<String, String> pack_latest = new HashMap<String, String>();
        for (String temp: s1) {
            if (temp.length() == 0) { continue; }
            pack_before.put(temp.split("#")[0], temp.split("#")[1]);
        }
        for (String temp: s2) {
            if (temp.length() == 0) { continue; }
            pack_latest.put(temp.split("#")[0], temp.split("#")[1]);
        }
        for (Map.Entry e: pack_before.entrySet()) {
            if(pack_latest.containsKey(e.getKey())) {
                if (! pack_latest.get(e.getKey()).equals(e.getValue())) {
                    contrastData.append("P:" + e.getKey() + "\n");
                    List<String> beforeList = new ArrayList<>();
                    List<String> latestList = new ArrayList<>();
                    String[] before_str = e.getValue().toString().split("\\|");
                    String[] latest_str = pack_latest.get(e.getKey()).toString().split("\\|");
                    for (String temp: before_str){
                        beforeList.add(temp);
                    }
                    for (String temp: latest_str){
                        latestList.add(temp);
                    }
                    // 将后一次中存在前一次的所有元素删除
                    latestList.removeAll(beforeList);

                    for (String latestTemp : latestList) {
                        String getClass = latestTemp.split("=")[0];
                        boolean flag = true;
                        for (String beforeTemp: beforeList) {
                            if (beforeTemp.split("=")[0].equals(getClass)) {
                                flag = false;
                            }
                            if (contrastData.toString().contains(latestTemp)) {continue;}
                            if (flag) {
                                contrastData.append("NC:" + latestTemp + "\n");
                            } else {
                                contrastData.append("B:" + beforeTemp + "-L:" + latestTemp + "\n");
                                flag = true;
                            }
                        }
                    }
                }
            } else {
                System.out.println("already deleted!!" + e.getKey());
            }
        }
        return contrastData.toString();
    }


}
