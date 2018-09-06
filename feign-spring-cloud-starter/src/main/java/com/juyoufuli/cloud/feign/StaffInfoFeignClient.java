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

@FeignClient(value = Constant.STAFFCENTER_SERVICE_NAME, configuration = FeignConfiguration.class, fallbackFactory = StaffInfoFeignClient.StaffInfoHystrixFallbackFactory.class)
public interface StaffInfoFeignClient {

    @RequestMapping(value = "info/syncDataBase", method = RequestMethod.GET)
    ResultBean syncDataBase();

    @Component
    static class StaffInfoHystrixFallbackFactory implements FallbackFactory<StaffInfoFeignClient> {

        private static final Logger LOGGER = LoggerFactory.getLogger(StaffInfoHystrixFallbackFactory.class);

        @Override
        public StaffInfoFeignClient create(Throwable cause) {
            StaffInfoHystrixFallbackFactory.LOGGER.info("fallback; reason was: {}", cause.getMessage());
            return new StaffInfoFeignClient() {
                @Override
                public ResultBean syncDataBase() {
                    return ResultBean.error(cause.getMessage());
                }
            };
        }
    }
}
