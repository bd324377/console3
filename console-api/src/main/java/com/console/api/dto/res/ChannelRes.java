package com.console.api.dto.res;

import com.console.payment.model.dto.ChannelDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChannelRes {
    private List<ChannelDTO> channels;

    public void buildPaymentChannels(List<ChannelDTO> channelList) {
        channels = channelList.stream().filter(item -> item.getPayStatus() == 1).map(item ->{
            ChannelDTO channelDTO = new ChannelDTO();
            channelDTO.setId(item.getId());
            channelDTO.setIcon(item.getIcon());
            channelDTO.setShowName(item.getShowName());
            channelDTO.setMinPayAmt(item.getMinPayAmt());
            channelDTO.setMaxPayAmt(item.getMaxPayAmt());
            channelDTO.setRealNamePayStatus(item.getRealNamePayStatus());
            channelDTO.setPayAmtArr(item.getPayAmtArr());
            return channelDTO;
        }).toList();
    }

    public void buildCashoutChannels(List<ChannelDTO> channelList) {
        channels = channelList.stream().filter(item -> item.getCashOutStatus() == 1).map(item ->{
            ChannelDTO channelDTO = new ChannelDTO();
            channelDTO.setId(item.getId());
            channelDTO.setIcon(item.getIcon());
            channelDTO.setShowName(item.getShowName());
            channelDTO.setMinCashOutAmt(item.getMinCashOutAmt());
            channelDTO.setMaxCashOutAmt(item.getMaxCashOutAmt());
            channelDTO.setCashOutAmtArr(item.getPayAmtArr());
            return channelDTO;
        }).toList();
    }
}
