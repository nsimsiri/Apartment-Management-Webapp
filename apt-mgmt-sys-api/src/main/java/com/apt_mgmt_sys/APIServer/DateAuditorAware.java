package com.apt_mgmt_sys.APIServer;

import org.springframework.data.domain.AuditorAware;

import java.util.Date;

public class DateAuditorAware implements AuditorAware<Date> {
    @Override
    public Date getCurrentAuditor(){
        return new Date();
    }
}
