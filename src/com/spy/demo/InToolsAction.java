package com.spy.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author spy
 * @date 2020-04-19 16:49
 */
public class InToolsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        new ConfirmDialog(e.getProject(), true).show();
    }
}
