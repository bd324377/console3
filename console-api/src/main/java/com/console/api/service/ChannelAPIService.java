package com.console.api.service;

import com.console.api.dto.res.ChannelRes;

public interface ChannelAPIService {
    ChannelRes getPaymentChannels();
    ChannelRes getCashOutChannels();
}
