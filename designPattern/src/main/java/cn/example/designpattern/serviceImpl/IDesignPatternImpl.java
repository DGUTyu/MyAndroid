package cn.example.designpattern.serviceImpl;


import com.google.auto.service.AutoService;

import cn.example.router.service.IDesignPattern;
import cn.example.router.utils.ActivityLaunchUtils;
import cn.example.designpattern.DesignPatternMainActivity;

/**
 * 创建实现类并添加 @AutoService 注解
 */
@AutoService({IDesignPattern.class})
public class IDesignPatternImpl implements IDesignPattern {
    @Override
    public void goDesignPatternPage() {
        ActivityLaunchUtils.INSTANCE.launchByApplication(DesignPatternMainActivity.class);
    }
}