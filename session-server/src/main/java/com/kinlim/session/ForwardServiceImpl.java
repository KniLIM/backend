package com.kinlim.session;

import com.knilim.service.ForwardService;
import com.knilim.utils.Tuple;

import org.apache.dubbo.config.annotation.Service;

import java.util.UUID;

@Service
public class ForwardServiceImpl implements ForwardService {

    @Override
    public Tuple<String, Integer> getAvailableSession() {
        return null;
    }

    @Override
    public void forward(UUID rcvId, Byte[] data) {

    }
}
