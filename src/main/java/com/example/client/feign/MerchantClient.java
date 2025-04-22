package com.example.client.feign;

import com.example.client.dto.MerchantFullDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "merchantClient", url = "${merchant.service.url}")
public interface MerchantClient {

    @GetMapping("/merchants/{id}")
    MerchantFullDTO findMerchantById(@PathVariable("id") String id);

    @PutMapping("/merchants/{id}")
    MerchantFullDTO updateMerchant(@PathVariable String id, @RequestBody MerchantFullDTO merchantFullDTO);
}