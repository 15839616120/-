package com.juyoufuli.cloud.feign;

import com.juyoufuli.cloud.autoconfigure.FeignConfiguration;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.result.ResultBean;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = Constant.STAFFCENTER_SERVICE_NAME, configuration = FeignConfiguration.class, fallbackFactory = StaffGroupFeignClient.StaffGroupHystrixFallbackFactory.class)
public interface StaffGroupFeignClient {

    @RequestMapping(value = "staffGroup/syncStaffGroupToMysql", method = RequestMethod.GET)
    ResultBean syncStaffGroupToMysql(@RequestParam(value = "type" ,required = true) Integer type);

//    ResultBean syncStaffGroupToRedis();



    @Component
    static class StaffGroupHystrixFallbackFactory implements FallbackFactory<StaffGroupFeignClient> {

        private static final Logger LOGGER = LoggerFactory.getLogger(StaffGroupHystrixFallbackFactory.class);

        @Override
        public StaffGroupFeignClient create(Throwable cause) {
            StaffGroupHystrixFallbackFactory.LOGGER.info("fallback; reason was: {}", cause.getMessage());
            return new StaffGroupFeignClient() {

                @Override
                public ResultBean syncStaffGroupToMysql(Integer type) {
                    return ResultBean.error(cause.getMessage());
                }

//                @Override
//                public ResultBean syncStaffGroupToRedis() {
//                    return ResultBean.error(cause.getMessage());
//                }
            };
        }
    }

}
