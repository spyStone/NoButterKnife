package com.spy.demo.impl;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.spy.demo.NoButterKnifeService;
import com.spy.demo.worker.NoButterKnife;

/**
 * @author spy
 * @date 2020-04-19 16:40
 */
public class NoButterKnifeServiceImpl implements NoButterKnifeService {

    private NoButterKnife noButterKnife = new NoButterKnife();;

    @Override
    public void init() {
        System.out.println("NoButterKnifeServiceImpl.init");
    }

    @Override
    public void start(Project project) {
        if (null != project) {
            if (null == noButterKnife) {
                noButterKnife = new NoButterKnife();
            }
            noButterKnife.searchToModify(project.getBasePath());
            NotificationGroup notificationGroup = new NotificationGroup("NoButterKnifeService", NotificationDisplayType.BALLOON, true);
            Notification notification = notificationGroup.createNotification("handling project:" + project.getName(), MessageType.INFO);
            Notifications.Bus.notify(notification);
        }
    }

    @Override
    public void start(String file) {
        if (null == noButterKnife) {
            noButterKnife = new NoButterKnife();
        }
        noButterKnife.searchToModify(file);
        NotificationGroup notificationGroup = new NotificationGroup("NoButterKnifeService", NotificationDisplayType.BALLOON, true);
        Notification notification = notificationGroup.createNotification("handling file:" +file, MessageType.INFO);
        Notifications.Bus.notify(notification);
    }
}
