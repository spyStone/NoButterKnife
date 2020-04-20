package com.spy.demo;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

/**
 * @author spy
 * @date 2020-04-19 16:40
 */
public interface NoButterKnifeService {
    static NoButterKnifeService getInstance(Project project) {
//        if (null != project) {
//            ServiceManager.getService(project, NoButterKnifeService.class);
//        }
        return ServiceManager.getService(NoButterKnifeService.class);
    }

    public void init();

    public void start(Project project);
    public void start(String file);
}
